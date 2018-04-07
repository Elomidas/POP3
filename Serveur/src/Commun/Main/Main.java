package Commun.Main;

import Commun.Connexions.*;

public class Main {

    private final static String[] DOMAINS = new String[]{"email.com", "email.fr"};
    public static void main(String[] args) {
        String domain = DOMAINS[0];
        if(args.length > 0) {
            for(String dom : DOMAINS) {
                if(args[0].equalsIgnoreCase(dom)) {
                    domain = dom;
                }
            }
        }
        System.out.println("Lancement du domaine " + domain);
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
