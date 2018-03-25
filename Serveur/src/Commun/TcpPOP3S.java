package Commun;

import POP3.ObjetConnecteSecurise;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by tardy on 22/03/2018.
 */
public class TcpPOP3S extends Connexion{

    public TcpPOP3S(int portPOP3S){
        super(portPOP3S);
    }

    public void run(){

            try {

                while (true) {
                    ServerSocket serverSocketPOP3S = new ServerSocket(this.m_port);
                    this.socket = serverSocketPOP3S.accept();

                    System.out.println("Début de connexion");

                    this.createIO();

                    ObjetConnecteSecurise object = new ObjetConnecteSecurise(this);
                    object.Launch();

                    socket.close();
                    if (socket.isClosed()) {
                        serverSocketPOP3S.close();
                        System.out.println("Fin de Connexion");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

    }
}
