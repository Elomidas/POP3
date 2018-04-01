package Model.Connexions;

import Model.Protocols.POP3.ObjetConnecte;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ConnexionPOP3 extends Connexion{

    public ConnexionPOP3(int port){
        super(port);
    }

    public void run(){
        action(0);
    }
}
