package Utilities;

public class DNSException extends Exception {
    public DNSException() {
        super();
    }

    public DNSException(String message) {
        super(message);
    }

    public DNSException(String message, Throwable cause) {
        super(message, cause);
    }

    public DNSException(Throwable cause) {
        super(cause);
    }
}
