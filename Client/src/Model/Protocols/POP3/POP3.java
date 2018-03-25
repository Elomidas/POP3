package Model.Protocols.POP3;

import Model.Protocols.ProtocolUnderTCP;
import Model.Protocols.ProtocolUnderTCPException;
import Model.Protocols.TCP.*;
import Utilities.TestRegex;

public class POP3 extends ProtocolUnderTCP{
    //Constants
    public static final int _DISCONNECTED = 0;
    public static final int _CONNECTED = 1;
    public static final int _AUTHENTICATED = 2;

    //Variables
    protected boolean m_authenticated;
    protected String m_error;

    /*  ###
     *  # CONSTRUCTOR
     *  ###
     */

    public POP3() {
        super("POP3");
        m_authenticated = false;
        m_error = "";
    }

    /*  ###
     *  # CHECK FUNCTIONS
     *  ###
     */

    /**
     * Check if client is connected to TCP server
     * @return true if client is connected, false else
     */
    /*
    public boolean CheckConnected() {
        return (m_tcp.Status() == TCP._CONNECTED);
    }*/

    /*  ###
     *  # ACCESSORS
     *  ###
     */

    /**
     * Check if client is connected and authenticated to server
     * @return POP3._DISCONNECT if client isn't connected, POP3._CONNECTED if client is connected but not authenticated or POP3._AUTHENTICATED if client is connected and authenticated.
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

    /**
     * Send a message through TCP connection an wait for a response
     * @param msg String containing the message to send
     * @return String containing the response
     * @throws POP3Exception Error while sending or receiving messages
     */
    /*
    protected String dialog(String msg) throws POP3Exception {
        this.Message(msg);
        return this.Response();
    }*/

    /**
     * Check a POP3 response validity
     * @param response String containing the POP3 response
     * @return true if response is correct, false if it carries an error
     */
    protected boolean checkResponse(String response) {
        if(!TestRegex.CheckPOP(response)) {
            m_error = "Server respond to with :\n  " + response;
            System.out.println(m_error);
            return false;
        }
        return true;
    }

    /**
     * Send a message and wait for response. Check if response is positive
     * @param message Message to ben send
     * @return true if response doesn't carry any error, false else
     * @throws POP3Exception Error while sending or receiving messages
     */
    protected boolean checkedDialog(String message) throws ProtocolUnderTCPException {
        String response = dialog(message);
        return this.checkResponse(response);
    }

    /**
     * Send a message and wait for response. Return response only if it is positive
     * @param message Message to send
     * @return Response from client if there isn't any error
     * @throws POP3Exception Error while sending or receiving messages
     */
    protected String getDialogResponseIfValid(String message) throws ProtocolUnderTCPException {
        String response = dialog(message);
        if(this.checkResponse(response)) {
            return response;
        }
        return "";
    }

    /**
     * Try to connect client to server through TCP
     * @param address   Server's address (IP or URL)
     * @param port      Server's port
     * @throws POP3Exception Error while trying to connect
     */
    /*
    public void Connect(String address, int port) throws POP3Exception {
        try {
            m_tcp.setServerAddress(address);
            m_tcp.setServerPort(port);
            m_tcp.Connect();
        } catch(TCPException e) {
            throw new POP3Exception("Unable to connect POP3.", e);
        }
    }*/

    /**
     * Try to authenticate user on server
     * @param login     User's login
     * @param password  User's password
     * @return true if user has been connected, false else
     * @throws POP3Exception Error while authenticating
     */
    public boolean Authentication(String login, String password) throws POP3Exception {
        System.out.println(login+":"+password);
        if(!this.CheckConnected()) {
            throw new POP3Exception("Unable to authenticate, client not connected to server.");
        }
        if(m_authenticated) {
            throw new POP3Exception("Already authenticate.");
        }
        if(checkUser(login)) {
            if(checkPassword(password)) {
                m_authenticated = true;
                return true;
            }
        }
        return false;
    }

