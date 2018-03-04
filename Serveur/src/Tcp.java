import java.io.*;
import java.net.Socket;

public class Tcp extends Thread{

    private Socket socket;
    protected BufferedReader m_input;
    protected PrintStream m_output;

    public Tcp(Socket socket){
        this.socket = socket;
    }

    public void run(){
        try {
            System.out.println("Début de connexion");

            m_input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            m_output = new PrintStream(socket.getOutputStream());

            ObjetConnecte object = new ObjetConnecte(this);
            object.Launch();

            if(socket.isClosed()) {
                System.out.println("Fin de Connexion");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void Send(String message) {
        m_output.println(message);
        m_output.flush();
    }

    public String Receive() {
        String result = "";
        try{
            result = m_input.readLine();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
