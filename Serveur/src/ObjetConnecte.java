import com.sun.corba.se.impl.logging.POASystemException;

import javax.print.DocFlavor;
import javax.rmi.CORBA.Util;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
        m_listeUtilisateurs = new ArrayList<Utilisateur>();

    }

    @Override
    public void run() {
        ajouteUtilisateur("MTARDY", "mtardy@email.com", "abcd123");
        ajouteUtilisateur("VREMOND", "vremond@email.com", "abcd1234");
        ajouteEmail("mtardy@email.com", "vremond@email.com", "coucou");
        ajouteEmail("mtardy@email.com", "vremond@email.com", "coucou2");
        ajouteEmail("mtardy@email.com", "vremond@email.com", "coucou3");

        System.out.println("message:");

        List<String> list = this.recupereSauvegardeEmail("mtardy@email.com");


        String etat = POP3_ETAT_AUTHENTIFICATION;
        while (m_continuer) {

            String commande = "test";
            String message = "2";
            int messageId = 2;
            String reponse = "2";
            switch (etat) {
                case POP3_ETAT_AUTORISATION:
                    if (Objects.equals(commande, "USER")) {
                        System.out.println("USER");

                        //if user ok
                        etat = POP3_ETAT_AUTHENTIFICATION;

                    }
                    break;
                case POP3_ETAT_AUTHENTIFICATION:

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
                case POP3_ETAT_TRANSACTION:
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
            m_continuer = false;
        }

//        try{
//
//            m_soc.close();
//        }
//        catch(IOException ioException){
//            ioException.printStackTrace();
//        }
//    }
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
        for (Email email: m_listeEmails
             ) {
            if (email.getM_id() == emailId) {
                return email;
            }
        }
        return null;
    }

    public List<String> recupereSauvegardeEmail(String adresseEmail) {
        List<String> results = new ArrayList<String>();


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

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
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

}
