import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Tcp extends Thread{

    private Socket socket;

    public Tcp(Socket socket){
        this.socket = socket;
    }

    public void run(){
        traitements();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //fermer le socket à la fin du run
    }

    public void traitements(){
        String messageReceive ="";
        try{
            InputStream in = socket.getInputStream();
            BufferedInputStream bufIn = new BufferedInputStream(in);
            messageReceive = readBuffer(bufIn);
            System.out.println(messageReceive);

            OutputStream out = socket.getOutputStream();
            BufferedOutputStream bufOut = new BufferedOutputStream(out);
            out.write(messageReceive.getBytes());
            out.flush();

        }catch(Exception e){
            e.printStackTrace();
        }
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
