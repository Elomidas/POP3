import org.omg.CORBA.OBJECT_NOT_EXIST;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Created by tardy on 13/02/2018.
 */
public class ObjetConnecte {
    private static final String POP3_ETAT_AUTORISATION = "Autorisation";
    private static final String POP3_ETAT_AUTHENTIFICATION = "Authentification";
    private static final String POP3_ETAT_TRANSACTION = "Transaction";
    private static final String POP3_REPONSE_NEGATIVE = "-ERR";
    private static final String POP3_REPONSE_POSITIVE = "+OK";
    protected static HashMap<String, Boolean> m_locked;
    static {
        m_locked = new HashMap<>();
    }

    protected boolean m_continuer;
    private String m_etat;
    private ArrayList<Email> m_listeEmails;
    private ArrayList<Utilisateur> m_listeUtilisateurs;
    protected Utilisateur m_current;
    protected boolean m_lock;
    protected Tcp m_tcp;
    protected int m_blankCount;

    public ObjetConnecte(Tcp tcp) {
        m_tcp = tcp;
    }

    protected void initialize() {
        m_listeUtilisateurs = new ArrayList<>();
        m_listeEmails = new ArrayList<>();
        m_current = null;
        m_continuer = true;
        m_lock = false;
        m_blankCount = 0;

        this.loadUsersFromFile();
    }

