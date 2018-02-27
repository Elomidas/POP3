package POP3;

import TCP.*;

public class POP3 {
    //Constants
    public static final int _DISCONNECTED = 0;
    public static final int _CONNECTED = 0;
    public static final int _AUTHENTICATED = 0;

    //Variables
    protected TCP m_tcp;
    protected boolean m_authenticated;

    /*  ###
     *  # CONSTRUCTOR
     *  ###
     */

    public POP3() {
        m_tcp = new TCP();
        m_authenticated = false;
    }

    /*  ###
     *  # CHECK FUNCTIONS
     *  ###
     */

    /*  Check if client is connected to server through TCP
     *  Parameters :
     *      None
     *  Return :
     *      boolean
     *      - true if client is connected to server
     *      - false if not
     */
    protected boolean checkConnected() {
        return (m_tcp.Status() == TCP._CONNECTED);
    }

    /*  ###
     *  # ACCESSORS
     *  ###
     */

    /*  Check if client is connected and authenticated to server.
     *  Parameters :
     *      None.
     *  Return :
     *      POP3._DISCONNECTED if client isn't connected to server through TCP
     *      POP3._CONNECTED if client is connected to server through TCP but user isn't authenticated.
     *      POP3._AUTHENTICATED if client is connected to server through TCP and user is authenticated.
     */
    public int Status() {
        if(this.checkConnected() == false) {
            return POP3._DISCONNECTED;
        }
        if(m_authenticated) {
            return POP3._AUTHENTICATED;
        }
        return POP3._CONNECTED;
    }

    /*  ###
     *  # MUTATORS
     *  ###
     */

    /*  Try to connect client to server through TCP.
     *  Parameters :
     *      String containing the server address (can be an IP address as an URL).
     *      Integer containing the port to target on the server.
     *  Return :
     *      Nothing.
     *  Throw :
     *      POP3Exception in case of error.
     */
    public void Connect(String address, int port) throws POP3Exception {
        try {
            m_tcp.setServerAddress(address);
            m_tcp.setServerPort(port);
            m_tcp.Connect();
        } catch(TCPException e) {
            throw new POP3Exception("Unable to connect POP3.", e);
        }
    }

    /*  Try to authenticate the user on the server
     *  Parameters :
     *      login :     String containing the username
     *      password :  String containing the password for the given username
     *  Return :
     *      true :  Successfully authenticated
     *      false : Error in login and/or password
     *  Throw :
     *      POP3Exception in case of other error.
     */

    public boolean Authentication(String login, String password) throws POP3Exception {
        if(this.checkConnected() == false) {
            throw new POP3Exception("Unable to authenticate, client not connected to server.");
        }
        if(m_authenticated) {
            throw new POP3Exception("Already authenticate.");
        }
        /*  TODO
         *  #############################
         *  # POP3 authentication code  #
         *  #############################
         */
        return false;
    }

    /*  Disconnect current user
     *  Parameters :
     *      None.
     *  Return :
     *      Nothing.
     *  Throw :
     *      POP3Exception in case of error
     */
    public void Disconnect() throws POP3Exception {
        if(this.checkConnected() == false) {
            throw new POP3Exception("Unable to disconnect, client not connected to server.");
        }
        if(m_authenticated) {
            /*  TODO
             *  #############################
             *  # POP3 disconnection code   #
             *  #############################
             */
            m_authenticated = false;
        }
        try {
            m_tcp.Close();
        } catch(TCPException e) {
            throw new POP3Exception("Unable to disconnect.", e);
        }
    }

    /*  Wait TCP Server to send a response
     *  Parameters :
     *      None
     *  Return :
     *      String received through TCP connection
     *  Throw :
     *      POP3Exception in case of error
     */
    protected String Response() throws POP3Exception {
        String result;
        if(this.checkConnected() == false) {
            throw new POP3Exception("Unable to receive message, client not connected to server.");
        }
        try {
            result = m_tcp.Receive();
        } catch(TCPException e) {
            throw new POP3Exception("Unable to receive message.", e);
        }
        return result;
    }
}
