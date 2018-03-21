package Commun;

import java.io.*;
import java.net.Socket;

public class Connexion extends Thread {

    protected BufferedReader m_input;
    protected PrintStream m_output;
    private Socket socket;

    public Connexion (Socket socket){
        this.socket = socket;
    }
    public void send(String message) {
        m_output.println(message + "\r");
        m_output.flush();
    }

    public String receive() throws IOException {
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
