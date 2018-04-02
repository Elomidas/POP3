package Commun.Connexions;

import POP3.ObjetConnecte;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ConnexionPOP3 extends Thread{

    Socket socket;
    ServerSocket serverSocket;
    int m_port;
    boolean m_continuer;
    String domain;

    public ConnexionPOP3(String domain){
        this.m_port = 1210;
        this.socket = null;
        this.serverSocket = null;
        this.m_continuer = true;
        this.domain = domain;
    }

    public void run(){
        try {
            this.serverSocket = new ServerSocket(this.m_port);
            while (m_continuer) {
                System.out.println("Attente de connexion au port 1210 ");
                this.socket = this.serverSocket.accept();
                ObjetConnecte objetConnecte = new ObjetConnecte(this.socket,this.domain);
                objetConnecte.start();
                System.out.println("Début de connexion POP3");
            }
        } catch (SocketTimeoutException e) {
            m_continuer = false;
        } catch (IOException e) {
            e.printStackTrace();
        }


//        try {
//            this.socket.close();
//            if (socket.isClosed()) {
//                this.serverSocket.close();
//                System.out.println("Fin de Commun.Connexions");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}
