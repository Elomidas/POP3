package Commun;

import POP3.ObjetConnecte;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpPOP3 extends Connexion {

    public TcpPOP3(int portPOP3){
        super(portPOP3);
    }

    public void run(){

        try {
            ServerSocket serverSocketPOP3 = new ServerSocket(this.m_port);
            this.socket = serverSocketPOP3.accept();

            System.out.println("Début de connexion");

            m_input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            m_output = new PrintStream(socket.getOutputStream());

            ObjetConnecte object = new ObjetConnecte(this);
            object.Launch();

            socket.close();

            if(socket.isClosed()) {
                System.out.println("Fin de Connexion");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
