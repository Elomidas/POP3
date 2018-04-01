package Model.Protocols.POP3;

import Model.Mail.Email;
import Model.Mail.Mailbox;
import Model.Protocols.TCP.Tcp;
import Model.Utilisateur.Utilisateur;

import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 * Created by tardy on 13/02/2018.
 */
public class ObjetConnecte extends Thread{
    protected static final String POP3_ETAT_AUTORISATION = "Autorisation";
    protected static final String POP3_ETAT_AUTHENTIFICATION = "Authentification";
    protected static final String POP3_ETAT_TRANSACTION = "Transaction";
    protected static final String POP3_REPONSE_NEGATIVE = "-ERR";
    protected static final String POP3_REPONSE_POSITIVE = "+OK";
    protected static HashMap<String, Boolean> m_locked;

    static {
        m_locked = new HashMap<>();
    }

    protected boolean m_continuer;
    protected String m_etat;
    protected Mailbox m_mailbox;
    protected Utilisateur m_current;
    protected boolean m_lock;
    protected Tcp m_tcp;
    protected int m_blankCount;

    public ObjetConnecte(Socket socket) throws IOException {
        this.m_tcp = new Tcp(socket);

    }

    protected void initialize() {
        m_mailbox = new Mailbox();
        m_current = null;
        m_continuer = true;
        m_lock = false;
        m_blankCount = 0;
        m_mailbox.getRepertoireUtilisateur().loadUsersFromFile();
    }

    public void run() {
        this.initialize();

        m_etat = POP3_ETAT_AUTORISATION;
        String input;
        while (m_continuer) {
            try {
//                System.out.println("Wait...");
                input = m_tcp.receive();
//                System.out.println(input + " received");

                String[] explodedCommand = input.split(" ", 2);
                String command = explodedCommand[0].toUpperCase();
                String[] parameters = new String[0];
                if(explodedCommand.length > 1) {
                    parameters = explodedCommand[1].split(" ");
                }
                String response;

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

                System.out.println("S: " + response);
                m_tcp.send(response);
            } catch (IOException e) {
                e.printStackTrace();
                m_continuer = false;
                return;
            }
        }
        this.m_tcp.Destroy();
        System.out.println("End of Model.Protocols.POP3");
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
                    this.m_mailbox.loadMails(m_current);
                    this.m_mailbox.setEmailsUndeleted(m_current);
                    m_etat = POP3_ETAT_TRANSACTION;
                    return ObjetConnecte.POP3_REPONSE_POSITIVE + " Model.Protocols.POP3 server ready";
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
     *  # Model.Protocols.POP3 Commands
     *  ###
     */

    private String list() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(ObjetConnecte.POP3_REPONSE_POSITIVE)
                .append(" ")
                .append(m_mailbox.getM_listeEmails().size())
                .append(" message(s) :\n");
        for(int i = 0; i < m_mailbox.getM_listeEmails().size(); i++) {
            sBuilder.append(i+1)
                    .append(" ")
                    .append(m_mailbox.getM_listeEmails().get(i).Size())
                    .append("\n");
        }
        return sBuilder.toString();
    }

    private String uidl() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(ObjetConnecte.POP3_REPONSE_POSITIVE)
                .append(" ")
                .append(m_mailbox.getM_listeEmails().size())
                .append(" message(s) :\n");
        for(int i = 0; i < m_mailbox.getM_listeEmails().size(); i++) {
            sBuilder.append(i+1)
                    .append(" ")
                    .append(m_mailbox.getM_listeEmails().get(i).getM_id())
                    .append("\n");
        }
        return sBuilder.toString();
    }

    protected String quit() {
        m_continuer = false;
        if(m_lock) {
            this.unlock(m_current.getM_adresseEmail());
        }
        return POP3_REPONSE_POSITIVE;
    }

    protected String quitTransaction() {
        m_continuer = false;
        if(m_lock) {
            this.unlock(m_current.getM_adresseEmail());
        }
        List<Email> listEmailsToRemove = new ArrayList<>();
        List<Email> listEmailsOfUser = m_mailbox.recupereEmails(m_current);
        for (Email email: listEmailsOfUser) {
            if (!email.getM_etat()) {
                m_mailbox.getM_listeEmails().remove(email);
                listEmailsToRemove.add(email);
            }
        }

        this.m_mailbox.removeMails(m_current);
        return POP3_REPONSE_POSITIVE;
    }

    private String retr(String id) {
        Email m = m_mailbox.getEmail(id);
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

        for (Email email: m_mailbox.getM_listeEmails()) {

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
        Email email = m_mailbox.getEmail(stringBuilder.append(idMessage).toString());
        int index = m_mailbox.getM_listeEmails().indexOf(email);
        if (email.getM_etat()) {
            email.setM_etat(false);
            m_mailbox.getM_listeEmails().set(index, email);
            //return positive answer or negative if message already tagged as deleted + "message" idMessage + "deleted"
            return POP3_REPONSE_POSITIVE + " message " + idMessage + "deleted";
        } else {
            return POP3_REPONSE_NEGATIVE + " message " + idMessage + "already deleted";
        }
    }

    private String stat() {
        int size = 0;
        int number = 0;
        for(Email m : m_mailbox.getM_listeEmails()) {
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
        Utilisateur u = this.m_mailbox.getRepertoireUtilisateur().getUtilisateurParNom(username);
        if(u == null) {
            u = this.m_mailbox.getRepertoireUtilisateur().getUtilisateurParEmail(username);
        }
        if (u != null) {
            m_current = u;
            return true;
        }
        return false;
    }

    private boolean checkPass(String password) {
        return m_current.checkPassword(password);
    }

    /*  Check if a mail repository is free or locked
     */
    protected boolean isFree(String mail) {
        return !m_locked.containsKey(mail) || (m_locked.get(mail) == false);
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


}
