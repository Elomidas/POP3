package POP3;

import TCP.*;

public class POP3 {
    //Variables
    protected TCP m_tcp;
    protected boolean m_authenticate;

    public POP3() {
        m_tcp = null;
        m_authenticate = false;
    }

    public void Connect(String address, int port) throws POP3Exception {
        try {
            m_tcp = new TCP();
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
     *  Throw
     *      POP3Exception in case of other error.
     */

    public boolean Authentication(String login, String password) throws POP3Exception {
        if(m_authenticate) {
            throw new POP3Exception("Already authenticate.");
        }
        if(m_tcp == null) {
            throw new POP3Exception("Unable to authenticate, client not connected to server.");
        }
        //TO-DO
        return false;
    }

    /*  Wait TCP Server to send a response
     *
     */
    protected String Response() throws POP3Exception {
        String result = "";
        if(m_tcp == null) {
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
