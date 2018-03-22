package Model.Protocols.SMTP;

import Model.Protocols.ProtocolUnderTCP;
import Model.Protocols.ProtocolUnderTCPException;
import Model.Protocols.TCP.TCP;
import Utilities.TestRegex;

public class SMTP extends ProtocolUnderTCP{

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
        } catch (ProtocolUnderTCPException e) {
            throw new SMTPException(e.getMessage(), e);
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
    public void SendMail(String targets, String mail) throws SMTPException {
        String[] to = targets.split(";");
        for(int i = 0; i < to.length; i++) {
            to[i] = to[i].trim();
        }
        // TODO
    }
}
