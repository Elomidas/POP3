package Model.Protocols.POP3;

import Model.Protocols.TCP.*;
import Utilities.TestRegex;

public class POP3 {
    //Constants
    public static final int _DISCONNECTED = 0;
    public static final int _CONNECTED = 1;
    public static final int _AUTHENTICATED = 2;
    protected static final String _EOM = ".";

    //Variables
    protected TCP m_tcp;
    protected boolean m_authenticated;
    protected String m_error;

    /*  ###
     *  # CONSTRUCTOR
     *  ###
     */

    public POP3() {
        m_tcp = new TCP();
        m_authenticated = false;
        m_error = "";
    }

    /*  ###
     *  # CHECK FUNCTIONS
     *  ###
     */

    /*  Check if client is connected to server through Model.Protocols.TCP
     *  Parameters :
     *      None
     *  Return :
     *      boolean
     *      - true if client is connected to server
     *      - false if not
     */
    public boolean CheckConnected() {
        return (m_tcp.Status() == TCP._CONNECTED);
    }

    /* Get a String describing the last error encountered.
     */
    public String getError() {
        return m_error;
    }

    /*  ###
     *  # ACCESSORS
     *  ###
     */

    /*  Check if client is connected and authenticated to server.
     *  Parameters :
     *      None.
     *  Return :
     *      Model.Protocols.POP3._DISCONNECTED if client isn't connected to server through Model.Protocols.TCP
     *      Model.Protocols.POP3._CONNECTED if client is connected to server through Model.Protocols.TCP but user isn't authenticated.
     *      Model.Protocols.POP3._AUTHENTICATED if client is connected to server through Model.Protocols.TCP and user is authenticated.
     */
    public int Status() {
        if(this.CheckConnected() == false) {
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

    /*  Send a message and wait the response
     *  Parameters :
     *      msg :   String containing the message to be sent
     *  Return :
     *      String containing the server's response
     *  Throw :
     *      POP3Exception in case of error.
     */
    protected String dialog(String msg) throws POP3Exception {
        this.Message(msg);
        return this.Response();
    }

    /*  Try to connect client to server through Model.Protocols.TCP.
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
        System.out.println(login+":"+password);
        if(this.CheckConnected() == false) {
            throw new POP3Exception("Unable to authenticate, client not connected to server.");
        }
        if(m_authenticated) {
            throw new POP3Exception("Already authenticate.");
        }
        if(checkUser(login)) {
            return checkPassword(password);
        }
        return false;
    }

    /*  Function that checks an username validity.
     *  Parameters :
     *      user :  String containing the username to check
     *  Return :
     *      boolean corresponding to the validity of the given username.
     *  Throw :
     *      POP3Exception in case of error.
     */
    protected boolean checkUser(String user) throws POP3Exception {
        String response;
        String cmd = "USER " + user;
        try {
            response = this.dialog(cmd);
        } catch(POP3Exception e) {
            throw new POP3Exception("Unable to check username validity.", e);
        }
        if(TestRegex.CheckPOP(response)) {
            return true;
        } else {
            m_error = "Server respond to \"" + cmd + "\" with :\n  " + response;
            System.out.println(m_error);
        }
        return false;
    }

    /*  Function that checks a password validity with login previously sent.
     *  Parameters :
     *      password :  String containing the password to check
     *  Return :
     *      boolean corresponding to the validity of the given login-password and the server's ability
     *        to open the repository.
     *  Throw :
     *      POP3Exception in case of error.
     */
    protected boolean checkPassword(String password) throws POP3Exception {
        String cmd = "PASS " + password;
        String response;
        try {
            response = this.dialog(cmd);
        } catch(POP3Exception e) {
            throw new POP3Exception("Unable to check password validity.", e);
        }
        if(TestRegex.CheckPOP(response)) {
            return true;
        } else {
            m_error = "Server respond to \"" + cmd + "\" with :\n  " + response;
            System.out.println(m_error);
        }
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
        if(this.CheckConnected() == false) {
            throw new POP3Exception("Unable to disconnect, client not connected to server.");
        }
        if(m_authenticated) {
            this.Message("QUIT");
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
        if(this.CheckConnected() == false) {
            throw new POP3Exception("Unable to receive message, client not connected to server.");
        }
        try {
            result = m_tcp.Receive();
        } catch(TCPException e) {
            throw new POP3Exception("Unable to receive message.", e);
        }
        return result;
    }

    /*  Send TCP Server a message
     *  Parameters :
     *      String containing message to send
     *  Return :
     *      Nothing
     *  Throw :
     *      POP3Exception in case of error
     */
    protected void Message(String message) throws POP3Exception {
        String result;
        if(this.CheckConnected() == false) {
            throw new POP3Exception("Unable to send message, client not connected to server.");
        }
        try {
            m_tcp.Send(message);
        } catch(TCPException e) {
            throw new POP3Exception("Unable to send message.", e);
        }
    }

    /*  Retrieve a mail from its UUID
     *  Parameters :
     *      id :    String containing mail's UUID
     *  Return :
     *      String representing the mail
     *  Throw :
     *      POP3Exception in case of error
     */
    public String getMail(String id) throws POP3Exception {
        String cmd = "RETR " + id;
        String response;
        try {
            response = this.dialog(cmd);
        } catch(POP3Exception e) {
            throw new POP3Exception("Unable to retrieve mail " + id + ".", e);
        }
        if(TestRegex.CheckPOP(response) == false) {
            m_error = response;
            throw new POP3Exception("Unable to retrieve mail" + id + ".\nServer response :\n" + response);
        }
        return this.retrieveMailString(response);
    }

    /*  Retrieve the message and the headers.
     *  Must be called just after reading "+OK ..." from input stream.
     *  Parameters :
     *      None
     *  Return :
     *      String representing the message
     *  Throw :
     *      POP3Exception in case of error.
     */
    protected String retrieveMailString(String response) throws POP3Exception {
        StringBuilder sBuilder = new StringBuilder();
        String[] lines = response.split("\\n");

        for(int i = 1; i < lines.length; i++) {
            sBuilder.append(lines[i])
                    .append("\\n");
        }
        return sBuilder.toString();
    }

    /*  Retrieve the current UUID list
     *  Parameters :
     *      None.
     *  Return :
     *      ArrayList containing all the messages' UUID
     *  Throw :
     *      POP3Exception in case of error.
     */
    public String[] getUUIDList() throws POP3Exception {
        String cmd = "UIDL";
        String response;
        try {
            response = this.dialog(cmd);
        } catch(POP3Exception e) {
            throw new POP3Exception("Unable to retrieve UUID list.", e);
        }
        if(TestRegex.CheckPOP(response) == false) {
            throw new POP3Exception("Unable to retrieve UUID list.\nServer response :\n" + response);
        }
        int size = Integer.parseInt(response.split(" ")[1]);
        String[] array = new String[size];
        System.out.println("resp : " + response);
        String part2 = response.split(":")[1];
        System.out.println("part2 : " + part2);
        String[] parts = part2.split("\n", 15);
        for(int index = 0; index < parts.length; index++) {
            System.out.println("part " + index + " : " + parts[index]);
            if(parts[index].equals("") == false) {
                String[] res = parts[index].split(" ");
                array[index-1] = res[1];
            }
        }
        return array;
    }

    /*  Delete a mail
     *  Parameters :
     *      UUID :  String containing the UUID of the message
     *  Return :
     *      Nothing.
     *  Throw :
     *      POP3Exception inc ase of error
     */
    public void Delete(String UUID) throws POP3Exception {
        String cmd = "DELE " + UUID;
        String response = this.dialog(cmd);
        if(TestRegex.CheckPOP(response) == false) {
            m_error = response;
            throw new POP3Exception("Unable to delete message " + UUID + ".\nServer response :\n  " + response);
        }
    }

    /*  Reset repository
     *  Parameters :
     *      None.
     *  Return :
     *      Nothing.
     *  Throw :
     *      POP3Exception in case of error.
     */
    public void Reset() throws POP3Exception {
        String cmd = "RSET";
        String response = this.dialog(cmd);
        if(TestRegex.CheckPOP(response) == false) {
            m_error = response;
            throw new POP3Exception("Unable to reset.\nServer response :\n  " + response);
        }
    }

    /*  Get the number of mails on the server
     *  Parameters :
     *      None.
     *  Return :
     *      Int representing the number of mails on the server.
     *  Throw :
     *      POP3Exception in case of error
     */
    public int getMailNumber() throws POP3Exception {
        String cmd = "STAT";
        String response = this.dialog(cmd);
        if(TestRegex.CheckPOP(response) == false) {
            m_error = response;
            throw new POP3Exception("Unable to stat.\nServer response :\n  " + response);
        }
        int nbr = Integer.parseInt(response.split(" ")[1]);
        System.out.println("STAT : " + nbr);
        return nbr;
    }
}
