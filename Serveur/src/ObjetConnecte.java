import org.omg.CORBA.OBJECT_NOT_EXIST;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by tardy on 13/02/2018.
 */
public class ObjetConnecte {
    protected static final String POP3_ETAT_AUTORISATION = "Autorisation";
    protected static final String POP3_ETAT_AUTHENTIFICATION = "Authentification";
    protected static final String POP3_ETAT_TRANSACTION = "Transaction";
    protected static final String POP3_REPONSE_NEGATIVE = "-ERR";
    protected static final String POP3_REPONSE_POSITIVE = "+OK";
    protected static HashMap<String, Boolean> m_locked;
    protected static ArrayList<Utilisateur> m_listeUtilisateurs;

    static {
        m_locked = new HashMap<>();
        m_listeUtilisateurs = new ArrayList<>();
        loadUsersFromFile();
    }

    protected boolean m_continuer;
    protected String m_etat;
    private ArrayList<Email> m_listeEmails;
    protected Utilisateur m_current;
    protected boolean m_lock;
    protected Tcp m_tcp;
    protected int m_blankCount;

    public ObjetConnecte(Tcp tcp) {
        this.m_tcp = tcp;
    }

    protected void initialize() {
        m_listeEmails = new ArrayList<>();
        m_current = null;
        m_continuer = true;
        m_lock = false;
        m_blankCount = 0;

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
                System.out.println("Response : " + response);
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
                    this.setEmailsUndeleted(m_current);
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
            this.quitTransaction();
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

    private String quitTransaction() {
        m_continuer = false;
        if(m_lock) {
            this.unlock(m_current.getM_adresseEmail());
        }
        List<Email> listEmailsToRemove = new ArrayList<>();
        List<Email> listEmailsOfUser = recupereEmails(m_current);
        for (Email email: listEmailsOfUser) {
            if (!email.getM_etat()) {
                m_listeEmails.remove(email);
                listEmailsToRemove.add(email);
            }
        }

        this.removeMails(m_current, listEmailsToRemove);
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
        //Function delete
        String reponse = POP3_REPONSE_NEGATIVE;
        //tag message as deleted
        StringBuilder stringBuilder = new StringBuilder();
        Email email = getEmail(stringBuilder.append(idMessage).toString());
        int index = m_listeEmails.indexOf(email);
        for (Email email2: m_listeEmails
             ) {
            System.out.println("Email:");
            System.out.println(email2.getM_id());
            System.out.println(email2.getM_destinataire());
            System.out.println(email2.getM_emetteur());
            System.out.println(email2.getM_etat());
        }
        if (email.getM_etat()) {
            email.setM_etat(false);
            m_listeEmails.set(index, email);
            //return positive answer or negative if message already tagged as deleted + "message" idMessage + "deleted"
            return POP3_REPONSE_POSITIVE + " message " + idMessage + "deleted";
        } else {
            return POP3_REPONSE_NEGATIVE + " message " + idMessage + "already deleted";
        }
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

    /*  ###
     *  # Check functions
     *  ###
     */

    protected boolean checkUser(String username) {
        for(Utilisateur u : m_listeUtilisateurs) {
            System.out.println("'" + u.getM_adresseEmail() + "' - " + u.getM_mdp());
        }
        System.out.println("Check 1");
        Utilisateur u = getUtilisateurParNom(username);
        if(u == null) {
            u = getUtilisateurParEmail(username);
        }
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

    public Utilisateur getUtilisateurParEmail(String email) {
        for(int i = 0; i < m_listeUtilisateurs.size(); i++) {
            Utilisateur utilisateur = m_listeUtilisateurs.get(i);
            if (utilisateur.getM_adresseEmail().equals(email)) {
                return utilisateur;
            }
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

    protected static void loadUsersFromFile() {
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
            if (email.getM_emetteur().equals(utilisateur)
                || email.getM_destinataire().equals(utilisateur)) {
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

    protected int removeMails(Utilisateur u, List<Email> listeEmailsToRemove) {
        String temp ="";
        String idMail ="";
        int i = 0;
        if(u != null) {
            try {
                BufferedReader br = new BufferedReader(new FileReader("data/" + u.getM_adresseEmail() + ".pop"));
                BufferedWriter bw = new BufferedWriter(new FileWriter("data/" + u.getM_adresseEmail() + "temp.pop"));
                idMail = br.readLine();
                Email email = getEmail(idMail);
                if (email != null) {
                    bw.write(email.encode());
                }

                while ((temp = br.readLine()) != null) {
                    if (temp.equals(".")) {
                        idMail = br.readLine();
                        if (idMail != null) {
                            email = getEmail(idMail);
                            if (email != null) {
                                System.out.println("Email est contenu dans la liste" + idMail);

                                bw.write(email.encode());
                            } else {
                                System.out.println("Email pas dans la liste" + idMail);
                            }
                        }
                    }
                }
                bw.close();
                br.close();
                File oldFile =  new File("data/" + u.getM_adresseEmail() + ".pop");
                oldFile.delete();
                File newFile = new File("data/" + u.getM_adresseEmail() + "temp.pop");
                newFile.renameTo(new File("data/" + u.getM_adresseEmail() + ".pop"));
            } catch(FileNotFoundException e) {
                //Do nothing
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return i;
    }
    
    protected void setEmailsUndeleted(Utilisateur utilisateur) {

        List<Email> listeEmailsDeUtilisateur = recupereEmails(utilisateur);

        for (Email email: listeEmailsDeUtilisateur
                ) {
            email.setM_etat(true);
            m_listeEmails.set(m_listeEmails.indexOf(email), email);
        }
    }

}
