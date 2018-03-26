package Test;

import Model.MailBox.MailException;
import Model.MailBox.Mailbox;
import Model.Protocols.SMTP.SMTP;
import Model.Protocols.SMTP.SMTPException;

import java.time.Instant;
import java.util.Date;

public class MainSMTP {
    protected static int port = 1212;

    public static void main(String[] args) {
        Mailbox m = new Mailbox();
        try {
            m.joinServer("192.168.1.4", 1211, 1212);
        } catch (MailException e) {
            e.printStackTrace();
        }

        System.out.println("Test 1");
        SMTP smtp = new SMTP();
        System.out.println("Test 2");
        try {
            System.out.println("Test 3");
            smtp.Connect("192.168.1.4", port);
            System.out.println("Test 4");
            smtp.Observe();
            System.out.println("Test 5");
            smtp.Close();
        } catch (Model.Protocols.ProtocolUnderTCPException e) {
            e.printStackTrace();
        }
    }
}
