package SMTP;

import Commun.*;
import com.sun.xml.internal.bind.v2.TODO;

import javax.rmi.CORBA.Util;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static SMTP.ReponseServeur.*;

public class ObjetSmtpConnecte {

    protected Tcp tcp;
    private String etatServeur;
    private boolean continuer;
    private String input;
    private String reponseServeur;
    private Email currentEmail;
    protected RepertoireUtilisateur repertoireUtilisateur;
    protected ArrayList<Email> m_listeEmails;
    protected Utilisateur currentUser;

    public ObjetSmtpConnecte(Tcp tcp){
        this.tcp = tcp;
        this.etatServeur = SERVER_READY;
        this.continuer = true;
        this.currentEmail = null;
        this.m_listeEmails = new ArrayList<>();
        this.repertoireUtilisateur = new RepertoireUtilisateur();
        loadMails();
    }

    public void Launch() throws IOException {
        this.etatServeur = SERVER_CONNEXION;
        tcp.send(SMTP_SERVER_READY);

        while(continuer){
            System.out.println("Wait...");
            input = tcp.receive();
            System.out.println(input + " received");

            String[] explodedCommand = input.split(" ", 2);
            String command = etatServeur == SERVER_LECTURE ? explodedCommand[0] : explodedCommand[0].toUpperCase();
            String[] parameters = new String[0];

            if (!etatServeur.equals(SERVER_LECTURE)) {
                if(explodedCommand.length > 1) {
                    parameters = explodedCommand[1].split(" ");
                }
            } else {
                StringBuilder sb = new StringBuilder();
                for (String line: explodedCommand
                     ) {
                    sb.append(line);
                }
                parameters = new String[]{sb.toString()};
            }
            switch (etatServeur) {
                case SERVER_CONNEXION:
                    reponseServeur = this.connexion(command, parameters);
                    break;
                case SERVER_IDENTIFICATION:
                    reponseServeur = this.identification(command, parameters);
                    break;
                case SERVER_TRANSACTION:
                    reponseServeur = this.transaction(command, parameters);
                    break;
                case SERVER_ENVOIE:
                    reponseServeur = this.envoie(command, parameters);
                    break;
                case SERVER_LECTURE:
                    reponseServeur = this.lecture(command, parameters);
                    break;
                default:
                    reponseServeur = SMTP_500_UNKNOWN_COMMAND;
            }
            System.out.println("reponse: "+reponseServeur);
            tcp.send(reponseServeur);
        }
        System.out.println("End of POP3");
    }

    /*
        Etats
     */

    /**
     * return answer of command related to connexion stat
     * @param command
     * @param parameters
     * @return answer
     */
    private String connexion(String command, String[] parameters) {
        switch (command){
            case "EHLO":
                return commandeEhlo(parameters);
            case "QUIT":
                return commandeQuit();
            default :
                return SMTP_500_UNKNOWN_COMMAND;
        }
    }

    /**
     * return answer of commands related to transaction stat
     * @param command
     * @param parameters
     * @return
     */
    private String transaction(String command, String[] parameters) {
        switch (command){
            case "RCPT":
                return commandeRcpt(parameters);
            case "RSET":
                return commandeRset();
            case "QUIT":
                return commandeQuit();
            default :
                return SMTP_500_UNKNOWN_COMMAND;
        }
    }

    /**
     * return answer of commands related to sending stat
     * @param command
     * @param parameters
     * @return
     */
    private String envoie(String command, String[] parameters) {
        switch (command){
            case "RSET":
                return commandeRset();
            case "RCPT":
                return commandeRcpt(parameters);
            case "DATA":
                return commandeData();
            case "QUIT":
                return commandeQuit();
            default :
                return SMTP_500_UNKNOWN_COMMAND;
        }
    }

    /**
     * return answer of commands related to reading stat
     * @param command
     * @param parameters
     * @return
     */
    private String lecture(String command, String[] parameters) {
        switch (command){
            case SMTP_CRLF:
                return commandeCrlf(parameters);
            case "QUIT":
                return commandeQuit();
            case "RSET":
                return commandeRset();
            default :
                writeEmail(parameters);
                return SMTP_354_START_READING;
        }
    }

    /**
     * return answer of commands related to identification stat
     * @param command
     * @param parameters
     * @return
     */
    private String identification(String command, String[] parameters) {
        switch (command){
            case "MAIL":
                return commandeMailFrom(parameters);
            case "QUIT":
                return commandeQuit();
            case "RSET":
                return commandeRset();
            default :
                return SMTP_500_UNKNOWN_COMMAND;
        }
    }

    /*
        Commandes
     */

    /**
     * handle and return answer of command EHLO
     * @param parameters
     * @return
     */
    private String commandeEhlo(String[] parameters) {
        etatServeur = SERVER_IDENTIFICATION;
        return SMTP_250_SERVERDOMAIN ;
    }

