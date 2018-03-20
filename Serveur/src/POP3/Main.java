package POP3;

import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    final static int port = 1210;

    public static void main(String[] args) {
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Lancement du serveur sur le port " + port);
            while (true){
                Socket socketClient = serverSocket.accept();
                Tcp t = new Tcp(socketClient);
                t.start();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
