package Main;

import Model.Connexions.*;
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
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
