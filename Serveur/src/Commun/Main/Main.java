package Commun.Main;

import Commun.Connexions.*;
public class Main {
    final static int portPOP3 = 1210;
    final static int portPOP3S = 1211;
    final static int portSMTP = 1212;

    public static void main(String[] args) {
        try{
            ConnexionPOP3 connexionPOP3 = new ConnexionPOP3();
            connexionPOP3.start();
            ConnexionPOP3S connexionPOP3S = new ConnexionPOP3S();
            connexionPOP3S.start();
            ConnexionSMTP connexionSMTP = new ConnexionSMTP();
            connexionSMTP.start();


//            ObjetConnecteSecurise objetConnecteSecurise = new ObjetConnecteSecurise(portPOP3S);
//            ObjetSmtpConnecte objetConnecteSMTP = new ObjetSmtpConnecte(portSMTP);
//            ObjetConnecte objetConnecte =new ObjetConnecte(portPOP3);
//            System.out.println("Lancement du serveur POP3 sur le port " + portPOP3);
//            objetConnecte.start();
//            System.out.println("Lancement du serveur POP3S sur le port " + portPOP3S);
//            objetConnecteSecurise.start();
//            System.out.println("Lancement du serveur SMTP sur le port " + portSMTP);
//            objetConnecteSMTP.start();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
