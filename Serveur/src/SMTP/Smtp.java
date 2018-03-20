package SMTP;

import POP3.ObjetConnecteSecurise;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class Smtp extends Thread {

    protected Socket socket;
    protected BufferedReader m_input;
    protected PrintStream m_output;

    public Smtp(Socket socket){
        this.socket = socket;
    }

    public void run() {
        try {
            System.out.println("Connexion d'un nouveau client: "+socket.getInetAddress().getHostAddress());
            send(ReponseServeur.SMTP_SERVER_READY);
            m_input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            m_output = new PrintStream(socket.getOutputStream());

            socket.close();

            if(socket.isClosed()) {
                System.out.println("Fin de Connexion");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String message) {
        m_output.println(message + "\r");
        m_output.flush();
    }
}
