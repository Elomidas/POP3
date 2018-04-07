package Controller;

import Main.Main;
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
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Controlleur lié à la fenetre du client
 */
public class Controller_Client extends Controller{

    /**
     * Récupération du tabPane
     * Utilisé ici pour la gestion des onglets
     */
    @FXML
    private TabPane _tabPane;

    /**
     * TextFlow avec le contenu du mail
     */
    @FXML
    private TextFlow _txtFlow;

    /**
     * Pagination de la réception de mails
     */
    @FXML
    private Pagination _pagination;

    /**
     * Bouton pour répondre à un mail
     */
    @FXML
    private Button _btnRepondre;

    /**
     * Bouton pour supprimer un mail
     */
    @FXML
    private Button _btnSuppr;


    /**
     * Bouton pour envoyer un mail
     */
    @FXML
    private Button _btnEnvoi;

    /**
     * Champ du destinataire
     */
    @FXML
    private TextField _tfDestinataire;

    /**
     * Champ de l'objet
     */
    @FXML
    private TextField _tfObjet;

    /**
     * Champ du contenu
     */
    @FXML
    private TextArea _tfContenu;

    /**
     * Adresse mail de la personne connectée
     */
    @FXML
    private Text _txtMailEmetteur;

    /**
     * Bouton de déconnexion
     */
    @FXML
    private Button _btnDeconnexion;

    /**
     * Bouton pour actualiser la liste de mails
     */
    @FXML
    private Button _btnActualiser;

    /**
     * Hashmap reliant ID du mail et ligne de la pagination correspondant
     */
    private HashMap<String, HBox> m_ligne;


    /**
     * Constructeur
     */
    public Controller_Client(){
        m_ligne = new HashMap<>();
    }

    /**
     *
     * @return nombre de messages par page
     */
    private int itemsPerPage() {
        return 10;
    }

    /**
     * Création de la pagination
     */
    private void creationPagination(boolean isUpdate){
        if(!isUpdate)
            _pagination.getStyleClass().add(Pagination.STYLE_CLASS_BULLET);
        int nbPages = (int)Math.ceil(mailbox.getMailNumber()/(float)itemsPerPage());
        _pagination.setPageCount(nbPages);
        _pagination.setPageFactory(pageIndex -> {
            if(pageIndex >= nbPages)
                return null;
            else
                return createPage(recuperationMails(pageIndex, isUpdate));
        });
    }

    /**
     *
     * @param indexPage index de la page à charger
     * @return tableau de mails qui a été récupéré
     */
    private Mail[] recuperationMails(int indexPage, boolean isUpdate){
        Mail[] mails = null;
        try {
            if(isUpdate){
                mails = mailbox.getMailsUpdated(indexPage*itemsPerPage(), itemsPerPage());
                main.getLogs().info("Update ended successfully .");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Update terminé !");
                alert.setContentText("Update réalisé avec succès.");
                alert.show();
                System.out.println("test update OK");
            }
            else{
                mails = mailbox.getMails(indexPage*itemsPerPage(), itemsPerPage());
                main.getLogs().info("Messages collected.");
            }
        } catch (MailException e) {
            //gestion erreur de connexion dans les logs
            main.getLogs().log(Level.SEVERE, "Unable to collect/update messages.", e);
            //affichage message erreur à l'utilisateur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Une erreur est survenue !");
            alert.setContentText("Impossible de charger les messages.");
            alert.show();
        }
        return mails;
    }

    /**
     *
     * @param mails liste des mails récupérés
     * @return VerticalBox contenant les mails de la page
     */
    private VBox createPage(Mail[] mails) {
        VBox box = new VBox(15);
        int length = mails.length;
        for (int i = 0; (i < length) && (mails[i] != null); i++) {
            HBox element = new HBox();
            final String ind;

            Hyperlink link = new Hyperlink("Mail :");
            Label destinataire = new Label(mails[i].getFrom());
            Label objet = new Label(mails[i].getSubject());
            Text contenu = new Text(mails[i].getMessage());

            element.getChildren().addAll(link, destinataire, objet);
            element.setSpacing(25);

            m_ligne.put(mails[i].getID(), element);

            box.getChildren().add(element);

            if(mails[i].Deleted()){
                for(Node mail : element.getChildren()){
                    mail.setStyle("-fx-text-fill : red;");
                }
            }

            ind = mails[i].getID();
            link.setOnMouseClicked(MouseEvent -> updateTF(ind, destinataire.getText(), objet.getText(), contenu));
        }
        return box;
    }

    /**
     * Met à jour notre pagination en cas d'appui sur le bouton actualiser
     */
    private void UpdatePagination(){
        creationPagination(true);
    }

    /**
     * Initialise
     */
    @FXML
    private void initialize(){
        _btnRepondre.setDisable(true);
        _btnSuppr.setDisable(true);
    }

