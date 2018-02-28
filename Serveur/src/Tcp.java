import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Tcp extends Thread{

    private Socket socket;
    private String messageSend;
    private String messageReceived;

    public Tcp(Socket socket){
        this.socket = socket;
    }

    public void run(){
        try {
            messageReceived = receiveMessage();
            sendMessage(messageSend);
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String receiveMessage() throws IOException {
        String messageReceived ="";

        InputStream in = socket.getInputStream();
        BufferedInputStream bufIn = new BufferedInputStream(in);
        messageReceived = readBuffer(bufIn);
        System.out.println("Requête reçue: "+messageReceived);

        return messageReceived;
    }

    private void sendMessage(String messageSend) throws IOException {
        OutputStream out;
        out = socket.getOutputStream();
        BufferedOutputStream bufOut = new BufferedOutputStream(out);
        byte b[] = messageSend.getBytes();

        bufOut.write(b);
        bufOut.close();

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
