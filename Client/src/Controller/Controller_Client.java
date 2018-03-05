package Controller;

import Main.Main_Client;
import Utilities.TestRegex;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Optional;

public class Controller_Client {

    //Récupération du tabPane
    @FXML
    private TabPane _tabPane;

    //Récupération des objets relatifs à la réception de mail
    @FXML
    private TextFlow _txtFlow;

    @FXML
    private Pagination _pagination;

    @FXML
    private Button _btnRepondre;

    @FXML
    private Button _btnSuppr;


    //Récupération des objets relatifs à l'envoi de mails
    @FXML
    private Button _btnEnvoi;

    @FXML
    private TextField _tfDestinataire;

    @FXML
    private TextField _tfObjet;

    @FXML
    private TextArea _tfContenu;

    @FXML
    private Text _txtMailEmetteur;

    @FXML
    private Button _btnDeconnexion;

    @FXML
    private Button _btnActualiser;


    private Main_Client _mainClient;
    

    /*
    Constructeur
     */
    public Controller_Client(){

    }

    /*
    Nombre de messages par page
     */
    private int itemsPerPage() {
        return 10;
    }

    /*
    Crée une nouvelle page
     */
    private VBox createPage(int pageIndex) {
        VBox box = new VBox(15);
        int page = pageIndex * itemsPerPage();
        //String headerMail = "";
        for (int i = page; i < page + itemsPerPage(); i++) {
            HBox element = new HBox();
            final int indMsg;

            Hyperlink link = new Hyperlink("Message " + (i + 1));
            //link.setVisited(true);
            Label destinataire = new Label("desinataire@mail.fr");
            Label objet = new Label("fakeObjet");
            Text contenu = new Text((i+1) + " " + (i+1) +" " + (i+1) + "\n" + (i+1) + " " + (i+1) + "\n" + (i+1));

            element.getChildren().addAll(link, destinataire, objet);

            element.setSpacing(25);
            box.getChildren().add(element);

            indMsg = i +1;
            link.setOnMouseClicked(MouseEvent -> updateTF(indMsg, destinataire.getText(), objet.getText(), contenu));


        }
        return box;
    }

    /*
    Initialise la fenêtre
     */
    @FXML
    private void initialize(){
        _txtMailEmetteur.setText("test@gmail.com");
        _pagination.setPageFactory((Integer pageIndex) -> createPage(pageIndex));
    }

    /*
    Envoi un mail lors du clic sur le bouton correspondant
     */
    private void TestEnvoiMail(){
        Platform.runLater(() ->{
            String destinataire;
            String objet;
            String contenu;
            destinataire = _tfDestinataire.getText();
            objet = _tfObjet.getText();
            contenu = _tfContenu.getText();

            if(TestRegex.CheckMail(destinataire)){

                if(!objet.equals(""))
                {
                    EnvoiMail(destinataire, objet, contenu);
                }
                else {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Pas d'objet !");
                    confirm.setContentText("Etes-vous sûr de vouloir envoyer le mail sans objet ?");
                    ButtonType btnOui = new ButtonType("Oui");
                    ButtonType btnNon = new ButtonType("Non");
                    confirm.getButtonTypes().setAll(btnOui, btnNon);
                    Optional<ButtonType> resultat = confirm.showAndWait();

                    if(resultat.get() == btnOui){
                        EnvoiMail(destinataire, objet, contenu);
                    }
                    else
                        confirm.close();
                }

            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Erreur destinataire !");
                alert.setContentText("Veuillez renseigner une adresse mail valide.");
                alert.show();

            }

        });
    }

    private void EnvoiMail(String destinataire,String objet,String contenu){
        //Envoi du message
        _mainClient.EnvoiMail(destinataire, objet, contenu);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Envoi réussi !");
        alert.setContentText("Message envoyé avec succès.");
        alert.show();

    }

    /*
    Mettre à jour le texteFlow avec le contenu des messages
     */
    private void updateTF(int ind, String destinataire, String objet, Text contenu){
        Platform.runLater(() -> {
            //Mise à jour tu textFlow
            _txtFlow.getChildren().clear();
            _txtFlow.getChildren().add(contenu);

            //On met à jour les boutons uniquement si un message a été sélectionné
            updateBTN(ind, destinataire, objet, contenu.getText());
        });

    }

    private void updateBTN(int ind, String destinataire, String objet, String contenu){
        Platform.runLater(() -> {
            _btnRepondre.setDisable(false);
            _btnSuppr.setDisable(false);
            _btnRepondre.setOnMouseClicked(mouseEvent -> RepondreMail(destinataire, objet, contenu));
            _btnSuppr.setOnMouseClicked(mouseEvent -> SupprMail(ind));
        });

    }

    private void UpdatePagination(){

    }

    private void SupprMail(int ind){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Suppression message !");
        alert.setContentText("Etes-vous sûr de vouloir supprimer le mail " + ind + " ?");
        ButtonType btnOui = new ButtonType("Oui");
        ButtonType btnNon = new ButtonType("Non");
        alert.getButtonTypes().setAll(btnOui, btnNon);
        Optional<ButtonType> resultat = alert.showAndWait();

        if(resultat.get() == btnOui)
            _mainClient.SupprMail(ind);
        else
            alert.close();
    }

    private void RepondreMail(String destinataire, String objet, String contenu){
        _tabPane.getSelectionModel().select(1);
        _tfDestinataire.setText(destinataire);
        objet = "Re : " + objet;
        _tfObjet.setText(objet);
        contenu = "\n\n\n\n\n========== Ancien message ==========\n\n" + contenu;
        _tfContenu.setText(contenu);
    }

    private void Deconnexion(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Déconnexion !");
        alert.setContentText("Etes-vous sûr de vouloir vous déconnecter ?" );
        ButtonType btnOui = new ButtonType("Oui");
        ButtonType btnNon = new ButtonType("Non");
        alert.getButtonTypes().setAll(btnOui, btnNon);
        Optional<ButtonType> resultat = alert.showAndWait();

        if(resultat.get() == btnOui)
            _mainClient.RetourConnexion();
        else
            alert.close();
    }

    /*
    Synchronisation du main avec le controlleur
     */
    public void SetMain(Main_Client mainClient)
    {
        _mainClient = mainClient;
        _btnEnvoi.setOnMouseClicked(mouseEvent -> TestEnvoiMail());
        _btnDeconnexion.setOnMouseClicked(mouseEvent -> Deconnexion());
        _btnActualiser.setOnMouseClicked(mouseEvent -> UpdatePagination());
    }
}
