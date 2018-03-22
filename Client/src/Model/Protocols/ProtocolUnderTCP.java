package Model.Protocols;

import Model.Protocols.TCP.*;

public abstract class ProtocolUnderTCP {

    private String protocolName;
    private TCP tcp;

    public ProtocolUnderTCP(){
        this.protocolName = new String("");
        this.tcp = new TCP();
    }

    public ProtocolUnderTCP(String protocolName){
        this();
        this.protocolName = protocolName;
    }

    /*  ###
     *  # ACCESSORS
     *  ###
     */

    public String getProtocolName() {
        return protocolName;
    }

    public TCP getTcp() {
        return tcp;
    }


    /*  ###
     *  # MUTATORS
     *  ###
     */

    public void setProtocolName(String protocolName) {
        this.protocolName = protocolName;
    }

    public void setTcp(TCP tcp) {
        this.tcp = tcp;
    }


    /*  ###
     *  # MAIN FUNCTIONS
     *  ###
     */

    /**
     *
     * @param address
     * @param port
     * @throws ProtocolUnderTCPException
     */
    public void Connect(String address, int port) throws ProtocolUnderTCPException {
        try {
            tcp.setServerAddress(address);
            tcp.setServerPort(port);
            tcp.Connect();
        } catch(TCPException e) {
            throw new ProtocolUnderTCPException("Unable to connect " + protocolName + ".", e);
        }
    }

    /**
     * Check if client is connected to TCP server
     * @return true if client is connected, false else
     */
    public boolean CheckConnected() {
        return (tcp.Status() == TCP._CONNECTED);
    }

    /**
     *
     * @param msg
     * @return
     * @throws ProtocolUnderTCPException
     */
    protected String dialog(String msg) throws ProtocolUnderTCPException {
        this.Message(msg);
        return this.Response();
    }

    private String Response() throws ProtocolUnderTCPException {
        String result;
        if(!this.CheckConnected()) {
            throw new ProtocolUnderTCPException("Unable to receive message, client not connected to server.");
        }
        try {
            result = tcp.Receive();
        } catch(TCPException e) {
            throw new ProtocolUnderTCPException("Unable to receive message.", e);
        }
        return result;
    }

    private void Message(String message) throws ProtocolUnderTCPException {
        if(!this.CheckConnected()) {
            throw new ProtocolUnderTCPException("Unable to send message, client not connected to server.");
        }
        try {
            tcp.Send(message);
        } catch(TCPException e) {
            throw new ProtocolUnderTCPException("Unable to send message.", e);
        }
    }
}
