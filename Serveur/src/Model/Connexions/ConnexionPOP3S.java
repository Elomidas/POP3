package Model.Connexions;

import Model.Protocols.POP3S.ObjetConnecteSecurise;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnexionPOP3S extends Connexion{

    public ConnexionPOP3S(int port){
        super(port);
    }

    public void run(){
        action(1);
    }
}
