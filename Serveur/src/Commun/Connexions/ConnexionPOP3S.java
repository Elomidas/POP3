package Commun.Connexions;

import POP3.ObjetConnecteSecurise;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by tardy on 31/03/2018.
 */
public class ConnexionPOP3S extends Connexion {

    public ConnexionPOP3S(String domain) {
        super(domain, 1211, "POP3S");
    }

    @Override
    protected void launchObject(Socket socket) throws IOException {
        ObjetConnecteSecurise objetConnecte = new ObjetConnecteSecurise(socket, domain);
        objetConnecte.start();
    }
}
