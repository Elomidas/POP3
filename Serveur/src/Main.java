import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    final static int port = 110;

    public static void main(String[] args) {
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Lancement du serveur");
            while (true){
                Socket socketClient = serverSocket.accept();
                Tcp t = new Tcp(socketClient);
                t.start();
                socketClient.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}