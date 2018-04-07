package Commun.Connexions;

import POP3.ObjetConnecte;

import java.io.IOException;
import java.net.Socket;

public class ConnexionPOP3 extends Connexion {

    public ConnexionPOP3(String domain) {
        super(domain, 1210, "POP3");
    }

    @Override
    protected void launchObject(Socket socket) throws IOException {
        ObjetConnecte objetConnecte = new ObjetConnecte(socket, domain);
        objetConnecte.start();
    }
}
