package Commun.Main;

import Commun.Connexions.*;
public class Main {

    public final static String DOMAIN = "email.com";
    public final static String DOMAIN2 = "email.fr";
    public static void main(String[] args) {
        String domain = DOMAIN;
        try{
            ConnexionPOP3 connexionPOP3 = new ConnexionPOP3(domain);
            connexionPOP3.start();
            ConnexionPOP3S connexionPOP3S = new ConnexionPOP3S(domain);
            connexionPOP3S.start();
            ConnexionSMTP connexionSMTP = new ConnexionSMTP(domain);
            connexionSMTP.start();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
