import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Tcp extends Thread{

    private Socket socket;
    private String messageSend = "bienvenue";
    private StringBuilder messageReceived;

    public Tcp(Socket socket){
        this.socket = socket;
    }

    public void run(){
        try {
            do { 
                messageReceived = receiveMessage();
                sendMessage(messageSend);
            }while (!socket.isClosed());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private StringBuilder receiveMessage() throws IOException {
        StringBuilder messageReceived = new StringBuilder();
        char iChar;

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

        return messageReceived;
    }

    private void sendMessage(String messageSend) throws IOException {
        OutputStream out;
        out = socket.getOutputStream();
        BufferedOutputStream bufOut = new BufferedOutputStream(out);
        byte b[] = messageSend.getBytes();

        bufOut.write(b);

    }

    private String readBuffer(BufferedInputStream buf) throws IOException{
      String response = "";
      int stream;
      byte[] b = new byte[4096];
      stream = buf.read(b);
      response = new String(b, 0, stream);
      return response;
   }

}
