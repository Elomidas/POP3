import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;

/**
 * Created by tardy on 13/02/2018.
 */
public class ObjetConnecte extends Thread {
    private static final String POP3_ETAT_AUTORISATION = "Autorisation";
    private static final String POP3_ETAT_AUTHENTIFICATION = "Authentification";
    private static final String POP3_ETAT_TRANSACTION = "Transaction";
    private static final boolean POP3_ETAT_OUVERT = true;

    @Override
    public void run() {

//        Socket socket = new Socket();
//        InetAddress inetAddress = new InetAddress();
        boolean pop3EtatOuvert = POP3_ETAT_OUVERT;
        String etat = POP3_ETAT_AUTHENTIFICATION;
        while (pop3EtatOuvert) {

            String commande = "test";

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
                            pop3EtatOuvert = false;
                            break;
                    }
                    break;
                case POP3_ETAT_TRANSACTION :
                    switch (commande) {
                        case "QUIT":
                            break;
                        case "RETR":
                            break;
                        case "NOOP":
                            break;
                        case "RSET":
                            break;
                        case "DELE":
                            break;
                        case "LIST":
                            break;
                        case "STAT":
                            break;
                        case "FERMETURE_AUTRE_QUE_QUIT":
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
            pop3EtatOuvert = false;
        }
    }
}
