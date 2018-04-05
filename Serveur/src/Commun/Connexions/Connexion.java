package Commun.Connexions;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public abstract class Connexion extends Thread{

    private ServerSocket serverSocket;
    private int port;
    private boolean continuer;
    String domain;
    private String protocol;

    Connexion(String domain, int port, String protocolName){
        this.port = port;
        this.domain = domain;
        serverSocket = null;
        continuer = true;
        protocol = protocolName;
    }

    public void run(){
        try {
            while(!available(port)) {
                port += 3;
            }
            this.serverSocket = new ServerSocket(port);
            while (continuer) {
                System.out.println("Attente de connexion " + protocol + " au port " + port);
                launchObject(serverSocket.accept());
                System.out.println("Début de connexion " + protocol);
            }
        } catch (SocketTimeoutException e) {
            continuer = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract void launchObject(Socket socket) throws IOException;


    private static boolean available(int port) {
        try (ServerSocket ss = new ServerSocket(port); DatagramSocket ds = new DatagramSocket(port)) {
            ss.setReuseAddress(true);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException ignored) {
        }
        return false;
    }
}
