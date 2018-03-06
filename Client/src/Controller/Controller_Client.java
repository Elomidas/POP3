package Controller;

import Main.Main_Client;
import Model.MailBox.Mail;
import Model.MailBox.MailException;
import Model.MailBox.Mailbox;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import Utilities.TestRegex;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;

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

    private Mailbox _mailbox;
    

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

    private void creationPagination(){
        int nbPages = (int)Math.ceil(_mailbox.getMailNumber()/(float)itemsPerPage());
        _pagination.setPageCount(nbPages);
        _pagination.setPageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer pageIndex) {
                if(pageIndex >= nbPages)
                    return null;
                else
                    return createPage(pageIndex,recuperationMails(pageIndex));
            }
        });
    }

    //Attention vérification indice à faire
    private Mail[] recuperationMails(int indexPage){
        Mail[] mails = null;
        try {
            mails = _mailbox.getMails(indexPage*itemsPerPage(), itemsPerPage());
        } catch (MailException e) {
            //gestion erreur de connexion dans les logs
            //TODO
            //affichage message erreur à l'utilisateur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Une erreur est survenue !");
            alert.setContentText(e.getMessage());
            alert.show();
        }
        return mails;
    }

    /*
    Crée une nouvelle page
     */
    private VBox createPage(int pageIndex, Mail[] mails) {
        VBox box = new VBox(15);
        //int page = pageIndex * itemsPerPage();
        int length = mails.length;
        for (int i = 0; (i < length) && (mails[i] != null); i++) {
            HBox element = new HBox();
            final String ind;

            Hyperlink link = new Hyperlink("Mail " + mails[i].getID() + " :");
            //link.setVisited(true);
            Label destinataire = new Label(mails[i].getFrom());
            Label objet = new Label(mails[i].getSubject());
            Text contenu = new Text(mails[i].getMessage());

            element.getChildren().addAll(link, destinataire, objet);

            element.setSpacing(25);
            box.getChildren().add(element);

            ind = mails[i].getID();
            link.setOnMouseClicked(MouseEvent -> updateTF(ind, destinataire.getText(), objet.getText(), contenu));
        }
        return box;
    }

    /*
    Initialise la fenêtre, en particulier la pagination
     */
    @FXML
    private void initialize(){

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
        try {
            _mailbox.SendMail(destinataire, objet, contenu);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Envoi réussi !");
            alert.setContentText("Message envoyé avec succès.");
            alert.show();
        } catch (MailException e) {
            //gestion erreur de connexion dans les logs
            //todo
            //affichage message erreur à l'utilisateur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Une erreur est survenue !");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    /*
    Mettre à jour le texteFlow avec le contenu des messages
     */
    private void updateTF(String ind, String destinataire, String objet, Text contenu){
        Platform.runLater(() -> {
            //Mise à jour tu textFlow
            _txtFlow.getChildren().clear();
            _txtFlow.getChildren().add(contenu);

            //On met à jour les boutons uniquement si un message a été sélectionné
            updateBTN(ind, destinataire, objet, contenu.getText());
        });

    }

    private void updateBTN(String ind, String destinataire, String objet, String contenu){
        Platform.runLater(() -> {
            _btnRepondre.setDisable(false);
            _btnSuppr.setDisable(false);
            _btnRepondre.setOnMouseClicked(mouseEvent -> RepondreMail(destinataire, objet, contenu));
            _btnSuppr.setOnMouseClicked(mouseEvent -> SupprMail(ind));
        });

    }

    private void UpdatePagination(){
        int pageActuelle = _pagination.getCurrentPageIndex();
        //ATTENTIOn indice
        for (int i=pageActuelle; i<_pagination.getMaxPageIndicatorCount()+1;i++){
            _pagination.getPageFactory().call(i);
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Update terminé !");
        alert.setContentText("Update réalisé avec succès.");
        alert.show();
    }

    private void SupprMail(String ind){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Suppression message !");
        alert.setContentText("Etes-vous sûr de vouloir supprimer le mail " + ind + " ?");
        ButtonType btnOui = new ButtonType("Oui");
        ButtonType btnNon = new ButtonType("Non");
        alert.getButtonTypes().setAll(btnOui, btnNon);
        Optional<ButtonType> resultat = alert.showAndWait();

        if(resultat.get() == btnOui) {
            try {
                _pagination..getChildrenUnmodifiable().get(0).setStyle("-fx-text-fill: red");
                _mailbox.DeleteMail(ind);
            } catch (MailException e) {
                //gestion erreur de connexion dans les logs
                //todo
                //affichage message erreur à l'utilisateur
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Une erreur est survenue !");
                alert.setContentText(e.getMessage());
                alert.show();
            }
        }
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

        if(resultat.get() == btnOui) {
            _mainClient.RetourConnexion();
            FinSession();
        }
        else
            alert.close();
    }

    public void FinSession(){
        try {
            _mailbox.Close();
        } catch (MailException e) {
            //gestion erreur de connexion dans les logs
            //todo
            //affichage message erreur à l'utilisateur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Une erreur est survenue !");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    /*
    Synchronisation du main avec le controlleur
     */
    public void SetMain(Main_Client mainClient)
    {
        _mainClient = mainClient;
        _mailbox = _mainClient.getMailbox();
        _txtMailEmetteur.setText(_mailbox.getUser());

        creationPagination();

        _btnEnvoi.setOnMouseClicked(mouseEvent -> TestEnvoiMail());
        _btnDeconnexion.setOnMouseClicked(mouseEvent -> Deconnexion());
        _btnActualiser.setOnMouseClicked(mouseEvent -> UpdatePagination());
    }
}
