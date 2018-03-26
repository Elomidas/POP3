package Main;

import Controller.*;
import Model.MailBox.Mailbox;
import Model.MyLogger.MyLogger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Logger;


public class Main extends Application {

    /**
     * Primary Stage utilisé
     */
    private Stage primaryStage;

    /**
     * BorderPane utilisé
     */
    private BorderPane rootLayout;

    /**
     * Controlleur associé à la connexion
     */
    private Controller controller;

    /**
     * Logs utilisé pour notre application
     */
    protected Logger logs;

    /**
     * Constructeur par défaut
     */
    public Main(){
        logs = MyLogger.getLogger("./logs/MainTCP.log");
        controller = new Controller_Connexion();
    }

    /**
     *
     * @return
     */
    public Logger getLogs() {
        return logs;
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
    private void initRootLayout(){
        try {
            FXMLLoader loader = new FXMLLoader();

            loader.setLocation(Main.class.getResource("../View/Root.fxml"));
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
     * Affiche le contenu de la fenêtre de connexion
     * @param rootLayout
     * @param main
     */
    private void afficherConnexion(BorderPane rootLayout, Main main){
        Mailbox mailbox = controller.getMailbox();
        controller = new Controller_Connexion();
        afficherContenu(rootLayout, main, "../View/Connexion.fxml", mailbox);
    }

    /**
     *
     * @param rootLayout
     * @param main
     */
    private void afficherClient(BorderPane rootLayout, Main main){
        Mailbox mailbox = controller.getMailbox();
        controller = new Controller_Client();
        afficherContenu(rootLayout, main, "../View/Client.fxml", mailbox);
    }

    /**
     * Affiche le contenu de la fenetre
     * Initialise le controlleur
     * @param rootLayout
     * @param main
     * @param root
     */
    private void afficherContenu(BorderPane rootLayout, Main main, String root, Mailbox mailbox){
        try
        {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource(root));
            AnchorPane contenu = loader.load();

            controller = loader.getController();
            controller.setMain(main, mailbox);

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
        this.primaryStage.setTitle("Client POP3");

        afficherClient(rootLayout, this);
    }

    /**
     * Gestion du retour au menu de connexion
     */
    public void RetourConnexion(){
        this.primaryStage.setTitle("Fenêtre de connexion");

        afficherConnexion(rootLayout, this);
    }

    /**
     * Redéfintion de la fonction stop appelée lors du clic sur la croix rouge
     */
    @Override
    public void stop(){
        controller.FinSession();
    }

    /**
     * Fonction main appelée pour lancer l'application
     * @param args
     */
    public static void main(String[] args){
        launch(args);
    }
}