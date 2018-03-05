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

            socket.close();

            if(socket.isClosed()) {
                System.out.println("Fin de Connexion");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Send(String message) {
        m_output.println(message.replace("\n", "\r\n") + "\r");
        m_output.flush();
    }

    public String Receive() throws IOException {
        StringBuilder messageReceived = new StringBuilder();
        char iChar;
        //
        int i;
        InputStream in = socket.getInputStream();
        BufferedInputStream bufIn = new BufferedInputStream(in);

        do {
            i = bufIn.read();
            iChar = (char) i;
            if((i != -1) & (i != '\n') & (i != '\r'))
                messageReceived.append(iChar);

        } while((i != -1) & (iChar != '\n') & (i != '\r'));
        System.out.println("Requête reçue: "+messageReceived);

        return messageReceived.toString();
    }

}
