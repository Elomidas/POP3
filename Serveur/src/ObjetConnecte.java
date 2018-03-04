import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by tardy on 13/02/2018.
 */
public class ObjetConnecte extends Thread {
    private static final String POP3_ETAT_AUTORISATION = "Autorisation";
    private static final String POP3_ETAT_AUTHENTIFICATION = "Authentification";
    private static final String POP3_ETAT_TRANSACTION = "Transaction";
    private static final String POP3_REPONSE_NEGATIVE = "-ERR";
    private static final String POP3_REPONSE_POSITIVE = "+OK";

    protected ServerSocket m_soc;
    protected boolean m_continuer;
    protected int m_port;
    private Socket m_connexion;
    private String m_etat;
    private ArrayList<Email> m_listeEmails;
    private ArrayList<Utilisateur> m_listeUtilisateurs;

    public ObjetConnecte()
    {
        this(80);
    }

    public ObjetConnecte(int port)
    {

        m_port = port;
        m_continuer = true;
        try {
            m_soc = new ServerSocket(m_port);
            m_connexion = new Socket();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        m_listeEmails = new ArrayList<Email>();
        m_listeUtilisateurs = new ArrayList<Utilisateur>();

    }

    @Override
    public void run() {
        ajouteUtilisateur("MTARDY", "mtardy@email.com", "Something");
        ajouteUtilisateur("VREMOND", "vremond@email.com", "Something else");
        ajouteEmail("mtardy@email.com", "vremond@email.com", "This is a message");
        ajouteEmail("mtardy@email.com", "vremond@email.com", "This is another message");
        ajouteEmail("mtardy@email.com", "vremond@email.com", "Another again");

        System.out.println("message:");

        List<String> list = this.recupereSauvegardeEmail("mtardy@email.com",false);


        m_etat = POP3_ETAT_AUTHENTIFICATION;
        while (m_continuer) {

            String input = "test";
            //TODO
            //Receive command
            String[] explodedCommand = input.split(" ", 2);
            String command = explodedCommand[0];
            String[] parameters = explodedCommand[1].split(" ");
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
                    break;
            }
            // TODO
            // Send response
        }

        try{

            m_soc.close();
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }

    protected String AuthorisationState(String command, String[] parameters) {
        if (command.equals("USER")) {
            boolean userOK = false;
            //TODO
            //User authentication

            if(userOK) {
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
            boolean passOK = false;
            //TODO
            //Check PASS

            if(passOK) {
                boolean openLock = false;
                //TODO
                //Open and lock repository
                if(openLock) {
                    m_etat = POP3_ETAT_TRANSACTION;
                    return ObjetConnecte.POP3_REPONSE_POSITIVE + " POP3 server ready";
                } else {
                    return ObjetConnecte.POP3_REPONSE_NEGATIVE + " unable to lock/open your repository";
                }
            }
        } else if(command.equals("QUIT")) {
            m_continuer = false;
        }
        return ObjetConnecte.POP3_REPONSE_NEGATIVE + " command \"" + command + "\" doesn't seem valid";
    }

    protected String TransactionState(String command, String[] parameters) {
        if(command.equals("QUIT")) {
            //TODO
            //Quit
        } else if(command.equals("RETR")) {
            //To be tested
            return retr(parameters[0]);
        } else if(command.equals("NOOP")) {
            //To be tested
            return noop();
        } else if(command.equals("RSET")) {
            //To be tested
            return rset();
        } else if(command.equals("DELE")) {
            //To be tested
            return dele(Integer.parseInt(parameters[0]));
        } else if(command.equals("LIST")) {
            //To be tested
            return list();
        } else if(command.equals("STAT")) {
            return stat();
        } else {
            //TODO
            //return something
            //TODO
            //Don't quit on unknown command
            fermetureAutreQueQuit();
            return ObjetConnecte.POP3_REPONSE_NEGATIVE;
        }
        return ObjetConnecte.POP3_REPONSE_NEGATIVE;
    }

    private String list() {
        //TODO
        //LIST function
        return ObjetConnecte.POP3_REPONSE_NEGATIVE;
    }

    private String quit() {
        //TODO
        return POP3_REPONSE_POSITIVE;
    }

    private String retr(String id) {
        //TODO
        Email m = getEmail(UUID.fromString(id));
        if(m == null) {
            return POP3_REPONSE_NEGATIVE;
        }
        return POP3_REPONSE_POSITIVE + " \n" + m.encode();
    }

    private String noop() {
        //return postive answer
        return POP3_REPONSE_POSITIVE;
    }

    private String rset() {
        //To be tested
        int nombreEmailsReset = 0;
        int nombreOctets = 0;
        //drop deleted tag on all message tagged as deleted

        for (Email email: m_listeEmails) {

            if (!email.getM_etat()){
                email.setM_etat(true);
                nombreEmailsReset++;
                nombreOctets += email.getM_message().length();
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
        //TODO
        //Function stat


        //return positive answer + space + number of email + space + number of octets
        return POP3_REPONSE_POSITIVE + " " + 5 + " " + 5;
    }

    private void fermetureAutreQueQuit() {
        //TODO
        //All
        //close something
    }

    private String pass(String nomUtilisateur,String mdp) {
        //To be tested
        // check if password related to userName
        Utilisateur utilisateur = getUtilisateurParNom(nomUtilisateur);
        if (mdp == utilisateur.getM_mdp()) {
            return POP3_REPONSE_POSITIVE;
        }
        return POP3_REPONSE_NEGATIVE;
    }

    private String user(String nomUtilisateur) {
        //To be tested
        //check if nomUtilisateur exists
        if (getUtilisateurParNom(nomUtilisateur) != null) {
            return POP3_REPONSE_POSITIVE;
        }
        return POP3_REPONSE_NEGATIVE;
    }

    public void ajouteUtilisateur(String nom, String email, String mdp) {

        Utilisateur utilisateur = new Utilisateur(0, nom, mdp, email);
        m_listeUtilisateurs.add(utilisateur);
    }

    public Utilisateur getUtilisateurParEmail(String email) {
        int i = 0;
        while (i < m_listeUtilisateurs.size()) {
            Utilisateur utilisateur = m_listeUtilisateurs.get(i);
            if (utilisateur.getM_adresseEmail() == email) {
                return utilisateur;
            }
            i++;
        }
        return null;
    }
    
    public Utilisateur getUtilisateurParNom(String nomUtilisateur) {
        int i = 0;
        while (i < m_listeUtilisateurs.size()) {
            Utilisateur utilisateur = m_listeUtilisateurs.get(i);
            if (utilisateur.getM_adresseEmail() == nomUtilisateur) {
                return utilisateur;
            }
        }
        return null;
    }
    public void ajouteEmail(String emailEmetteur, String emailDestinataire, String message) {
        Utilisateur emetteur = getUtilisateurParEmail(emailEmetteur);
        Utilisateur destinataire = getUtilisateurParEmail(emailDestinataire);
        UUID uuid = UUID.randomUUID();
        if (emetteur ==null || destinataire == null ) {
            System.out.println("Erreur pas d'emetteur ou de destinataire");
        } else {
            Email email = new Email(uuid, message, destinataire, emetteur, true);
            m_listeEmails.add(email);
            enregistreEmail(emailEmetteur,emailDestinataire, message, uuid);
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

    public Email getEmail(UUID emailId){
        for (Email email: m_listeEmails) {
            if (email.getM_id() == emailId) {
                return email;
            }
        }
        return null;
    }

    public List<String> recupereSauvegardeEmail(String adresseEmail,boolean reset) {
        //To be tested
        List<String> results = new ArrayList<>();

        m_listeEmails = reset? null : m_listeEmails;

        File[] files = new File("./data").listFiles();
        //If this pathname does not denote a directory, then listFiles() returns null.

        assert files != null;
        System.out.println("Number of email saved:");
        System.out.println(files.length);
        for (File file : files) {
            StringBuilder stringBuilder = new StringBuilder();
            if (file.isFile()) {
                String[] nameSplit = file.getName().split("_");
                String emailEmetteur = nameSplit[0];
                String emailDestinataire = nameSplit[1];
                String id = nameSplit[2].split(".txt")[0];

                if ((Objects.equals(emailEmetteur, adresseEmail) || Objects.equals(emailDestinataire, adresseEmail))
                        && !emailEmetteur.isEmpty()
                        && !emailDestinataire.isEmpty()
                        && !id.isEmpty()
                        ) {

                    FileInputStream in = null;

                    String message = "";
                    int content = 0;
                    try {
                        in = new FileInputStream("./data/"+ emailEmetteur + "_" + emailDestinataire + "_" + id +".txt");
                        while ((content = in.read()) != -1) {
                            stringBuilder.append((char) content);
                        }
                        message = stringBuilder.toString();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Utilisateur destinataire = getUtilisateurParEmail(emailDestinataire);
                    Utilisateur emetteur = getUtilisateurParEmail(emailEmetteur);
                    Email email = new Email(UUID.fromString(id), message, destinataire, emetteur, true);
                    m_listeEmails.add(email);
                }
            }
        }
        return results;
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

}
