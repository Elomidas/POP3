package SMTP;

import Commun.Connexion;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class Smtp extends Connexion {

    protected Socket socket;

    public Smtp(Socket socket){
        super(socket);
        this.socket = socket;
    }

    public void run() {
        try {
            System.out.println("Connexion d'un nouveau client: "+socket.getInetAddress().getHostAddress());
            send(ReponseServeur.SMTP_SERVER_READY);
            m_input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            m_output = new PrintStream(socket.getOutputStream());

            ObjetSmtpConnecte object = new ObjetSmtpConnecte(this);
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
