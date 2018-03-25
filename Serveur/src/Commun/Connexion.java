package Commun;

import java.io.*;
import java.net.Socket;

public class Connexion extends Thread {

    protected BufferedReader m_input;
    protected PrintStream m_output;
    protected Socket socket;
    protected int m_port;

    public Connexion (int port){
        this.m_port = port;
        this.socket = null;
        this.m_input = null;
        this.m_output = null;
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
