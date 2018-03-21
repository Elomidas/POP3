package SMTP;

import java.io.IOException;

public class ObjetSmtpConnecte {

    protected Smtp smtp;
    private String etatServeur;
    private boolean continuer;
    private String input;
    private String reponseServeur;

    public ObjetSmtpConnecte(Smtp smtp){
        this.smtp = smtp;
        this.etatServeur = ReponseServeur.SERVER_READY;

    }

    public void Launch() throws IOException {
        this.etatServeur = ReponseServeur.SERVER_CONNEXION;
        smtp.send(ReponseServeur.SMTP_SERVER_READY);

        while(continuer){
            System.out.println("Wait...");
            input = smtp.receive();
            System.out.println(input + " received");

            String[] explodedCommand = input.split(" ", 2);
            String command = explodedCommand[0].toUpperCase();
            String[] parameters = new String[0];
            if(explodedCommand.length > 1) {
                parameters = explodedCommand[1].split(" ");
            }

            switch (etatServeur) {
                case ReponseServeur.SERVER_CONNEXION:
                    reponseServeur = this.connexion(command, parameters);
                    break;
                case ReponseServeur.SERVER_IDENTIFICATION:
                    reponseServeur = this.identification(command, parameters);
                    break;
                case ReponseServeur.SERVER_TRANSACTION:
                    reponseServeur = this.transaction(command, parameters);
                    break;
                case ReponseServeur.SERVER_ENVOIE:
                    reponseServeur = this.envoie(command, parameters);
                    break;
                case ReponseServeur.SERVER_LECTURE:
                    reponseServeur = this.lecture(command, parameters);
                    break;
                default:
                    reponseServeur = ReponseServeur.SMTP_500_UNKNOWN_COMMAND;
            }
            System.out.println("reponse: "+reponseServeur);
            smtp.send(reponseServeur);
        }
        System.out.println("End of POP3");
    }

    private String connexion(String command, String[] parameters) {
        switch (command){
            case "EHLO":
                return commandeEhlo(parameters);
            case "RSET":
                return commandeRset();
            case "QUIT":
                return commandeQuit();
            default :
                return ReponseServeur.SMTP_500_UNKNOWN_COMMAND;
        }
    }

    private String commandeEhlo(String[] parameters) {
        return "ehlo";
    }

    private String commandeRset() {
        return "rset";
    }

    private String commandeQuit() {
        return "quit";
    }

    private String identification(String command, String[] parameters) {
        return "identification";
    }

    private String transaction(String command, String[] parameters) {
        return "transaction";
    }

    private String envoie(String command, String[] parameters) {
        return "envoie";
    }

    private String lecture(String command, String[] parameters) {
        return "lecture";
    }
}
