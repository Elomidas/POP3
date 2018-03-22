package Model.Protocols.SMTP;

import Model.Protocols.ProtocolUnderTCP;

public class SMTPSendingThread extends ProtocolUnderTCP implements Runnable {
    private String to;
    private String message;

    public SMTPSendingThread(String to, String message) {
        super("SMTP - Thread");
        this.to = to;
        this.message = message;
    }

    @Override
    public void run() {
        System.out.println(to);
        System.out.println(message);

    }
}
