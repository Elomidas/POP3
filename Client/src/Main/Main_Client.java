package Main;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import Controller.*;

public class Main_Client extends Main_Connexion {

    private Controller_Client _controllerClient;
    //private Client _client;

    @Override
    public void start(Stage primaryStage) {
        super.primaryStage = primaryStage;
        super.primaryStage.setTitle("Client POP3");

        //_client = new Client();

        initRootLayout();

        afficheClient();
    }

    /*
    public Client getClient()
    {
        return this._client;
    }
    */

    public Controller_Client getController()
    {
        return this._controllerClient;
    }

    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return super.primaryStage;
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

    public void EnvoiMail(String destinataire, String objet, String contenu){
        //Envoi du mail depuis couche métier
    }

    public void SupprMail(int ind){
        //Suppression du mail depuis couche métier
    }
}
