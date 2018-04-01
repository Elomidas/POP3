package Model.Connexions;

import Model.Protocols.SMTP.ObjetSmtpConnecte;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ConnexionSMTP extends Thread{

    Socket socket;
    ServerSocket serverSocket;
    int m_port;
    boolean m_continuer;

    public ConnexionSMTP(){
        this.m_port = 1212;
        this.socket = null;
        this.serverSocket = null;
        this.m_continuer = true;
    }

    public void run(){
        try {
            this.serverSocket = new ServerSocket(this.m_port);
            while (true) {
                System.out.println("Attente de connexion au port 1212 ");
                this.socket = this.serverSocket.accept();

                ObjetSmtpConnecte objetConnecte = new ObjetSmtpConnecte(this.socket);
                objetConnecte.start();
                System.out.println("Début de connexion Model.Protocols.SMTP");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
