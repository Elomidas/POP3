package Controller;

import Main.Main_Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

public class Controller_Client {

    //Récupération des objets relatifs à la réception de mail
    @FXML
    private TextFlow _txtFlow;

    @FXML
    private Pagination _pagination;


    //Récupération des objets relatifs à l'envoi de mails
    @FXML
    private Button _btnEnvoi;

    @FXML
    private TextField _tfDestinataire;

    @FXML
    private TextField _tfObjet;

    @FXML
    private TextArea _tfContenu;

    private Main_Client _mainClient;

    public Controller_Client(){

    }
    private int itemsPerPage() {
        return 10;
    }


    public VBox createPage(int pageIndex) {
        VBox box = new VBox(5);
        int page = pageIndex * itemsPerPage();
        for (int i = page; i < page + itemsPerPage(); i++) {
            VBox element = new VBox();
            Hyperlink link = new Hyperlink("Item " + (i + 1));
            link.setVisited(true);
            Label text = new Label("Search results\nfor " + link.getText());
            element.getChildren().addAll(link, text);
            box.getChildren().add(element);
        }
        return box;
    }

    @FXML
    private void initialize(){

        _pagination.setPageFactory((Integer pageIndex) -> createPage(pageIndex));
    }

    private void EnvoiMail(){
        Platform.runLater(() ->{
            String destinataire;
            String objet;
            String contenu;

            if(!_tfDestinataire.getText().equals("")){
                destinataire = _tfDestinataire.getText();
                objet = _tfObjet.getText();
                contenu = _tfContenu.getText();
                //Envoi du message
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

    public void SetMain(Main_Client mainClient)
    {
        _mainClient = mainClient;
        _btnEnvoi.setOnMouseClicked(mouseEvent -> EnvoiMail());
    }
}
