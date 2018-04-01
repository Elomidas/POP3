package Model.Connexions;

import Model.Protocols.POP3.ObjetConnecte;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ConnexionPOP3 extends Thread{

    Socket socket;
    ServerSocket serverSocket;
    int m_port;
    boolean m_continuer;

    public ConnexionPOP3(){
        this.m_port = 1210;
        this.socket = null;
        this.serverSocket = null;
        this.m_continuer = true;
    }

    public void run(){
        try {
            this.serverSocket = new ServerSocket(this.m_port);
            while (true) {
                System.out.println("Attente de connexion au port 1210 ");
                this.socket = this.serverSocket.accept();

                ObjetConnecte objetConnecte = new ObjetConnecte(this.socket);
                objetConnecte.start();
                System.out.println("Début de connexion Model.Protocols.POP3");
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
