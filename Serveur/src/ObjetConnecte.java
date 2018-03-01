import com.sun.corba.se.impl.logging.POASystemException;

import javax.print.DocFlavor;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;
import java.util.SimpleTimeZone;

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
//        try
//        {
//            m_soc = new ServerSocket(m_port);
//            connexion = new Socket();
//        }
//        catch (IOException e)
//        {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        m_listeEmails = new ArrayList<Email>();

    }

    @Override
    public void run() {
//        try {
//            System.out.println("Attente d'une connexion.\n");
//            connexion = m_soc.accept();
//
//            System.out.println("Connexion effectuee avec " + connexion.getInetAddress().getHostName() + "\n");
//
//            //renvoyer message de bienvenue
//            Comm cm = new Comm(connexion);
//            new Thread(cm).start();
//            cm.Emission("Bienvenue");
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//
//        }

        String etat = POP3_ETAT_AUTHENTIFICATION;
        while (m_continuer) {

            String commande = "test";
            String message = "2";
            int messageId = 2;
            String reponse = "2";
            switch (etat) {
                case POP3_ETAT_AUTORISATION :
                    if (Objects.equals(commande, "USER")) {
                        System.out.println("USER");

                        //if user ok
                        etat = POP3_ETAT_AUTHENTIFICATION;

                    }
                    break;
                case POP3_ETAT_AUTHENTIFICATION :

                    switch (commande) {
                        case "PASS":
                        System.out.println("PASS");

                        //if pwd ok
                        etat = POP3_ETAT_TRANSACTION;
                        break;
                        case "QUIT":
                            System.out.println("QUIT");
                            m_continuer = false;
                            break;
                    }
                    break;
                case POP3_ETAT_TRANSACTION :
                    switch (commande) {
                        case "QUIT":
                            break;
                        case "RETR":
                            reponse = retr(message);
                            break;
                        case "NOOP":
                            reponse = noop();
                            break;
                        case "RSET":
                            reponse = rset();
                            break;
                        case "DELE":
                            reponse = dele(messageId);
                            break;
                        case "LIST":
                            reponse = list(messageId);
                            break;
                        case "STAT":
                            reponse = stat();
                            break;
                        case "FERMETURE_AUTRE_QUE_QUIT":
                        default:
                            fermetureAutreQueQuit();
                            break;
                    }
                    break;
                default:
                    break;
            }
//            m_continuer = false;
        }

        try{

            m_soc.close();
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }

    private String list(int idMessage) {

        if (idMessage == 0) {
            return POP3_REPONSE_POSITIVE;
        } else {
            return POP3_REPONSE_NEGATIVE;
        }
        //if no idMessage list all message
        // return positive answer + idMessage ++ number of octet
        // return negative answer if idMessage doesn't exist

    }

    private String quit() {
        return POP3_REPONSE_POSITIVE;
    }

    private String retr(String message) {
        return POP3_REPONSE_POSITIVE;
    }

    private String noop() {
        //return postive answer
        return POP3_REPONSE_POSITIVE;
    }

    private String rset() {
        int nombreEmailsEffaces = 0;
        int nombreOctets = 0;
        //drop deleted tag on all message tagged as deleted
        //return positive answwer + number of message in maildrop + number of octets

        return POP3_REPONSE_POSITIVE + " " + nombreEmailsEffaces + " " + nombreOctets;
    }

    private String dele(int idMessage) {
        String reponse = POP3_REPONSE_NEGATIVE;
        //tag message as deleted
        //return positive answer or negative if message already tagged as deleted + "message" idMessage + "deleted"
        return POP3_REPONSE_POSITIVE + " message " + idMessage + "deleted";
    }

    private String stat() {
        //return positive answer + space + number of email + space + number of octets
        return POP3_REPONSE_POSITIVE + " " + 5 + " " + 5;
    }

    private void fermetureAutreQueQuit() {
        //close something
    }

    private String pass(String nomUtilisateur,String mdp) {
        // check if password related to userName
        Utilisateur utilisateur = getUtilisateurParNom(nomUtilisateur);
        if (mdp == utilisateur.getM_mdp()) {
            return POP3_REPONSE_POSITIVE;
        }
        return POP3_REPONSE_NEGATIVE;
    }

    private String user(String nomUtilisateur) {
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
        while (i <= m_listeUtilisateurs.size()) {
            Utilisateur utilisateur = m_listeUtilisateurs.get(i);
            if (utilisateur.getM_adresseEmail() == email) {
                return utilisateur;
            }
        }
        return null;
    }
    
    public Utilisateur getUtilisateurParNom(String nomUtilisateur) {
        int i = 0;
        while (i <= m_listeUtilisateurs.size()) {
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

        if (emetteur ==null || destinataire == null ) {
            System.out.println("Erreur pas d'emetteur ou de destinataire");
        } else {
            Email email = new Email(0, message, destinataire, emetteur, true);
            m_listeEmails.add(email);
        }
    }

    public void enregistreEmail(String emailEmetteur, String emailDestinataire, String message) {
        XStream xstream = new XStream();

    }

}
