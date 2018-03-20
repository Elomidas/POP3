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
}
