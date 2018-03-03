package Controller;

import Main.Main_Connexion;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
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

    public Controller_Connexion(){

    }

    @FXML
    private void initialize(){
        _btnConnexion.setDisable(true);
    }

    private void connexion(){
        Platform.runLater(() ->{
            _main.lancerClient();
        });
    }

    private void gestionBtnConnexion(){
        if(!_tfPort.getText().equals("") &&
                !_tfAdresseMail.getText().equals("") &&
                !_tfAdresseIP.getText().equals("") &&
                !_tfMotDePasse.getText().equals("")){
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
