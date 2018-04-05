package Model.Protocols.TCP;

public class TCPException extends Exception {
	
	public TCPException() {
		super();
	}
	
	public TCPException(String message) {
		super(message);
	}
	
	public TCPException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public TCPException(Throwable cause) {
		super(cause);
	}
}
