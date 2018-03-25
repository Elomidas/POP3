package Test;

import Model.Protocols.SMTP.SMTP;
import Model.Protocols.SMTP.SMTPException;

import java.time.Instant;
import java.util.Date;

public class MainSMTP {
    protected static int port = 1212;

    public static void main(String[] args) {
        System.out.println("Test 1");
        SMTP smtp = new SMTP();
        System.out.println("Test 2");
        try {
            System.out.println("Test 3");
            smtp.Connect("127.0.0.1", port);
            System.out.println("Test 4");
            smtp.Observe();
            System.out.println("Test 5");
            smtp.Close();
        } catch (Model.Protocols.ProtocolUnderTCPException e) {
            e.printStackTrace();
        }
    }
}
