package Main;

import Controller.Controller_Client;
import Controller.Controller_Connexion;
import Model.MyLogger.MyLogger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;


public class Main_Connexion extends Application {

    /**
     * Primary Stage utilisé
     */
    protected Stage primaryStage;

    /**
     * BorderPane utilisé
     */
    protected BorderPane rootLayout;

    /**
     * Controlleur associé à la connexion
     */
    private Controller_Connexion _controllerConnexion;

    /**
     * Controlleur utilisé poru le client
     */
    private Controller_Client _controllerClient;

    /**
     * Main utilisé par le client après connexion
     */
    //private Main_Client _mainClient;

    /**
     * Indique si l'on traite avec le controller connexion (=1)
     * ou le controlleur client (=2)
     */
    private int _controllerUtilise;

    /**
     * Logs utilisé pour notre application
     */
    protected MyLogger logs;

    /**
     * Constructeur par défaut
     */
    public Main_Connexion(){
        logs = new MyLogger();
        _controllerUtilise =1;
    }

    public Main_Connexion(MyLogger logs){
        _controllerUtilise =1;
        this.logs = logs;
    }

    /**
     *
     * @return
     */
    public MyLogger getLogs() {
        return logs;
    }

    /**
     *
     * @return Controlleur associé
     */
    public Controller_Connexion getController(){
        return _controllerConnexion;
    }

    /**
     * Redéfinition de la fonction Start
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage)
    {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Fenêtre de connexion");

        initRootLayout();

        afficherConnexion(rootLayout, this);
    }

    /**
     * Initialise notre fenêtre avec le BorderPane
     * correspondant au fond de notre affichage
     */
    protected void initRootLayout(){
        try {
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

    /**
     * Affiche le contenu de la fenêtre
     * @param rootLayout
     * @param main
     */
    private void afficherConnexion(BorderPane rootLayout, Main_Connexion main){
        afficherContenu(rootLayout, main, "../View/Connexion.fxml");
    }

    /**
     * Affiche le contenu de la fenetre
     * Initialise le controlleur
     * @param rootLayout
     * @param main
     * @param root
     */
    private void afficherContenu(BorderPane rootLayout, Main_Connexion main, String root){
        try
        {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main_Connexion.class.getResource(root));
            AnchorPane contenu = (AnchorPane) loader.load();

            if(root.split("/")[2].equals("Connexion.fxml")){
                _controllerConnexion = loader.getController();
                _controllerConnexion.setMain(main);
            }
            else if(root.split("/")[2].equals("Client.fxml")){
                _controllerClient = loader.getController();
                _controllerClient.setMain(main, _controllerConnexion.getMailbox());
            }

            rootLayout.setCenter(contenu);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Lance la fenetre du client lors d'une connexion réussie
     */
    public void lancerClient(){
        /*
        _mainClient = new Main_Client(_controllerConnexion.getMailbox(), logs);
        _mainClient.start(primaryStage);
           */
        this._controllerUtilise =2;
        this.primaryStage.setTitle("Client POP3");

        afficherContenu(rootLayout, this, "../View/Client.fxml");

    }

    /**
     * Gestion du retour au menu de connexion
     */
    public void RetourConnexion(){
        this._controllerUtilise =1;
        this.primaryStage.setTitle("Fenêtre de connexion");

        afficherContenu(rootLayout, this, "../View/Connexion.fxml");
    }

    /**
     * Redéfintion de la fonction stop appelée lors du clic sur la croix rouge
     */
    @Override
    public void stop(){
        if(_controllerUtilise == 1)
            _controllerConnexion.FinSession();
        else if(_controllerUtilise ==2)
            _controllerClient.FinSession();
    }

    /**
     * Fonction main appelée pour lancer l'application
     * @param args
     */
    public static void main(String[] args)
    {
        launch(args);
    }
}
