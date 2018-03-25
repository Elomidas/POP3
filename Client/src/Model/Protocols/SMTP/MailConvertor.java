package Model.Protocols.SMTP;

import java.time.Instant;
import java.util.Date;

public class MailConvertor {
    private String subject;
    private String from;
    private String to;
    private String date;
    private String[] message;

    public MailConvertor() {
        subject = "none";
        from = "";
        to = "";
        date = Date.from(Instant.now()).toString();
        message = new String[0];
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public void setMessage(String message) {
        this.message= message.split("\n");
        int i;
        for(i = 0; i < this.message.length; i++) {
            if(this.message[i].equals(".")) {
                this.message[i] = "..";
            }
        }
    }

    private String[] getHeaders() {
        String[] headers = new String[4];
        headers[0] = "From: \"" + from.split("@", 2)[0] + "\" <" + from + ">";
        headers[1] = "To: \"" + to.split("@", 2)[0] + "\" <" + to + ">";
        headers[2] = date;
        headers[3] = subject;
        return headers;
    }

    public String getSendableMessage() {
        String[] headers = this.getHeaders();
        StringBuilder sendable = new StringBuilder("");
        String eol = "\r\n";
        for(int i = 0; i < headers.length; i++) {
            sendable.append(headers[i]).append(eol);
        }
        sendable.append(eol);
        for(int i = 0; i < message.length; i++) {
            sendable.append(message[i]).append(eol);
        }
        sendable.append(".").append(eol);
        return sendable.toString();
    }
}
