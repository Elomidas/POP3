package Model.Protocols.TCP;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by tardy on 31/03/2018.
 */
public class  Tcp {

    BufferedInputStream m_input;
    PrintStream m_output;
    Socket socket;

    public Tcp(Socket socket) throws IOException {
        this.socket = socket;
        this.m_input = null;
        this.m_output = null;
        this.createIO();
    }
    public void send(String message) {
        m_output.println(message + "\r");
        m_output.flush();
    }

    protected void createIO() throws IOException {
        m_input = new BufferedInputStream(socket.getInputStream());
        m_output = new PrintStream(socket.getOutputStream());
    }

    public String receive() throws IOException {
        StringBuilder messageReceived = new StringBuilder();
        try {
            char iChar;

            int i;
            int count = 0;
            boolean first = true;

            do {
                i = m_input.read();
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
            System.out.println("C: \"" + messageReceived + "\"");
        } catch(Exception e) {
            return e.getMessage();
        }

        return messageReceived.toString();
    }

    public void Destroy(){
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
