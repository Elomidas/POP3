package Main;

import Model.Connexions.*;
public class Main {
    final static int portPOP3 = 1210;
    final static int portPOP3S = 1211;
    final static int portSMTP = 1212;

    //TODO problème lors de l'envoi du mail, lorsque reception le mail est bien écrit mais si on réouvre une session il n'apparait pas et se suppr
    public static void main(String[] args) {
        try{
            ConnexionPOP3 connexionPOP3 = new ConnexionPOP3(portPOP3);
            connexionPOP3.start();
            ConnexionPOP3S connexionPOP3S = new ConnexionPOP3S(portPOP3S);
            connexionPOP3S.start();
            ConnexionSMTP connexionSMTP = new ConnexionSMTP(portSMTP);
            connexionSMTP.start();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