    /**
     * Check username validity
     * @param user Username to be checked
     * @return true if username is valid, false else
     * @throws POP3Exception Error while checking username
     */
    private boolean checkUser(String user) throws POP3Exception {
        String response;
        String cmd = "USER " + user;
        try {
            response = dialog(cmd);
        } catch(ProtocolUnderTCPException e) {
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

    /**
     * Check password validity
     * @param password Password to be checked
     * @return true if password is valid, false else
     * @throws POP3Exception Error while checking username
     */
    private boolean checkPassword(String password) throws POP3Exception {
        String cmd = "PASS " + password;
        String response;
        try {
            response = this.dialog(cmd);
        } catch(ProtocolUnderTCPException e) {
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

    /**
     * Disconnect user from server
     * @throws POP3Exception Error while disconnecting
     */
    public void Disconnect() throws POP3Exception {
        if(!this.CheckConnected()) {
            throw new POP3Exception("Unable to disconnect, client not connected to server.");
        }
        if(m_authenticated) {
            try {
                dialog("QUIT");
            } catch (ProtocolUnderTCPException e) {
                throw new POP3Exception("Unable to disconnect.", e);
            }
            m_authenticated = false;
        }
        try {
            super.getTcp().Close();
        } catch(TCPException e) {
            throw new POP3Exception("Unable to disconnect.", e);
        }
    }

    /**
     * Wait for a response from server
     * @return Response received from server
     * @throws POP3Exception Error while receiving response
     */
    /*
    private String Response() throws POP3Exception {
        String result;
        if(!this.CheckConnected()) {
            throw new POP3Exception("Unable to receive message, client not connected to server.");
        }
        try {
            result = m_tcp.Receive();
        } catch(TCPException e) {
            throw new POP3Exception("Unable to receive message.", e);
        }
        return result;
    }*/

    /**
     * Send a message through TCP
     * @param message message to send to server
     * @throws POP3Exception Error while sending message.
     */
    /*
    private void Message(String message) throws POP3Exception {
        if(!this.CheckConnected()) {
            throw new POP3Exception("Unable to send message, client not connected to server.");
        }
        try {
            m_tcp.Send(message);
        } catch(TCPException e) {
            throw new POP3Exception("Unable to send message.", e);
        }
    }*/

    /**
     * Retrieve a mail from its ID
     * @param id Mail's ID
     * @return String containing the mail
     * @throws POP3Exception Error while retrieving message
     */
    public String getMail(String id) throws POP3Exception {
        String cmd = "RETR " + id;
        String response;
        try {
            response = dialog(cmd);
        } catch(ProtocolUnderTCPException e) {
            throw new POP3Exception("Unable to retrieve mail " + id + ".", e);
        }
        if(!TestRegex.CheckPOP(response)) {
            m_error = response;
            throw new POP3Exception("Unable to retrieve mail" + id + ".\nServer response :\n" + response);
        }
        return this.retrieveMailString(response);
    }

    /**
     * Parse the received response to get the message with its header
     * @param response String to be parsed
     * @return Message with its header
     */
    private String retrieveMailString(String response) {
        StringBuilder sBuilder = new StringBuilder();
        String[] lines = response.split("\\n");
        sBuilder.append(lines[1])
                .append(" - ");
        for(int i = 2; i < lines.length; i++) {
            sBuilder.append(lines[i])
                    .append("\\n");
        }
        return sBuilder.toString();
    }

    /**
     * Get messages'ID list
     * @return Messages'ID list
     * @throws POP3Exception Error while retrieving ID list
     */
    public String[] getUUIDList() throws POP3Exception {
        String cmd = "UIDL";
        String response;
        try {
            response = dialog(cmd);
        } catch(ProtocolUnderTCPException e) {
            throw new POP3Exception("Unable to retrieve UUID list.", e);
        }
        if(!TestRegex.CheckPOP(response)) {
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
            if(!parts[index].equals("")) {
                String[] res = parts[index].split(" ");
                array[index-1] = res[1];
            }
        }
        return array;
    }

    /**
     * Delete a message thanks to its ID
     * @param UUID ID of the message to delete
     * @throws POP3Exception Error while deleting
     */
    public void Delete(String UUID) throws POP3Exception {
        String cmd = "DELE " + UUID;
        String response = null;
        try {
            response = dialog(cmd);
        } catch (ProtocolUnderTCPException e) {
            throw new POP3Exception("Unable to delete message.", e);
        }
        if(!TestRegex.CheckPOP(response)) {
            m_error = response;
            throw new POP3Exception("Unable to delete message " + UUID + ".\nServer response :\n  " + response);
        }
    }

    /**
     * Reset repository on server
     * @throws POP3Exception Error while resetting
     */
    public void Reset() throws POP3Exception {
        String cmd = "RSET";
        String response = null;
        try {
            response = dialog(cmd);
        } catch (ProtocolUnderTCPException e) {
            throw new POP3Exception("Unable to reset.", e);
        }
        if(!TestRegex.CheckPOP(response)) {
            m_error = response;
            throw new POP3Exception("Unable to reset.\nServer response :\n  " + response);
        }
    }

    /**
     * Get the number of mails on the server
     * @return Number of mails on the server
     * @throws POP3Exception Error while scanning server
     */
    public int getMailNumber() throws POP3Exception {
        String cmd = "STAT";
        String response = null;
        try {
            response = dialog(cmd);
        } catch (ProtocolUnderTCPException e) {
            throw new POP3Exception("Unable to stat.", e);
        }
        if(!TestRegex.CheckPOP(response)) {
            m_error = response;
            throw new POP3Exception("Unable to stat.\nServer response :\n  " + response);
        }
        int nbr = Integer.parseInt(response.split(" ")[1]);
        System.out.println("STAT : " + nbr);
        return nbr;
    }
}
