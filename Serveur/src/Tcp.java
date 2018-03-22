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

            ObjetConnecteSecurise object = new ObjetConnecteSecurise(this);
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
        m_output.println(message + "\r");
        m_output.flush();
    }

    public String Receive() throws IOException {
        StringBuilder messageReceived = new StringBuilder();
        try {
            char iChar;

            int i;
            InputStream in = socket.getInputStream();
            BufferedInputStream bufIn = new BufferedInputStream(in);
            int count = 0;
            boolean first = true;

            do {
                i = bufIn.read();
                iChar = (char) i;
                if ((i != -1) & (i != '\n') & (i != '\r')) {
                    messageReceived.append(iChar);
                    first = false;
                } else {
                    if(first) {
                        count++;
                        if(count >= 5) {
                            first = false;
                        }
                    }
                }
            } while (((i != -1) & (iChar != '\n') & (i != '\r')) || first);
            System.out.println("Requête reçue: " + messageReceived);
        } catch(Exception e) {
            return "";
        }

        return messageReceived.toString();
    }

}
