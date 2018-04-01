package Model.Connexions;

import Model.Protocols.POP3.ObjetConnecte;
import Model.Protocols.POP3S.ObjetConnecteSecurise;
import Model.Protocols.SMTP.ObjetSmtpConnecte;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class Connexion extends Thread{

    Socket socket;
    ServerSocket serverSocket;
    int port;

    public Connexion(int port){
        this.socket = null;
        this.serverSocket = null;
        this.port = port;
    }

    public abstract void run();

    protected void action(int choice){
        try {
            this.serverSocket = new ServerSocket(port);
            while (true) {
                System.out.println("Attente de connexion au port " + port + ".");
                this.socket = this.serverSocket.accept();

                switch (choice){
                    case 0 :{
                        ObjetConnecte objetConnecte = new ObjetConnecte(this.socket);
                        objetConnecte.start();
                        System.out.println("Début de connexion POP3");
                        break;
                    }
                    case 1 :{
                        ObjetConnecteSecurise objetConnecte = new ObjetConnecteSecurise(this.socket);
                        objetConnecte.start();
                        System.out.println("Début de connexion POP3S");
                        break;
                    }
                    case 2 :{
                        ObjetSmtpConnecte objetConnecte = new ObjetSmtpConnecte(this.socket);
                        objetConnecte.start();
                        System.out.println("Début de connexion SMTP");
                        break;
                    }
                    default : {
                        //Nothing
                        break;
                    }
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

}
