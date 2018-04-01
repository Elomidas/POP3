package Model.Connexions;

import Model.Protocols.POP3.ObjetConnecteSecurise;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ConnexionPOP3S extends Thread{

    Socket socket;
    ServerSocket serverSocket;
    int m_port;
    boolean m_continuer;

    public ConnexionPOP3S(){
        this.m_port = 1211;
        this.socket = null;
        this.serverSocket = null;
        this.m_continuer = true;
    }

    public void run(){
        try {
            this.serverSocket = new ServerSocket(this.m_port);
            while (true) {
                System.out.println("Attente de connexion au port 1211 ");
                this.socket = this.serverSocket.accept();

                ObjetConnecteSecurise objetConnecte = new ObjetConnecteSecurise(this.socket);
                objetConnecte.start();
                System.out.println("Début de connexion POP3S");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
