package Main;

import java.io.IOException;

import Model.MailBox.Mailbox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import Controller.*;

public class Main_Client extends Main_Connexion {

    private Controller_Client _controllerClient;
    private Main_Connexion _mainConnexion;
    private Mailbox _mailbox;

    public Main_Client(){

    }

    public Main_Client(Mailbox mailbox){
        _mailbox = mailbox;
    }

    public Mailbox getMailbox() {
        return _mailbox;
    }

    @Override
    public void start(Stage primaryStage) {
        super.primaryStage = primaryStage;
        super.primaryStage.setTitle("Client POP3");

        //_client = new Client();

        initRootLayout();

        afficheClient();
    }

    /**
     * Initializes the root layout.
     */
    protected void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();

            loader.setLocation(Main_Client.class.getResource("../View/Root.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the person overview inside the root layout.
     */
    private void afficheClient() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main_Client.class.getResource("../View/Client.fxml"));
            AnchorPane personOverview = (AnchorPane) loader.load();


            _controllerClient = loader.getController();
            _controllerClient.SetMain(this);


            // Set person overview into the center of root layout.
            rootLayout.setCenter(personOverview);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop(){
        //todo
        _controllerClient.FinSession();
    }

    public void RetourConnexion(){
        //On indique à la couche métier et au serveur que le client s'est déconnecté

        _mainConnexion = new Main_Connexion();
        _mainConnexion.start(primaryStage);
    }
}
