package Model.Connexions;

public class ConnexionSMTP extends Connexion{

    public ConnexionSMTP(int port){
        super(port);
    }

    public void run(){
        action(2);
    }
}
