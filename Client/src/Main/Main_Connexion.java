package Main;

import Controller.Controller_Connexion;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;


public class Main_Connexion extends Application {

    protected Stage primaryStage;
    protected BorderPane rootLayout;
    private Controller_Connexion _controllerConnexion;

    private Main_Client _mainClient;

    public Main_Connexion(){

    }

    public Controller_Connexion getController(){
        return _controllerConnexion;
    }

    @Override
    public void start(Stage primaryStage)
    {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Fenêtre de connexion");

        initRootLayout();

        afficherConnexion(rootLayout, this);
    }

    protected void initRootLayout(){
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();

            loader.setLocation(Main_Connexion.class.getResource("../View/Root.fxml"));
            rootLayout = (BorderPane) loader.load();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void afficherConnexion(BorderPane rootLayout, Main_Connexion main){
        afficherConnexion(rootLayout, main, "../View/Connexion.fxml");
    }

    private void afficherConnexion(BorderPane rootLayout, Main_Connexion main, String root){
        try
        {
            // Charge l'affichage du client.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main_Connexion.class.getResource(root));
            AnchorPane connexion = (AnchorPane) loader.load();

            _controllerConnexion = loader.getController();
            _controllerConnexion.setMain(main);

            rootLayout.setCenter(connexion);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void lancerClient(){
        _mainClient = new Main_Client(_controllerConnexion.getMailbox());
        _mainClient.start(primaryStage);
    }


    @Override
    public void stop(){
        //todo
        //On pense bien à fermer la connexion...etc.
    }

    public static void main(String[] args)
    {
        launch(args);

    }
}
