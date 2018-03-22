package Model.Protocols;

public class ProtocolUnderTCPException extends Exception {

    public ProtocolUnderTCPException() {
        super();
    }

    public ProtocolUnderTCPException(String message) { super(message); }

    public ProtocolUnderTCPException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProtocolUnderTCPException(Throwable cause) {
        super(cause);
    }
}
