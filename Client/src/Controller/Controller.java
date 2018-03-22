package Controller;

import Main.Main;
import Model.MailBox.MailException;
import Model.MailBox.Mailbox;
import javafx.scene.control.Alert;

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
            //Logger logs = _main.getLogs();
            //logs.info("test");
        } catch (MailException e) {
            //gestion erreur de connexion dans les logs
            //todo
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
