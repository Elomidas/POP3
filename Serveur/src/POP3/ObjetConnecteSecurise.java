package POP3;

import Commun.Email;
import Commun.Tcp;
import Commun.Utilisateur;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.System.*;

/**
 * Created by tardy on 12/03/2018.
 */
public class ObjetConnecteSecurise {
    private static final String POP3_ETAT_AUTORISATION = "Autorisation";
    private static final String POP3_ETAT_TRANSACTION = "Transaction";
    private static final String POP3_REPONSE_NEGATIVE = "-ERR";
    private static final String POP3_REPONSE_POSITIVE = "+OK";
    protected static HashMap<String, Boolean> m_locked;
    protected static ArrayList<Utilisateur> m_listeUtilisateurs;
    private Timestamp timeConnexion;
    private int processId;

    static {
        m_locked = new HashMap<>();
        m_listeUtilisateurs = new ArrayList<>();
        loadUsersFromFile();
    }

    protected boolean m_continuer;
    private String m_etat;
    private ArrayList<Email> m_listeEmails;
    protected Utilisateur m_current;
    protected boolean m_lock;
    protected Tcp m_tcp;
    protected int m_blankCount;

    public ObjetConnecteSecurise(Tcp tcp) {
        m_tcp = tcp;
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
        try {
            m_tcp.send(ObjetConnecteSecurise.POP3_REPONSE_POSITIVE + " POP3 server ready "  + generateTimbre());
        } catch (InvocationTargetException | NoSuchMethodException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        while (m_continuer) {
            try {
                out.println("Wait...");
                input = m_tcp.receive();
                out.println(input + " received");

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
                    response = ObjetConnecteSecurise.POP3_REPONSE_NEGATIVE + " one more blank command and you will be disconnected.";
                } else if(m_blankCount >= 10) {
                    if(m_lock) {
                        this.unlock(m_current.getM_adresseEmail());
                    }
                    response = ObjetConnecteSecurise.POP3_REPONSE_NEGATIVE + " you've been deconnected by server.";
                    m_continuer = false;
                } else {
                    switch (m_etat) {
                        case ObjetConnecteSecurise.POP3_ETAT_AUTORISATION:
                            response = this.AuthorisationState(command, parameters);
                            break;
                        case ObjetConnecteSecurise.POP3_ETAT_TRANSACTION:
                            response = this.TransactionState(command, parameters);
                            break;
                        default:
                            out.println("What is that (state/command) : " + m_etat + "/" + command);
                            response = ObjetConnecteSecurise.POP3_REPONSE_NEGATIVE;
                            break;
                    }
                }
                out.println("Response : " + response);
                m_tcp.send(response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        out.println("End of POP3");
    }

    public int getProcessId() throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        java.lang.management.RuntimeMXBean runtime =
                java.lang.management.ManagementFactory.getRuntimeMXBean();
        java.lang.reflect.Field jvm = runtime.getClass().getDeclaredField("jvm");
        jvm.setAccessible(true);
        sun.management.VMManagement mgmt =
                (sun.management.VMManagement) jvm.get(runtime);
        java.lang.reflect.Method pid_method =
                mgmt.getClass().getDeclaredMethod("getProcessId");
        pid_method.setAccessible(true);

        this.processId = (Integer) pid_method.invoke(mgmt);
        return processId;
    }

    public Long getTimestamp(){
        this.timeConnexion = new Timestamp(System.currentTimeMillis());
        return  timeConnexion.getTime();

    }

    public String generateTimbre() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, NoSuchFieldException {
        return "<"+getProcessId()+"."+getTimestamp()+"@localhost>";
    }

    public boolean decrypteTimbre(String encryptUser) throws NoSuchAlgorithmException {
        StringBuilder decrypt = new StringBuilder();

        MessageDigest md = MessageDigest.getInstance("MD5");
        decrypt.append("<")
                .append(this.processId)
                .append(".")
                .append(this.timeConnexion.getTime())
                .append("@localhost>")
                .append(this.m_current.getM_mdp());
        byte[] digestedBytes = md.digest(decrypt.toString().getBytes());
        StringBuilder returnBuilder = new StringBuilder();
        for(int i = 0; i < digestedBytes.length; i++) {
            returnBuilder.append(String.format("%02X", digestedBytes[i]));
        }
        if(returnBuilder.toString().equals(encryptUser))
            return true;
        return false;
    }

    /*  ###
     *  # Automates
     *  ###
     */

    protected String AuthorisationState(String command, String[] parameters) {
        if (command.equals("APOP")) {
            if(parameters.length < 1) {
                return ObjetConnecteSecurise.POP3_REPONSE_NEGATIVE + " parameter missing.";
            }
            String username = parameters[0];
            String password = parameters[1];
            out.println("username : " + username);

            if(this.checkUser(username)) {
                try {
                    if(this.decrypteTimbre(password)) {
                        if (this.isFree(m_current.getM_adresseEmail())) {
                            this.lock(m_current.getM_adresseEmail());
                            this.loadMails(m_current);
                            this.setEmailsUndeleted(m_current);
                            m_etat = POP3_ETAT_TRANSACTION;
                            return ObjetConnecteSecurise.POP3_REPONSE_POSITIVE;
                        } else {
                            return ObjetConnecteSecurise.POP3_REPONSE_NEGATIVE + " unable to lock/open your repository";
                        }
                    } else {
                        return ObjetConnecteSecurise.POP3_REPONSE_NEGATIVE + " password is not valid";
                    }
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

            } else {
                return ObjetConnecteSecurise.POP3_REPONSE_NEGATIVE + " username is not valid";
            }
        }
        return ObjetConnecteSecurise.POP3_REPONSE_NEGATIVE + " command \"" + command + "\" doesn't seem valid";
    }

    protected String TransactionState(String command, String[] parameters) {
        if(command.equals("QUIT")) {
            this.quitTransaction();
            return this.quit();
        } else if(command.equals("RETR")) {
            if(parameters.length < 1) {
                return ObjetConnecteSecurise.POP3_REPONSE_NEGATIVE + " parameter missing.";
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
                return ObjetConnecteSecurise.POP3_REPONSE_NEGATIVE + " parameter missing.";
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
            return ObjetConnecteSecurise.POP3_REPONSE_NEGATIVE + " unknown command '" + command + "'.";
        }
    }

    /*  ###
     *  # POP3 Commands
     *  ###
     */

    private String list() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(ObjetConnecteSecurise.POP3_REPONSE_POSITIVE)
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
        sBuilder.append(ObjetConnecteSecurise.POP3_REPONSE_POSITIVE)
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
            out.println("Email:");
            out.println(email2.getM_id());
            out.println(email2.getM_destinataire());
            out.println(email2.getM_emetteur());
            out.println(email2.getM_etat());
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

    private boolean checkUser(String username) {
        Utilisateur u = getUtilisateurParNom(username);
        if(u == null) {
            u = getUtilisateurParEmail(username);
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
            out.println(i + " users added.");
            br.close();
        } catch(FileNotFoundException e) {
            out.println("Unable to open users.pop");
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
                    out.println(m.getM_destinataire().getM_adresseEmail() + " == " + u.getM_adresseEmail());
                    writer.write(m.encode());
                } else {
                    out.println(m.getM_destinataire().getM_adresseEmail() + " != " + u.getM_adresseEmail());
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
                out.println(i + " message(s) loaded.");
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
                                out.println("Email est contenu dans la liste" + idMail);

                                bw.write(email.encode());
                            } else {
                                out.println("Email pas dans la liste" + idMail);
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

    private void setEmailsUndeleted(Utilisateur utilisateur) {

        List<Email> listeEmailsDeUtilisateur = recupereEmails(utilisateur);

        for (Email email: listeEmailsDeUtilisateur
                ) {
            email.setM_etat(true);
            m_listeEmails.set(m_listeEmails.indexOf(email), email);
        }
    }
}
