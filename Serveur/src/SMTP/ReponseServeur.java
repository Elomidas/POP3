package SMTP;

public class ReponseServeur {

    public final static String SMTP_SERVER_READY = "220 Simple Mail Transfer Service Ready";
    public final static String SMTP_500_UNKNOWN_COMMAND = "500 Erreur de syntaxe, commande non reconnue";
    public final static String SMTP_250_SERVERDOMAIN = "250 localhost";
    public final static String SMTP_221_CLOSING = "221 fermeture";
    public final static String SMTP_250_OK = "250 OK";
    public final static String SMTP_550_UNKNOWN_USER = "250 utilisateur inconnu";
    public final static String SMTP_354_START_READING = "354 debut de lecture";
    /* Etat */
    public final static String SERVER_READY = "Initialisation";
    public final static String SERVER_CONNEXION = "Connexion";
    public final static String SERVER_IDENTIFICATION = "Identification";
    public final static String SERVER_TRANSACTION = "Transaction de Messagerie";
    public final static String SERVER_ENVOIE = "Envoie de Message";
    public final static String SERVER_LECTURE = "Lecture des Lignes";

    public final static String SMTP_CRLF = "\\r\\n";
}
