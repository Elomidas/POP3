package Controller;

import Main.Main_Connexion;
import Model.MailBox.MailException;
import Model.MailBox.Mailbox;
import Uilities.TestRegex;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;


public class Controller_Connexion {

    @FXML
    private TextField _tfAdresseMail;

    @FXML
    private TextField _tfMotDePasse;

    @FXML
    private TextField _tfAdresseIP;

    @FXML
    private TextField _tfPort;

    @FXML
    private Button _btnConnexion;


    private Main_Connexion _main;

    private Mailbox _mailBox;

    public Controller_Connexion(){
        _mailBox = new Mailbox();

    }

    public Mailbox getMailbox(){
        return _mailBox;
    }

    @FXML
    private void initialize(){
        //_btnConnexion.setDisable(true);
    }

    private void connexion(){
        Platform.runLater(() ->{
            //On vérifie que les informations demandées soient cohérentes
            /*
            try {
                if(_mailBox.joinServer(_tfAdresseIP.getText(), Integer.parseInt(_tfPort.getText())))
                {
                    _mailBox.setUser(_tfAdresseMail.getText());
                    if(_mailBox.Authenticate(_tfMotDePasse.getText())){
                        _main.lancerClient();
                    }
                    else
                    {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Connexion impossible !");
                        alert.setContentText("Nous ne parvenons pas à vous identifier.");
                        alert.show();
                    }
                }
                else
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Impossible de joindre le serveur !");
                    alert.setContentText("Nous ne parvenons pas à joindre le serveur.");
                    alert.show();
                }

            } catch (MailException e) {
                //gestion erreur de connexion dans les logs
                //todo
                //affichage message erreur à l'utilisateur
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Une erreur est survenue !");
                alert.setContentText(e.getMessage());
                alert.show();
            }
            */
            try {
                _mailBox.setUser(_tfAdresseMail.getText());
                _main.lancerClient();
            } catch (MailException e) {
                e.printStackTrace();
            }
        });
    }

    private void gestionBtnConnexion(){
        if((_tfPort.getText().matches("[0-9]+")) &&
                TestRegex.CheckMail(_tfAdresseMail.getText()) &&
                TestRegex.CheckIP(_tfAdresseIP.getText()) &&
                !_tfMotDePasse.getText().equals(""))
        {
            _btnConnexion.setDisable(false);
        }
    }

    public void setMain(Main_Connexion main) {
        this._main = main;
        _btnConnexion.setOnMouseClicked(MouseEvent -> connexion());
        _btnConnexion.setOnKeyPressed((final KeyEvent ke) ->{
            if(ke.getCode() == KeyCode.ENTER)
                connexion();
        });
        EventHandler<KeyEvent> eventHandlerTF = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                //Playing the animation
                gestionBtnConnexion();
            }
        };
        _tfAdresseMail.addEventHandler(KeyEvent.ANY, eventHandlerTF);
        _tfAdresseIP.addEventHandler(KeyEvent.ANY, eventHandlerTF);
        _tfMotDePasse.addEventHandler(KeyEvent.ANY, eventHandlerTF);
        _tfPort.addEventHandler(KeyEvent.ANY, eventHandlerTF);
    }
}
