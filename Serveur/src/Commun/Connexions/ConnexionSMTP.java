package Commun.Connexions;

import SMTP.ObjetSmtpConnecte;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by tardy on 31/03/2018.
 */
public class ConnexionSMTP extends Connexion {

    public ConnexionSMTP(String domain){
        super(domain, 1212, "SMTP");
    }

    @Override
    protected void launchObject(Socket socket) throws IOException {
        ObjetSmtpConnecte objetConnecte = new ObjetSmtpConnecte(socket, domain);
        objetConnecte.start();
    }
}
