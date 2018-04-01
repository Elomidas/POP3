package Model.Protocols.SMTP;

class ReponseServeur {

    final static String SMTP_SERVER_READY = "220 Simple MailBox Transfer Service Ready";
    final static String SMTP_500_UNKNOWN_COMMAND = "500 Erreur de syntaxe, commande non reconnue";
    final static String SMTP_250_SERVERDOMAIN = "250 localhost";
    final static String SMTP_221_CLOSING = "221 fermeture";
    final static String SMTP_250_OK = "250 OK";
    final static String SMTP_550_UNKNOWN_USER = "250 utilisateur inconnu";
    final static String SMTP_354_START_READING = "354 debut de lecture";
    /* Etat */
    final static String SERVER_READY = "Initialisation";
    final static String SERVER_CONNEXION = "Model/Connexions";
    final static String SERVER_IDENTIFICATION = "Identification";
    final static String SERVER_TRANSACTION = "Transaction de Messagerie";
    final static String SERVER_ENVOIE = "Envoie de Message";
    final static String SERVER_LECTURE = "Lecture des Lignes";

    final static String SMTP_CRLF = ".";
}
