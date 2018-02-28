package Main;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import Controller.*;

public class Main_Client extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;

    private Controller_Client _controllerClient;
    //private Client _client;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Client Model.Protocols.POP3");

        //_client = new Client();
        _controllerClient = new Controller_Client();

        initRootLayout();

        showPersonOverview();
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
        return primaryStage;
    }

    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
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
    public void showPersonOverview() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main_Client.class.getResource("../View/Client.fxml"));
            AnchorPane personOverview = (AnchorPane) loader.load();


            Controller_Client controllerClient = loader.getController();
            controllerClient.SetMain(this);


            // Set person overview into the center of root layout.
            rootLayout.setCenter(personOverview);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Fonctions autres
    /*
    public boolean Connexion(String pseudo)
    {
        return _client.Connexion(pseudo);
    }

    public void EnvMsg(String msg)
    {
        _client.EnvoiMessage(msg);
    }



    public String RecMsg()
    {
        return this._client.ReceptionMessage();

    }

    public String RecPseudo()
    {
        //System.out.println("test");
        return this._client.ReceptionPseudo();
    }
    */

    public static void main(String[] args) {
        launch(args);
    }

}
