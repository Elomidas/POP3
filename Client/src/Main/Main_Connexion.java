package Main;

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
     * Main utilisé par le client après connexion
     */
    private Main_Client _mainClient;

    /**
     * Logs utilisé pour notre application
     */
    protected MyLogger logs;

    /**
     * Constructeur par défaut
     */
    public Main_Connexion(){
        logs = new MyLogger();
    }

    public Main_Connexion(MyLogger logs){
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
        afficherConnexion(rootLayout, main, "../View/Connexion.fxml");
    }

    /**
     * Affiche le contenu de la fenetre
     * Initialise le controlleur
     * @param rootLayout
     * @param main
     * @param root
     */
    private void afficherConnexion(BorderPane rootLayout, Main_Connexion main, String root){
        try
        {
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

    /**
     * Lance la fenetre du client lors d'une connexion réussie
     */
    public void lancerClient(){
        _mainClient = new Main_Client(_controllerConnexion.getMailbox(), logs);
        _mainClient.start(primaryStage);
    }

    /**
     * Redéfintion de la fonction stop appelée lors du clic sur la croix rouge
     */
    @Override
    public void stop(){
        System.out.println("Dans le main connexion!!!");
        _controllerConnexion.close();
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
