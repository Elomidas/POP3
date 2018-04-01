package Model.Protocols;

import Model.MailBox.Mailbox;
import Model.Protocols.TCP.Tcp;

import java.io.IOException;
import java.net.Socket;

public abstract class ObjetConnecteUnderTCP extends Thread {

    protected Tcp tcp;
    protected boolean continuer;
    protected Mailbox mailbox;

    public ObjetConnecteUnderTCP(Socket socket){
        try {
            this.tcp = new Tcp(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.continuer = true;
        this.mailbox = new Mailbox();
    }

    protected abstract String transaction(String command, String[] parameters);

    protected abstract String commandeRset();

    protected abstract String commandeQuit();

}
