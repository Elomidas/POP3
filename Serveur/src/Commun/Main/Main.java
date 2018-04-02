package Commun.Main;

import Commun.Connexions.*;
public class Main {

    public final static String SMTP_DOMAIN = "email.com";
    public final static String SMTP_DOMAIN2 = "email.fr";
    public static void main(String[] args) {

        try{
            ConnexionPOP3 connexionPOP3 = new ConnexionPOP3();
            connexionPOP3.start();
            ConnexionPOP3S connexionPOP3S = new ConnexionPOP3S();
            connexionPOP3S.start();
            ConnexionSMTP connexionSMTP = new ConnexionSMTP(SMTP_DOMAIN);
            connexionSMTP.start();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
