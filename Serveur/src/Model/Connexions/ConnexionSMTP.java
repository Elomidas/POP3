package Model.Connexions;

import Model.Protocols.SMTP.ObjetSmtpConnecte;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ConnexionSMTP extends Connexion{

    public ConnexionSMTP(int port){
        super(port);
    }

    public void run(){
        action(2);
    }
}
