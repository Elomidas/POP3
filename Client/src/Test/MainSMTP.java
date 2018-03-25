package Test;

import Model.Protocols.SMTP.SMTP;
import Model.Protocols.SMTP.SMTPException;

import java.time.Instant;
import java.util.Date;

public class MainSMTP {
    protected static int port = 1212;

    public static void main(String[] args) {
        SMTP smtp = new SMTP();
        try {
            smtp.Connect("127.0.0.1", port);
            smtp.Observe();
            smtp.Close();
        } catch (Model.Protocols.ProtocolUnderTCPException e) {
            e.printStackTrace();
        }
    }
}
