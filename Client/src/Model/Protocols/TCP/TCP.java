package Model.Protocols.TCP;

import Utilities.TestRegex;

import java.io.*;
import java.net.*;

public class TCP {
    //Constants
    public static final int _UNCONFIGURED = 0;
    public static final int _CONFIGURED = 1;
    public static final int _CONNECTED = 2;

    //Variables
    protected int m_port;
    protected Socket m_socket;
    protected InetAddress m_server;
    protected BufferedReader m_input;
    protected PrintStream m_output;

    /*  ###
     *  # CONSTRUCTOR
     *  ###
     */
    public TCP() {
        m_port = 0;
        m_socket = null;
        m_server = null;
        m_input = null;
        m_output = null;
    }

    /*  ###
     *  # CHECK FUNCTIONS
     *  ###
     */

    /*  Check if current port seem correct.
     *  Parameters :
     *      None.
     *  Return :
     *      boolean
     *      - true if port seem correct
     *      - false if not
     */
    protected boolean checkPort() {
        return (m_port != 0);
    }

    /*  Check if current address seem correct.
     *  Parameters :
     *      None.
     *  Return :
     *      boolean
     *      - true if address seem correct
     *      - false if not
     */
    protected boolean checkAddress() {
        return (m_server != null);
    }

    /*  Check if current configuration seem correct.
     *  Parameters :
     *      None.
     *  Return :
     *      boolean
     *      - true if configuration seem correct
     *      - false if not
     */
    protected boolean checkConfiguration() {
        return (this.checkPort() && this.checkAddress());
    }

    /*  Check if client is connected to server.
     *  Parameters :
     *      None.
     *  Return :
     *      boolean
     *      - true if client and server are connected
     *      - false if not
     */
    protected boolean checkConnection() {
        return m_socket != null;
    }

    /*  ###
     *  # ACCESSORS
     *  ###
     */

    /*  Return a string that describe the socket state.
     *  Parameters :
     *      None.
     *  Return :
     *      String describing current socket state.
     */
    protected String getSocketString() {
        String info;
        if(this.checkConnection()) {
            info = m_socket.getInetAddress().getHostAddress() + ":" + m_socket.getPort();
        } else {
            info = "Socket not connected, stored address is " + m_server + ":" + m_port;
        }
        return info;
    }

    /*  Check the current Model.Protocols.TCP client status
     *  Parameters :
     *      None.
     *  Return :
     *      Integer
     *      - Model.Protocols.TCP._UNCONFIGURED if port and/or server address isn't set.
     *      - Model.Protocols.TCP._CONFIGURED if port and address are set but client isn't connected to server.
     *      - Model.Protocols.TCP.CONNECTED if client is connected to server.
     */
    public int Status() {
        if(this.checkConnection()) {
            return TCP._CONNECTED;
        }
        if(this.checkConfiguration()) {
            return TCP._CONFIGURED;
        }
        return TCP._UNCONFIGURED;
    }

    /*  ###
     *  # MUTATORS
     *  ###
     */

    /*  Set the address for server to target.
     *  Parameters :
     *      String containing the server's address, can be an URL or an IP address.
     *  Return :
     *      Nothing.
     *  Throw :
     *      TCPException in case of error.
     */
    public void setServerAddress(String address) throws TCPException {
        if(this.checkConnection()) {
            throw new TCPException("Disconnect client before changing configuration.");
        }
        m_server = null;
        try {
            if(TestRegex.CheckIP(address)) {
                //Here, address contains an IP address
                m_server = InetAddress.getByName(address);
            } else {
                //Here, address does not contain an IP address, so we handle it as a domain name
                m_server = InetAddress.getByName(address);
            }
        } catch(Exception e) {
            throw new TCPException("Unable to set server address (" + address + ").", e);
        }
    }

    /*  Set the port targeted on the server.
     *  Parameters :
     *      Integer that represents the server's port to target.
     *  Return :
     *      Nothing.
     *  Throw :
     *      TCPException in case of error.
     */
    public void setServerPort(int port) throws TCPException {
        if(this.checkConnection()) {
            throw new TCPException("Disconnect client before changing configuration.");
        }
        m_port = port;
    }

    /*  Create input and output streams, used to transfer data through Model.Protocols.TCP connection.
     *  Store them in this.m_input and this.m_output
     *  Parameters :
     *      None.
     *  Return :
     *      Noting.
     *  Throw :
     *      TCPException if there is an error.
     */
    protected void createIO() throws TCPException {
        if(this.checkConnection()) {
            try {
                m_input = new BufferedReader(new InputStreamReader(m_socket.getInputStream()));
                m_output = new PrintStream(m_socket.getOutputStream());
            } catch(Exception e) {
                String err = "Unable to create IO streams from server " + this.getSocketString();
                throw new TCPException(err, e);
            }
        } else {
            String err = "Application isn't able to create IO streams : socket can't be null";
            throw new TCPException(err);
        }
    }

    /*  Try to connect client with the given server.
     *  Parameters :
     *      None.
     *  Return :
     *      Nothing.
     *  Throw :
     *      TCPException in case of error.
     */
    public void Connect() throws TCPException {
        if(this.checkConfiguration()) {
            try {
                m_socket = new Socket(m_server, m_port);
                this.createIO();
            } catch(Exception e) {
                throw new TCPException("Error while trying to connect to server " + this.getSocketString(), e);
            }
        } else {
            String err;
            if(this.checkAddress()) {
                err = "You didn't set a valid port (" + m_port + ").";
            } else if(this.checkPort()) {
                err = "Server isn't set (" + m_server + "). Check your configuration.";
            } else {
                err = "Both of the given port (" + m_port + ") and server address (" + m_server + ") are invalid. Why did you do that ?";
            }
            throw new TCPException(err);
        }
    }

    /*  Close connection if it's open
     *  Parameters :
     *      None.
     *  Return :
     *      Nothing.
     *  Throw :
     *      TCPException in case of error.
     */
    public void Close() throws TCPException {
        if(this.checkConnection()) {
            try {
                m_input = null;
                m_output = null;
                m_socket.close();
                m_socket = null;
            } catch(Exception e) {
                throw new TCPException("Unable to close socket.", e);
            }
        }
    }

    /*  Send a message through Model.Protocols.TCP connection
     *  Parameters :
     *      String containing the message to be send.
     *  Return :
     *      Nothing.
     *  Throw :
     *      TCPException in case of undefined output.
     */
    public void Send(String message) throws TCPException {
        if(this.checkConnection()) {
            m_output.println(message);
            m_output.flush();
        } else {
            throw new TCPException("Unable to send message with an undefined output.");
        }
    }

    /*  Wait to receive a message through Model.Protocols.TCP connection
     *  Parameters :
     *      None.
     *  Return :
     *      String containing the message received.
     *  Throw :
     *      TCPException in case of undefined output.
     */
    public String Receive() throws TCPException {
        StringBuilder messageReceived = new StringBuilder();
        char iChar;
        boolean first = true;

        int i;
        try {
            InputStream in = m_socket.getInputStream();
            BufferedInputStream bufIn = new BufferedInputStream(in);

            do {
                i = bufIn.read();
                iChar = (char) i;
                if ((i != -1) & (i != '\r')) {
                    if(i == '\n') {
                        if(first == false) {
                            messageReceived.append("\n");
                        }
                    } else {
                        messageReceived.append(iChar);
                        first = false;
                    }
                }

            } while ((i != -1) &  (i != '\r'));
        } catch(IOException e) {
            throw new TCPException("Unable to receive.", e);
        }

        String msg = messageReceived.toString();
        System.out.println("Received : " + msg);
        return msg;
    }
}
