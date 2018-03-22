package Commun;

import Commun.Connexion;
import POP3.ObjetConnecteSecurise;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class Tcp extends Connexion {

    private Socket socket;

    public Tcp(Socket socket){
        super(socket);
        this.socket = socket;
    }

    public void run(){
        try {
            System.out.println("Début de connexion");

            m_input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            m_output = new PrintStream(socket.getOutputStream());

            ObjetConnecteSecurise object = new ObjetConnecteSecurise(this);
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
