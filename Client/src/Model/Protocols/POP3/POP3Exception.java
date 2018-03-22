package Model.Protocols.POP3;

import Model.Protocols.ProtocolUnderTCPException;

public class POP3Exception extends ProtocolUnderTCPException {

    public POP3Exception() {
        super();
    }

    public POP3Exception(String message) {
        super(message);
    }

    public POP3Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public POP3Exception(Throwable cause) {
        super(cause);
    }
}
