package Model.Protocols;

import Model.Protocols.TCP.*;
import Utilities.DNS;
import Utilities.DNSException;

public abstract class ProtocolUnderTCP {

    protected String protocolName;
    protected TCP tcp;

    /**
     * Create a new ProtocolUnderTCP
     * @param protocolName explicit
     */
    public ProtocolUnderTCP(String protocolName){
        this.protocolName = protocolName;
        tcp = new TCP();
    }

    /**
     * Get protocol's name
     * @return Protocol's name
     */
    public String getProtocolName() {
        return protocolName;
    }

    /**
     * Set protocol's name
     * @param protocolName protocol's name
     */
    public void setProtocolName(String protocolName) {
        this.protocolName = protocolName;
    }

    /**
     * Compute server's address thanks to DNS class
     * @param domain Server's domain name
     * @return Server's IP address
     * @throws ProtocolUnderTCPException No server with this domain name
     */
    private String computeAddress(String domain) throws ProtocolUnderTCPException {
        String address;
        try {
            address = DNS.getAddress(domain);
        } catch (DNSException e) {
            throw new ProtocolUnderTCPException("Unable to determine address.", e);
        }
        return address;
    }

    /**
     * Determine the port to use
     * @param domain Server's domain name
     * @return port to use
     * @throws ProtocolUnderTCPException Error while determinig the port
     */
    protected int computePort(String domain) throws ProtocolUnderTCPException {
        throw new ProtocolUnderTCPException("Something is wrong here...");
    }

    /**
     * Set protocol's parameters
     * @param domain Domain name of the server to reach
     * @throws ProtocolUnderTCPException Error while computing parameters
     */
    protected void setParameters(String domain) throws ProtocolUnderTCPException {
        try {
            tcp.setServerAddress(computeAddress(domain));
            tcp.setServerPort(computePort(domain));
        } catch (TCPException e) {
            throw new ProtocolUnderTCPException("Unable to set TCP parameters.", e);
        }

    }

    /**
     * Join the server through TCP
     * @param domain Domain name of the server to reach
     * @throws ProtocolUnderTCPException
     */
    public void Connect(String domain) throws ProtocolUnderTCPException {
        this.setParameters(domain);
        this.connect();
    }

    protected void connect() throws ProtocolUnderTCPException {
        try {
            tcp.Connect();
        } catch (TCPException e) {
            throw new ProtocolUnderTCPException("Unable to join server through TCP.", e);
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
     * Send a message and wait the response
     * @param msg Message to send
     * @return Respnse received
     * @throws ProtocolUnderTCPException Error while sending message or receiving response.
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

    public void Close() throws ProtocolUnderTCPException {
        try {
            tcp.Close();
        } catch (TCPException e) {
            throw new ProtocolUnderTCPException("Unable to close " + protocolName + ".", e);
        }
    }
}