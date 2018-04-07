package Controller;

import View.Main;
import Model.MailBox.MailException;
import Model.MailBox.Mailbox;
import javafx.scene.control.Alert;

import java.util.logging.Level;

public abstract class Controller {

    /*  ###
     *  # VARIABLES
     *  ###
     */

    /**
     * Main utilisé dans l'application
     */
    protected Main main;

    /**
     * Boite mail à laquelle on est connecté
     */
    protected Mailbox mailbox;

    /*  ###
     *  # FUNCTIONS
     *  ###
     */

    /**
     * Fonction appelée par le main lors d'un clic sur la croix rouge
     */
    public void FinSession() {
        try {
            mailbox.Close();
            main.getLogs().info("Session ending.");
        } catch (MailException e) {
            //gestion erreur de connexion dans les logs
            main.getLogs().log(Level.SEVERE, "An error occurred while session ending", e);
            //affichage message erreur à l'utilisateur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Une erreur est survenue.");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    public abstract void setMain(Main main, Mailbox mailbox);

    /**
     * @return mailBox à laquelle on est connecté
     */
    public Mailbox getMailbox() {
        return mailbox;
    }
}
