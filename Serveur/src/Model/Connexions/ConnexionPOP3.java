package Model.Connexions;

public class ConnexionPOP3 extends Connexion{

    public ConnexionPOP3(int port){
        super(port);
    }

    public void run(){
        action(0);
    }
}
