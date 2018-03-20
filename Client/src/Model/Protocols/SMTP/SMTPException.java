package Model.Protocols.SMTP;

public class SMTPException extends Exception {

    public SMTPException() {
        super();
    }

    public SMTPException(String message) {
        super(message);
    }

    public SMTPException(String message, Throwable cause) {
        super(message, cause);
    }

    public SMTPException(Throwable cause) {
        super(cause);
    }
}
