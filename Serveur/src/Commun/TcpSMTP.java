package Commun;

import POP3.ObjetConnecteSecurise;
import SMTP.ObjetSmtpConnecte;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by tardy on 22/03/2018.
 */
public class TcpSMTP extends Connexion{

    public TcpSMTP(int portSMTP){
        super(portSMTP);
    }

    public void run() {

        try {

            while (true) {
                ServerSocket serverSocketSMTP = new ServerSocket(this.m_port);
                this.socket = serverSocketSMTP.accept();
                System.out.println("Début de connexion");

                this.createIO();

                ObjetSmtpConnecte object = new ObjetSmtpConnecte(this);
                object.Launch();

                this.socket.close();
                if (socket.isClosed()) {
                    serverSocketSMTP.close();
                    System.out.println("Fin de Connexion");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
