package Model.Protocols.SMTP;

import Model.MailBox.Mail;
import Model.Protocols.ProtocolUnderTCP;
import Model.Protocols.ProtocolUnderTCPException;
import Model.Protocols.TCP.TCP;
import Model.Protocols.TCP.TCPException;
import Utilities.TestRegex;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;

public class SMTP extends ProtocolUnderTCP {

    /**
     * Constants
     */
    public static final int _UNINITIALIZED = -1;
    private static final int _INITIALIZED = 0;
    private static final int _CONNECTION = 1;
    private static final int _IDENTIFICATION = 2;
    private static final int _SENDERIDENTIFIED = 3;
    private static final int _RECEIVERIDENTIFIED = 4;
    private static final int _MAILSENDING = 5;
    private static final int _CONTENTSENT = 6;
    private int currentState;
    protected static final String _EOM = ".";

    /**
     * Constructor
     */
    public SMTP(){
        super("SMTP");
        currentState = SMTP._UNINITIALIZED;
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

    /**
     * Set the connection parameters
     * @param address
     * @param port
     * @throws SMTPException
     */
    @Override
    public void Connect(String address, int port) throws SMTPException {
        if(currentState == SMTP._UNINITIALIZED) {
            try {
                super.setParameters(address, port);
                currentState = SMTP._INITIALIZED;
            } catch (ProtocolUnderTCPException e) {
                throw (SMTPException) e;
            }
        } else {
            throw new SMTPException("Connection not allowed in state " + currentState);
        }
    }

    /**
     * Connect with previously set parameters
     * @throws SMTPException
     */
    @Override
    protected void connect() throws SMTPException {
        if(currentState == SMTP._INITIALIZED) {
            try {
                super.connect();
                currentState = SMTP._CONNECTION;
                String response = tcp.Receive();
                if(TestRegex.Match("220.*", response)) {
                    currentState = SMTP._IDENTIFICATION;
                    this.dialog("EHLO " + System.getenv().get("USERDOMAIN"));
                } else {
                    throw new SMTPException("Server respond to connection with " + response);
                }
            } catch (ProtocolUnderTCPException e) {
                throw new SMTPException("Unable to send mails with SMTP.", e);
            } catch (TCPException e) {
                throw new SMTPException("Unable to receive response from server.", e);
            }
        } else {
            throw new SMTPException("TCP connection not allowed in state " + currentState);
        }
    }

    /**
     * Send a mail to one or more targets
     * @param targets String containing targets, separated by ";" or "; "
     * @param from Mail of the sender
     * @param subject Mail's subject
     * @param mail Mail's text
     * @throws SMTPException Error while sending the mail(s)
     */
    public void SendMail(String targets, String from, String subject, String mail) throws SMTPException {
        this.connect();
        String[] to = targets.split(";");
        MailConvertor mailConvertor = new MailConvertor();
        mailConvertor.setSubject(subject);
        mailConvertor.setFrom(from);
        mailConvertor.setMessage(mail);
        StringBuilder errors = new StringBuilder("");
        for(int i = 0; i < to.length; i++) {
            String target = to[i].trim();
            mailConvertor.setTo(target);
            if(!this.sendSimpleMail(mailConvertor)) {
                errors.append("Mail to ")
                        .append(target)
                        .append(" hasn't been delivered.\n");
            }
        }
        try {
            this.Close();
        } catch (ProtocolUnderTCPException e) {
            throw (SMTPException)e;
        }
        if(!errors.toString().equals("")) {
            throw new SMTPException(errors.toString());
        }
    }

    /**
     * Send a mail to an unique address
     * @param mail Mail to be send
     * @return true if the mail has been send, false else.
     */
    protected boolean sendSimpleMail(MailConvertor mail) {
        try {
            this.sendFrom(mail.getFrom());
            this.sendTo(mail.getTo());
            this.sendMail(mail);
            currentState = SMTP._IDENTIFICATION;
        } catch (SMTPException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Send the MAIL FROM SMTP command
     * @param from address of the guy who send the mail
     * @throws SMTPException Error while sending the MAIL FROM command
     */
    private void sendFrom(String from) throws SMTPException {
        if(currentState != SMTP._IDENTIFICATION) {
            throw new SMTPException("Sending MAIL FROM not allowed in state " + currentState);
        }
        String msg = "MAIL FROM:<" + from + ">";
        try {
            String response = dialog(msg);
            if(TestRegex.Match("250.*", response)) {
                currentState = SMTP._SENDERIDENTIFIED;
            } else {
                currentState = SMTP._IDENTIFICATION;
                throw new SMTPException("Sender not recognized.");
            }
        } catch (ProtocolUnderTCPException e) {
            throw new SMTPException("Unable to send MAIL command.", e);
        }
    }

    /**
     * Send the RCPT TO SMTP command
     * @param to Address on which send the mail
     * @throws SMTPException Error while executing RCPT
     */
    private void sendTo(String to) throws SMTPException {
        if(currentState != SMTP._SENDERIDENTIFIED) {
            throw new SMTPException("Sending RCPT TO not allowed in state " + currentState);
        }
        String msg = "RCPT TO:<" + to + ">";
        try {
            String response = dialog(msg);
            if(!TestRegex.Match("250.*", response)) {
                currentState = SMTP._IDENTIFICATION;
                throw new SMTPException("Unable to execute RCPT TO command.\nServer respond with " + response);
            } else {
                currentState = SMTP._RECEIVERIDENTIFIED;
            }
        } catch (ProtocolUnderTCPException e) {
            throw new SMTPException("Unable to send RCPT command.", e);
        }
    }

    /**
     * Send the DATA SMTP command
     * @param mail Mail to be send
     * @throws SMTPException Error while sending the mail
     */
    private void sendMail(MailConvertor mail) throws SMTPException {
        if(currentState != SMTP._RECEIVERIDENTIFIED) {
            throw new SMTPException("Sending DATA not allowed in state " + currentState);
        }
        currentState = SMTP._MAILSENDING;
        try {
            String response = dialog("DATA");
            if(!TestRegex.Match("354.*", response)) {
                currentState = SMTP._IDENTIFICATION;
                throw new SMTPException("Unable to execute DATA command.\nServer respond with " + response);
            }
        } catch (ProtocolUnderTCPException e) {
            currentState = SMTP._IDENTIFICATION;
            throw new SMTPException("Unable to send DATA command.", e);
        }
        String[] table = mail.getSendableTable();
        try {
            for(String msg : table) {
                tcp.Send(msg);
            }
            String response = tcp.Receive();
            if(!TestRegex.Match("250.*", response)) {
                currentState = SMTP._IDENTIFICATION;
                throw new SMTPException("Unable to send message.\nServer respond : " + response);
            }
        } catch (TCPException e) {
            currentState = SMTP._IDENTIFICATION;
            throw new SMTPException("Unable to send message.", e);
        }
        currentState = SMTP._CONTENTSENT;
    }

    @Override
    public void Close() throws SMTPException {
        if((currentState != SMTP._IDENTIFICATION)
                && (currentState != SMTP._MAILSENDING)) {
            throw new SMTPException("Sending QUIT not allowed in state " + currentState);
        }
        try {
            tcp.Send("QUIT");
            currentState = SMTP._INITIALIZED;
            super.Close();
        } catch (TCPException e) {
            throw new SMTPException("Unable to use command QUIT.", e);
        } catch (ProtocolUnderTCPException e) {
            throw (SMTPException)e;
        }
    }

    /**
     * Function used to test
     * @throws SMTPException
     */
    public void Observe() throws SMTPException {
        try {
            this.connect();
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