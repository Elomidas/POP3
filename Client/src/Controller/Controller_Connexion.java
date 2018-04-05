package Controller;

import Main.Main;
import Model.MailBox.MailException;
import Model.MailBox.Mailbox;
import Utilities.TestRegex;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.logging.Level;

/**
 * Controlleur associé à la fenêtre de connexion
 */
public class Controller_Connexion extends Controller {

    /**
     * champ correspondant à l'adresse mail
     */
    @FXML
    private TextField _tfAdresseMail;

    /**
     * champ correspondant au mot de passe
     */
    @FXML
    private TextField _tfMotDePasse;

    /**
     * champ correspondant à l'adresse IP
     */
    @FXML
    private TextField _tfAdresseIP;

    /**
     * Champ correspondant au port POP3
     */
    @FXML
    private TextField _tfPortPOP3;

    /**
     * Champ correspondant au port SMTP
     */
    @FXML
    private TextField _tfPortSMTP;

    /**
     * Bouton utilisé pour se connecter à une boite mail
     */
    @FXML
    private Button _btnConnexion;

    /**
     * Constructeur
     */
    public Controller_Connexion(){
        super.mailbox = new Mailbox();
    }

    /**
     * Redéfinition de la fonction initialisation
     * Par défaut, le bouton est désactivé
     */
    @FXML
    private void initialize(){
        _btnConnexion.setDisable(true);
    }

    /**
     * Fonction utilisée pour se connecter lorsque l'on active le bouton de connection
     * En cas d'erreur, une fenêtre affiche une erreur avec le problème rencontré
     */
    private void connexion(){
        Platform.runLater(() ->{
            //On vérifie que les informations demandées soient cohérentes
            try {
                if(mailbox.joinServer(TestRegex.GetDomain(_tfAdresseMail.getText())))
                {
                    mailbox.setUser(_tfAdresseMail.getText());
                    if(mailbox.Authenticate(_tfMotDePasse.getText())){
                        main.lancerClient();
                        main.getLogs().info("User authenticated, client started.");
                    }
                    else
                    {
                        //gestion erreur de connexion dans les logs
                        main.getLogs().log(Level.SEVERE, "Unable to authenticate.");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Connexion impossible !");
                        alert.setContentText("Nous ne parvenons pas à vous identifier.");
                        alert.show();
                    }
                }
                else
                {
                    //gestion erreur de connexion dans les logs
                    main.getLogs().log(Level.SEVERE, "Unable to join server.");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Impossible de joindre le serveur !");
                    alert.setContentText("Nous ne parvenons pas à joindre le serveur.");
                    alert.show();
                }

            } catch (MailException e) {
                //gestion erreur de connexion dans les logs
                main.getLogs().log(Level.SEVERE, "An error occurred on mailbox.", e);
                //affichage message erreur à l'utilisateur
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Une erreur est survenue !");
                alert.setContentText(e.getMessage());
                alert.show();
            }
        });
    }

    /**
     * Gestion de l'activation du bouton
     * Le bouton sort de l'état désactivé si tous les champs ont été correctement remplis
     */
    private void gestionBtnConnexion(){
        if(TestRegex.CheckMail(_tfAdresseMail.getText())&&
                !_tfMotDePasse.getText().equals(""))
        {
            _btnConnexion.setDisable(false);
        }
    }

    /**
     * Déifnit le main à utiliser
     * @param main Main que l'on utilise
     */
    public void setMain(Main main, Mailbox mailbox) {
        super.main = main;
        _btnConnexion.setOnMouseClicked(MouseEvent -> connexion());
        _btnConnexion.setOnKeyPressed((final KeyEvent ke) ->{
            if(ke.getCode() == KeyCode.ENTER)
                connexion();
        });
        EventHandler<KeyEvent> eventHandlerTF = event -> gestionBtnConnexion();
        _tfAdresseMail.addEventHandler(KeyEvent.ANY, eventHandlerTF);
        _tfMotDePasse.addEventHandler(KeyEvent.ANY, eventHandlerTF);
    }
}
