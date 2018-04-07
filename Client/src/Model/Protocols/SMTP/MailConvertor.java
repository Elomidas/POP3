package Model.Protocols.SMTP;

import java.time.Instant;
import java.util.Date;

public class MailConvertor {
    private String subject;
    private String from;
    private String to;
    private String date;
    private String[] message;
    private String eol = "\r\n";

    MailConvertor() {
        subject = "none";
        from = "";
        to = "";
        date = Date.from(Instant.now()).toString();
        message = new String[0];
    }

    /**
     * Set the mail's subject
     * @param subject Mail's subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Set the address of the guy who send the mail
     * @param from Address of the guy who send the mail
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * Set the address to which send the mail
     * @param to address to which send the mail
     */
    public void setTo(String to) {
        this.to = to;
    }

    /**
     * Get the address of the man who send the mail
     * @return Address of the man who send the mail
     */
    public String getFrom() {
        return from;
    }

    /**
     * Get address to which send the mail
     * @return address to which send the mail
     */
    public String getTo() {
        return to;
    }

    /**
     * Set mail's body
     * @param message mail's body
     */
    public void setMessage(String message) {
        this.message= message.split("\n");
        int i;
        for(i = 0; i < this.message.length; i++) {
            if(this.message[i].equals(".")) {
                this.message[i] = "..";
            }
        }
    }

    /**
     * Give the mail's header as a String array
     * @return String array containing the headers
     */
    private String[] getHeaders() {
        String[] headers = new String[4];
        headers[0] = "From: \"" + from.split("@", 2)[0] + "\" <" + from + ">";
        headers[1] = "To: \"" + to.split("@", 2)[0] + "\" <" + to + ">";
        headers[2] = date;
        headers[3] = subject;
        return headers;
    }

    /**
     * Create a mail with its headers
     * @return a string containing a mail's headers and a mail's body.
     */
    private String getSendableMail() {
        String[] headers = this.getHeaders();
        StringBuilder sendable = new StringBuilder("");
        for (String header : headers) {
            sendable.append(header).append(eol);
        }
        sendable.append(eol);
        for (String aMessage : message) {
            sendable.append(aMessage).append(eol);
        }
        sendable.append(".").append(eol);
        return sendable.toString();
    }

    /**
     * Get message under as a string array.
     * @return Array of string containing mail's headers and mail's body
     */
    public String[] getSendableTable() {
        return getSendableMail().split(eol);
    }
}