    /**
     * handle and return answer of command RSET
     * @return
     */
    private String commandeRset() {
        this.currentEmail = null;
        etatServeur = SERVER_IDENTIFICATION;
        return SMTP_250_OK;
    }

    /**
     * handle and return answer of command QUIT
     * @return
     */
    private String commandeQuit() {
        continuer = false;
        etatServeur = SERVER_READY;
        return SMTP_221_CLOSING;
    }

    /**
     * handle and return answer of command MAIL FROM
     * @param parameters
     * @return
     */
    private String commandeMailFrom(String[] parameters) {
        if (parameters.length <= 1) {
            return SMTP_500_UNKNOWN_COMMAND;
        }

        String emailAddress = parameters[1];
        if (emailAddress != null && TestRegex.CheckMail(emailAddress)) {
            Utilisateur utilisateur = repertoireUtilisateur.getUtilisateurParEmail(emailAddress);
            if (utilisateur == null) {
                return SMTP_550_UNKNOWN_USER;
            }
            this.currentEmail = new Email( new ArrayList<Utilisateur>(),utilisateur);
        } else {
            return SMTP_550_UNKNOWN_USER;
        }
        etatServeur = SERVER_TRANSACTION;
        return SMTP_250_OK;
    }

    /**
     * handle and return answer of command RCPT TO
     * @param parameters
     * @return
     */
    private String commandeRcpt(String[] parameters) {
        if (parameters.length <= 1) {
            return SMTP_500_UNKNOWN_COMMAND;
        }

        String emailAddress = parameters[1];

        if ((emailAddress  != null ) && TestRegex.CheckMail(emailAddress)) {
            Utilisateur utilisateur = repertoireUtilisateur.getUtilisateurParEmail(emailAddress);
            if (utilisateur == null) {
                return SMTP_550_UNKNOWN_USER;
            }

            this.currentEmail.addRecipient(utilisateur);

        } else {
            return SMTP_550_UNKNOWN_USER;
        }
        etatServeur = SERVER_ENVOIE;
        return SMTP_250_OK;
    }

    /**
     *
     * @return
     */
    private String commandeData() {
        etatServeur = SERVER_LECTURE;
        return SMTP_354_START_READING;
    }

    /**
     *
     * @param parameters
     * @return
     */
    private String commandeCrlf(String[] parameters) {
        this.m_listeEmails.add(currentEmail);
        this.saveMails();
        etatServeur = SERVER_IDENTIFICATION;
        return SMTP_250_OK;
    }

    /*
        Autres
     */

    /**
     * save Emails in files
     */
    protected void saveMails() {
        removeSavedFiles();
        try {
            for(Email m : m_listeEmails) {
                for (Utilisateur utilisateur: m.getM_destinataires()) {
                    BufferedWriter writer = new BufferedWriter(new FileWriter("data/" + utilisateur.getM_adresseEmail() + ".pop", true));
                    writer.write(m.encode());
                    writer.close();
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param line
     */
    private void writeEmail(String[] line) {
        if (this.currentEmail.getM_message().equals("")) {
            this.currentEmail.setM_message(line[0]);
        } else {
            this.currentEmail.setM_message(this.currentEmail.getM_message() + "\n" + line[0]);
        }
    }

    protected void loadMails() {
        for (Utilisateur u: this.repertoireUtilisateur.getM_listeUtilisateurs()
             ) {

            if (u != null) {
                try {
                    BufferedReader br = new BufferedReader(new FileReader("data/" + u.getM_adresseEmail() + ".pop"));
                    int i = 0;
                    while (this.readMail(br, u)) {
                        i++;
                    }
                    System.out.println(i + " message(s) loaded.");
                    br.close();
                } catch (FileNotFoundException e) {
                    //Do nothing
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected boolean readMail(BufferedReader br, Utilisateur u) {
        ArrayList<Utilisateur> utilisateurArrayList = new ArrayList<>();
        utilisateurArrayList.add(u);
        try {
            StringBuilder sBuilder = new StringBuilder();
            String id = br.readLine();
            if(id == null) {
                return false;
            }
            while(true) {
                String line = br.readLine();
                if(line.equals(".")) {
                    sBuilder.append(".\n");
                    Email m = new Email(utilisateurArrayList, sBuilder.toString(), this.repertoireUtilisateur.getM_listeUtilisateurs());
                    m.setM_id(UUID.fromString(id));
                    m_listeEmails.add(m);
                    return true;
                } else if(line == null) {
                    return false;
                } else {
                    sBuilder.append(line)
                        .append("\n");
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void removeSavedFiles() {
        File directory = new File("data/");
        for (File f : directory.listFiles()) {
            if (f.getName().endsWith(".com.pop")) {
                try {
                    FileOutputStream writer = new FileOutputStream(f.getAbsolutePath());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
