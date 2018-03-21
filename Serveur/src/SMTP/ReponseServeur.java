package SMTP;

public class ReponseServeur {

    public final static String SMTP_SERVER_READY = "220 Simple Mail Transfer Service Ready";
    public final static String SMTP_500_UNKNOWN_COMMAND = "500 Erreur de syntaxe, commande non reconnue";
    /* Etat */
    public final static String SERVER_READY = "Initialisation";
    public final static String SERVER_CONNEXION = "Connexion";
    public final static String SERVER_IDENTIFICATION = "Identification";
    public final static String SERVER_TRANSACTION = "Transaction de Messagerie";
    public final static String SERVER_ENVOIE = "Envoie de Message";
    public final static String SERVER_LECTURE = "Lecture des Lignes";
}
