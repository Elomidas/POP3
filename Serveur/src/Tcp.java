import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Tcp extends Thread{

    private Socket socket;

    public Tcp(Socket socket){
        this.socket = socket;
    }

    public void run(){
        traitements();
        //fermer le socket à la fin du run
    }

    public void traitements(){
        String message ="";
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            message = in.readLine();
            PrintStream out = new PrintStream(socket.getOutputStream());
            out.println(message);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
/*   private String read() throws IOException{
      String response = "";
      int stream;<question></question>
      byte[] b = new byte[4096];
      stream = reader.read(b);
      response = new String(b, 0, stream);
      return response;
   } */

}
