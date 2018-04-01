package Model.Connexions;

public class ConnexionPOP3S extends Connexion{

    public ConnexionPOP3S(int port){
        super(port);
    }

    public void run(){
        action(1);
    }
}
