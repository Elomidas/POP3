package Model.Protocols.SMTP;

import Model.Protocols.ProtocolUnderTCPException;

public class SMTPException extends ProtocolUnderTCPException {

    public SMTPException() {
        super();
    }

    public SMTPException(String message) { super(message); }

    public SMTPException(String message, Throwable cause) {
        super(message, cause);
    }

    public SMTPException(Throwable cause) {
        super(cause);
    }
}