    public void Launch() {
        this.initialize();

        m_etat = POP3_ETAT_AUTORISATION;
        String input;
        while (m_continuer) {
            try {
                System.out.println("Wait...");
                input = m_tcp.Receive();
                System.out.println(input + " received");

                String[] explodedCommand = input.split(" ", 2);
                String command = explodedCommand[0].toUpperCase();
                String[] parameters = new String[0];
                if(explodedCommand.length > 1) {
                    parameters = explodedCommand[1].split(" ");
                }
                String response;
                if(command.equals("")) {
                    m_blankCount++;
                } else {
                    m_blankCount = 0;
                }
                if(m_blankCount == 9) {
                    response = ObjetConnecte.POP3_REPONSE_NEGATIVE + " one more blank command and you will be disconnected.";
                } else if(m_blankCount >= 10) {
                    if(m_lock) {
                        this.unlock(m_current.getM_adresseEmail());
                    }
                    response = ObjetConnecte.POP3_REPONSE_NEGATIVE + " you've been deconnected by server.";
                    m_continuer = false;
                } else {
                    switch (m_etat) {
                        case ObjetConnecte.POP3_ETAT_AUTORISATION:
                            response = this.AuthorisationState(command, parameters);
                            break;
                        case ObjetConnecte.POP3_ETAT_AUTHENTIFICATION:
                            response = this.AuthenticationState(command, parameters);
                            break;
                        case ObjetConnecte.POP3_ETAT_TRANSACTION:
                            response = this.TransactionState(command, parameters);
                            break;
                        default:
                            System.out.println("What is that (state/command) : " + m_etat + "/" + command);
                            response = ObjetConnecte.POP3_REPONSE_NEGATIVE;
                            break;
                    }
                }

                m_tcp.Send(response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("End of POP3");
    }

    /*  ###
     *  # Automates
     *  ###
     */

    protected String AuthorisationState(String command, String[] parameters) {
        if (command.equals("USER")) {
            if(parameters.length < 1) {
                return ObjetConnecte.POP3_REPONSE_NEGATIVE + " parameter missing.";
            }
            String username = parameters[0];
            System.out.println("username : " + username);
            if(this.checkUser(username)) {
                m_etat = POP3_ETAT_AUTHENTIFICATION;
                return ObjetConnecte.POP3_REPONSE_POSITIVE;
            } else {
                return ObjetConnecte.POP3_REPONSE_NEGATIVE + " username is not valid";
            }
        }
        return ObjetConnecte.POP3_REPONSE_NEGATIVE + " command \"" + command + "\" doesn't seem valid";
    }

    protected String AuthenticationState(String command, String[] parameters) {
        if(command.equals("PASS")) {
            if(parameters.length < 1) {
                return ObjetConnecte.POP3_REPONSE_NEGATIVE + " parameter missing.";
            }
            String password = parameters[0];

            if(this.checkPass(password)) {
                if(this.isFree(m_current.getM_adresseEmail())) {
                    this.lock(m_current.getM_adresseEmail());
                    this.loadMails(m_current);
                    m_etat = POP3_ETAT_TRANSACTION;
                    return ObjetConnecte.POP3_REPONSE_POSITIVE + " POP3 server ready";
                } else {
                    return ObjetConnecte.POP3_REPONSE_NEGATIVE + " unable to lock/open your repository";
                }
            }
        } else if(command.equals("QUIT")) {
            return this.quit();
        }
        return ObjetConnecte.POP3_REPONSE_NEGATIVE + " command \"" + command + "\" doesn't seem valid";
    }

    protected String TransactionState(String command, String[] parameters) {
        if(command.equals("QUIT")) {
            return this.quit();
        } else if(command.equals("RETR")) {
            if(parameters.length < 1) {
                return ObjetConnecte.POP3_REPONSE_NEGATIVE + " parameter missing.";
            }
            return retr(parameters[0]);
        } else if(command.equals("NOOP")) {
            return noop();
        } else if(command.equals("RSET")) {
            //To be tested
            return rset();
        } else if(command.equals("DELE")) {
            //To be tested
            if(parameters.length < 1) {
                return ObjetConnecte.POP3_REPONSE_NEGATIVE + " parameter missing.";
            }
            return dele(Integer.parseInt(parameters[0]));
        } else if(command.equals("LIST")) {
            return list();
        } else if(command.equals("UIDL")) {
            return uidl();
        } else if(command.equals("STAT")) {
            return stat();
        } else {
            //fermetureAutreQueQuit();
            return ObjetConnecte.POP3_REPONSE_NEGATIVE + " unknown command '" + command + "'.";
        }
    }

    /*  ###
     *  # POP3 Commands
     *  ###
     */

    private String list() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(ObjetConnecte.POP3_REPONSE_POSITIVE)
                .append(" ")
                .append(m_listeEmails.size())
                .append(" message(s) :\n");
        for(int i = 0; i < m_listeEmails.size(); i++) {
            sBuilder.append(i+1)
                    .append(" ")
                    .append(m_listeEmails.get(i).Size())
                    .append("\n");
        }
        return sBuilder.toString();
    }

    private String uidl() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(ObjetConnecte.POP3_REPONSE_POSITIVE)
                .append(" ")
                .append(m_listeEmails.size())
                .append(" message(s) :\n");
        for(int i = 0; i < m_listeEmails.size(); i++) {
            sBuilder.append(i+1)
                    .append(" ")
                    .append(m_listeEmails.get(i).getM_id())
                    .append("\n");
        }
        return sBuilder.toString();
    }

    private String quit() {
        m_continuer = false;
        if(m_lock) {
            this.unlock(m_current.getM_adresseEmail());
        }
        return POP3_REPONSE_POSITIVE;
    }

    private String retr(String id) {
        Email m = getEmail(id);
        if(m == null) {
            return POP3_REPONSE_NEGATIVE + " unable to find this message.";
        }
        return POP3_REPONSE_POSITIVE + " \n" + m.encode();
    }

    private String noop() {
        //return postive answer
        return POP3_REPONSE_POSITIVE;
    }

    private String rset() {
        int nombreEmailsReset = 0;
        int nombreOctets = 0;
        //drop deleted tag on all message tagged as deleted

        for (Email email: m_listeEmails) {

            if (email.getM_etat() == false){
                email.setM_etat(true);
                nombreEmailsReset++;
                nombreOctets += email.Size();
            }


        }
        //return positive answwer + number of message in maildrop + number of octets

        return POP3_REPONSE_POSITIVE + " " + nombreEmailsReset + " " + nombreOctets;
    }

    private String dele(int idMessage) {
        //TODO
        //Function delete
        String reponse = POP3_REPONSE_NEGATIVE;
        //tag message as deleted
        //return positive answer or negative if message already tagged as deleted + "message" idMessage + "deleted"
        return POP3_REPONSE_POSITIVE + " message " + idMessage + "deleted";
    }

    private String stat() {
        int size = 0;
        int number = 0;
        for(Email m : m_listeEmails) {
            size += m.Size();
            number++;
        }
        return POP3_REPONSE_POSITIVE + " " + number + " " + size;
    }

    private void fermetureAutreQueQuit() {
        //TODO
        //All
        //close something
    }

    /*  ###
     *  # Check functions
     *  ###
     */

    private boolean checkUser(String username) {
        System.out.println("Check 1");
        Utilisateur u = getUtilisateurParNom(username);
        System.out.println("Check 2");
        if (u != null) {
            m_current = u;
            System.out.println("Check 3");
            return true;
        }
        System.out.println("Check 4");
        return false;
    }

    private boolean checkPass(String password) {
        return m_current.checkPassword(password);
    }

    /*  Check if a mail repository is free or locked
     */
    protected boolean isFree(String mail) {
        return m_locked.containsKey(mail) && (m_locked.get(mail) == false);
    }

    /*  ###
     *  # Users
     *  ###
     */

    public void ajouteUtilisateur(String nom, String email, String mdp) {

        Utilisateur utilisateur = new Utilisateur(0, nom, mdp, email);
        m_listeUtilisateurs.add(utilisateur);
    }

    public Utilisateur getUtilisateurParEmail(String email) {
        int i = 0;
        while (i < m_listeUtilisateurs.size()) {
            Utilisateur utilisateur = m_listeUtilisateurs.get(i);
            if (utilisateur.getM_adresseEmail().equals(email)) {
                return utilisateur;
            }
            i++;
        }
        return null;
    }
    
    public Utilisateur getUtilisateurParNom(String nomUtilisateur) {
        for(int i = 0; i < m_listeUtilisateurs.size(); i++) {
            Utilisateur utilisateur = m_listeUtilisateurs.get(i);
            if (utilisateur.getM_adresseEmail().equals(nomUtilisateur)) {
                return utilisateur;
            }
        }
        return null;
    }

    protected void loadUsersFromFile() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("data/users.pop"));
            String line = br.readLine();
            int i = 0;
            while((line != null) && (line.length() > 4)) {
                Utilisateur u = new Utilisateur(line);
                m_listeUtilisateurs.add(u);
                m_locked.putIfAbsent(u.getM_adresseEmail(), false);
                line = br.readLine();
                i++;
            }
            System.out.println(i + " users added.");
            br.close();
        } catch(FileNotFoundException e) {
            System.out.println("Unable to open users.pop");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /*  Lock a mailbox
     */
    protected void lock(String mail) {
        if(this.isFree(mail)) {
            m_locked.put(mail, true);
            m_lock = true;
        }
    }

    protected void unlock(String mail) {
        m_locked.put(mail, false);
        m_lock = false;
    }

    /*  ###
     *  # Emails
     *  ###
     */

    public void ajouteEmail(String emailEmetteur, String emailDestinataire, String message) {
        Utilisateur emetteur = getUtilisateurParEmail(emailEmetteur);
        Utilisateur destinataire = getUtilisateurParEmail(emailDestinataire);
        String id = "" + UUID.randomUUID();
        if (emetteur == null || destinataire == null ) {
            System.out.println("Erreur pas d'emetteur ou de destinataire");
        } else {
            Email email = new Email(id, message, destinataire, emetteur, true);
            m_listeEmails.add(email);
            //enregistreEmail(emailEmetteur,emailDestinataire, message, uuid);
        }
    }

    public void enregistreEmail(String emailEmetteur, String emailDestinataire, String message, UUID id) {

        FileOutputStream out = null;
        try {
            out = new FileOutputStream("./data/"+ emailEmetteur + "_" + emailDestinataire + "_" + id +".txt");
            out.write(message.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Email getEmail(String emailId){
        for (Email email: m_listeEmails) {
            if (email.getM_id().equals(emailId)) {
                return email;
            }
        }
        return null;
    }

    public List<Email> recupereEmails(Utilisateur utilisateur) {
        //To be tested
        List<Email> listEmails = new ArrayList<Email>();
        for (Email email: m_listeEmails) {
            if (email.getM_emetteur().equals(utilisateur.getM_adresseEmail())
                || email.getM_destinataire().equals(utilisateur.getM_adresseEmail())) {
                listEmails.add(email);
            }
        }
        return listEmails;
    }

    protected void saveMails(Utilisateur u) {
        try {

            BufferedWriter writer = new BufferedWriter(new FileWriter("data/" + u.getM_adresseEmail() + ".pop"));

            for(Email m : m_listeEmails) {
                if(m.getM_destinataire().getM_adresseEmail().equals(u.getM_adresseEmail())) {
                    System.out.println(m.getM_destinataire().getM_adresseEmail() + " == " + u.getM_adresseEmail());
                    writer.write(m.encode());
                } else {
                    System.out.println(m.getM_destinataire().getM_adresseEmail() + " != " + u.getM_adresseEmail());
                }
            }

            writer.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    protected void loadMails(Utilisateur u) {
        if(u != null) {
            try {
                BufferedReader br = new BufferedReader(new FileReader("data/" + u.getM_adresseEmail() + ".pop"));
                int i = 0;
                while (this.readMail(br, u)) {
                    i++;
                }
                System.out.println(i + " message(s) loaded.");
                br.close();
            } catch(FileNotFoundException e) {
                //Do nothing
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected boolean readMail(BufferedReader br, Utilisateur u) {
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
                    Email m = new Email(u, sBuilder.toString(), m_listeUtilisateurs);
                    m.setM_id(id);
                    m_listeEmails.add(m);
                    return true;
                } else if(line == null) {
                    return false;
                } else {
                    sBuilder.append(line + "\n");
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
