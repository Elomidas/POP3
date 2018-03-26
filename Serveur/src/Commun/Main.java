package Commun;

import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    final static int portPOP3 = 1210;
    final static int portPOP3S = 1211;
    final static int portSMTP = 1212;

    public static void main(String[] args) {
        try{

                System.out.println("Lancement du serveur POP3 sur le port " + portPOP3);
                TcpPOP3 tPOP3 = new TcpPOP3(portPOP3);
                tPOP3.start();
                System.out.println("Lancement du serveur POP3S sur le port " + portPOP3S);
                TcpPOP3S tPOP3S = new TcpPOP3S(portPOP3S);
                tPOP3S.start();
                System.out.println("Lancement du serveur SMTP sur le port " + portSMTP);
                TcpSMTP tSMTP = new TcpSMTP(portSMTP);
                tSMTP.start();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