    /**
     * Vérifie si l'on peut envoyer le mail correctement
     * et si tous les champs sont corrects
     */
    private void TestEnvoiMail(){
        Platform.runLater(() ->{
            String destinataire;
            String objet;
            String contenu;
            destinataire = _tfDestinataire.getText();
            objet = _tfObjet.getText();
            contenu = _tfContenu.getText();

            if(TestRegex.CheckMails(destinataire)){
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
                //gestion erreur de connexion dans les logs
                main.getLogs().log(Level.SEVERE, "Invalid target.");
                //affichage message erreur à l'utilisateur
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Erreur destinataire(s) !");
                alert.setContentText("Veuillez renseigner une ou plusieurs adresse(s) mail valide(s).");
                alert.show();
            }
        });
    }

    /**
     * Envoie un mail
     * @param destinataire mail du ou des destinataires
     * @param objet objet du ou des mails
     * @param contenu contenu du ou des mails
     */
    private void EnvoiMail(String destinataire,String objet,String contenu){
        try {
            List<String> adresses;
            adresses = mailbox.SendMail(destinataire, objet, contenu);
            if(adresses.isEmpty()){
                main.getLogs().info("Mail sent.");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Envoi réussi !");
                alert.setContentText("Message envoyé avec succès.");
                alert.show();
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur destinataire !");
                alert.setContentText("Les destinataires suivants sont invalides :\n");
                for(String adresse : adresses){
                    //gestion erreur de connexion dans les logs
                    main.getLogs().log(Level.SEVERE, adresse + " isn't a valid adress. Unable to send this mail.");
                    //affichage message erreur à l'utilisateur
                    alert.setContentText(alert.getContentText() + adresse + "\n");
                }
                alert.show();
            }
        } catch (MailException e) {
            //gestion erreur de connexion dans les logs
            main.getLogs().log(Level.SEVERE, "Unable to send mail.", e);
            //affichage message erreur à l'utilisateur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Une erreur est survenue !");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    /**
     * Mettre à jour le texteFlow avec le contenu des messages
     * @param ind indice du mail sélectionné
     * @param destinataire emmeteur du mail
     * @param objet objet du mail
     * @param contenu contenu du mail
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

    /**
     * Met à jour les boutons répondre et supprimer en fonction de lé sélection d'un message
     * Initialise ces boutons pour leur assigner une tache à effectuer en cas d'action du bouton
     * @param ind indice du mail sélectionné
     * @param destinataire emmeteur du mail
     * @param objet objet du mail
     * @param contenu contenu du mail
     */
    private void updateBTN(String ind, String destinataire, String objet, String contenu){
        Platform.runLater(() -> {
            _btnRepondre.setDisable(false);
            _btnSuppr.setDisable(false);
            _btnRepondre.setOnMouseClicked(mouseEvent -> RepondreMail(destinataire, objet, contenu));
            _btnSuppr.setOnMouseClicked(mouseEvent -> SupprMail(ind));
        });
    }

    /**
     * Supprime le mail sélectionné
     * CSS pour l'afficher en rouge lorsqu'il est marqué comme supprimé
     * @param ind indice du message à supprimer
     */
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
                mailbox.DeleteMail(ind);
                for(Node mail : m_ligne.get(ind).getChildren()){
                    mail.setStyle("-fx-text-fill : red;");
                }
                main.getLogs().info("Mail deleted.");
            } catch (MailException e) {
                //gestion erreur de connexion dans les logs
                main.getLogs().log(Level.SEVERE, "Unable to delete mail.", e);
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

    /**
     * Permet de répondre à un mail lors du clic sur le bouton répondre
     * Remplis tous les champs nécessaires dans la partie envoi
     * @param destinataire emmeteur du mail
     * @param objet objet du mail
     * @param contenu contenu du mail
     */
    private void RepondreMail(String destinataire, String objet, String contenu){
        _tabPane.getSelectionModel().select(1);
        _tfDestinataire.setText(destinataire);
        objet = "Re : " + objet;
        _tfObjet.setText(objet);
        contenu = "\n\n\n\n\n========== Ancien message ==========\n\n" + contenu;
        _tfContenu.setText(contenu);
    }

    /**
     * Se déconnecte lors du clic sur le bouton déconnexion
     */
    private void Deconnexion(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Déconnexion !");
        alert.setContentText("Etes-vous sûr de vouloir vous déconnecter ?" );
        ButtonType btnOui = new ButtonType("Oui");
        ButtonType btnNon = new ButtonType("Non");
        alert.getButtonTypes().setAll(btnOui, btnNon);
        Optional<ButtonType> resultat = alert.showAndWait();

        if(resultat.get() == btnOui) {
            FinSession();
            main.RetourConnexion();
        }
        else
            alert.close();
    }

    /**
     * Synchronisation du main avec le controlleur
     * @param main cette classe ci
     */
    public void setMain(Main main, Mailbox mailbox)
    {
        super.main= main;
        super.mailbox = mailbox;
        _txtMailEmetteur.setText(super.mailbox.getUser());
        creationPagination(false);

        _btnEnvoi.setOnMouseClicked(mouseEvent -> TestEnvoiMail());
        _btnDeconnexion.setOnMouseClicked(mouseEvent -> Deconnexion());
        _btnActualiser.setOnMouseClicked(mouseEvent -> UpdatePagination());
    }
}