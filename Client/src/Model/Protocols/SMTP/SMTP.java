package Model.Protocols.SMTP;

import Model.MailBox.Mail;
import Model.Protocols.ProtocolUnderTCP;
import Model.Protocols.ProtocolUnderTCPException;
import Model.Protocols.TCP.TCP;
import Model.Protocols.TCP.TCPException;
import Utilities.TestRegex;

public class SMTP extends ProtocolUnderTCP {

    /**
     * Constants
     */
    private static final int _INITIALIZED = 0;
    private static final int _CONNECTION = 1;
    private static final int _IDENTIFICATION = 2;
    private static final int _SENDERIDENTIFIED = 3;
    private static final int _RECEIVERIDENTIFIED = 4;
    private static final int _MAILSENDING = 5;
    private static final int _CONTENTSENT = 6;
    private static final int _DECONNECTION = 7;
    protected static final String _EOM = ".";

    /**
     * Constructor
     */
    public SMTP(){
        super("SMTP");
    }


    /**
     * Accessors
     */

    /**
     *
     * @return
     */
    @Override
    public String getProtocolName() {
        return super.getProtocolName();
    }


    /**
     * Mutators
     */

    /**
     *
     * @param protocolName
     */
    @Override
    public void setProtocolName(String protocolName){
        super.setProtocolName(protocolName);
    }


    /**
     * Main functions
     */

    @Override
    public void Connect(String address, int port) throws SMTPException {
        try {
            super.Connect(address, port);
            tcp.Receive();
        } catch (ProtocolUnderTCPException | TCPException e) {
            throw new SMTPException("Unable to connect.", e);
        }
    }

    @Override
    public boolean CheckConnected() {
        return super.CheckConnected();
    }

    /**
     * Send a mail to one or more targets
     * @param targets String containing targets, separated by ";" or "; "
     * @param mail Mail to send
     * @throws SMTPException Error while sending mail
     */
    public void SendMail(String targets, String from, String subject, String mail) throws SMTPException {
        String[] to = targets.split(";");
        MailConvertor mailConvertor = new MailConvertor();
        mailConvertor.setSubject(subject);
        mailConvertor.setFrom(from);
        mailConvertor.setMessage(mail);
        for(int i = 0; i < to.length; i++) {
            String target = to[i].trim();
            //
        }
    }

    protected boolean sendSimpleMail(MailConvertor mail) {
        try {
            this.sendFrom(mail.getFrom());
            this.sendTo(mail.getTo());
            this.sendMail(mail);
        } catch (SMTPException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void sendFrom(String from) throws SMTPException {
        String msg = "MAIL FROM:<" + from + ">";
        try {
            String response = dialog(msg);
            if(!TestRegex.Match("250.*", response));
        } catch (ProtocolUnderTCPException e) {
            throw new SMTPException("Unable to send MAIL command.", e);
        }
    }

    private void sendTo(String to) throws SMTPException {
        String msg = "RCPT TO:<" + to + ">";
        try {
            String response = dialog(msg);
            if(!TestRegex.Match("250.*", response));
        } catch (ProtocolUnderTCPException e) {
            throw new SMTPException("Unable to send RCPT command.", e);
        }
    }

    private void sendMail(MailConvertor mail) throws SMTPException {
        try {
            String response = dialog("DATA");
            if(!TestRegex.Match("354.*", response));
        } catch (ProtocolUnderTCPException e) {
            throw new SMTPException("Unable to send DATA command.", e);
        }
        String[] table = mail.getSendableTable();
        try {
            for(String msg : table) {
                tcp.Send(msg);
            }
            String response = tcp.Receive();
            if(!TestRegex.Match("250.*", response)) {
                throw new SMTPException("Unable to send message.\nServer respond : " + response);
            }
        } catch (TCPException e) {
            throw new SMTPException("Unable to send message.", e);
        }
    }

    @Override
    public void Close() throws ProtocolUnderTCPException {
        try {
            tcp.Send("QUIT");
        } catch (TCPException e) {
            e.printStackTrace();
        }
        super.Close();
    }

    public void Observe() throws SMTPException {
        try {
            this.dialog("EHLO " + System.getenv().get("USERDOMAIN"));
            MailConvertor mail = new MailConvertor();
            mail.setFrom("vremond@email.com");
            mail.setTo("mtardy@email.com");
            mail.setSubject("Test Coucou");
            mail.setMessage("Comment tu vas ?\nMoi je vais nickel.\n.\nVictor");
            this.sendSimpleMail(mail);
        } catch (ProtocolUnderTCPException e) {
            throw new SMTPException("Receive error", e);
        }
    }
}
