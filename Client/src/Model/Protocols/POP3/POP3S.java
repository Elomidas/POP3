package Model.Protocols.POP3;

import Model.Protocols.TCP.TCPException;
import Utilities.TestRegex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class POP3S extends POP3 {
    protected String m_secureKey;
    protected MessageDigest m_digest;
    /*  A Secure version for POP3
     *  Authentication with APOP command
     */

    public POP3S() {
        super();
        m_secureKey = "";
        try {
            m_digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void Connect(String address, int port) throws POP3Exception {
        super.Connect(address, port);
        if(this.CheckConnected()) {
            try {
                String response = m_tcp.Receive();
                if (TestRegex.CheckPOP(response)) {
                    String[] matches = TestRegex.Submatches("\\+OK[^<]+(<.*>)", response);
                    if (matches.length != 1) {
                        throw new POP3Exception("Unable to get  MD5 key.");
                    } else {
                        m_secureKey = matches[0];
                    }
                }
            } catch (TCPException e) {
                throw new POP3Exception("Unable to get MD5 key.", e);
            }
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
    @Override
    public boolean Authentication(String login, String password) throws POP3Exception {
        System.out.println(login+":"+password);
        if(this.CheckConnected() == false) {
            throw new POP3Exception("Unable to authenticate, client not connected to server.");
        }
        if(m_authenticated) {
            throw new POP3Exception("Already authenticate.");
        }
        if(APOP(login, password)) {
            m_authenticated = true;
        }
        return m_authenticated;
    }

    protected boolean APOP(String login, String password) {
        if(checkKey()) {
            StringBuilder mBuilder = new StringBuilder();
            StringBuilder sBuilder = new StringBuilder();
            mBuilder.append(m_secureKey)
                    .append(password);
            byte[] digestedBytes = m_digest.digest(mBuilder.toString().getBytes());
            String digestedPassword = String.format("%02x", digestedBytes);
            sBuilder.append("APOP ")
                    .append(login)
                    .append(" ")
                    .append(digestedPassword);
            //String response = dialog()
        }
        return false;
    }

    protected  boolean checkKey() {
        return TestRegex.CheckMD5(m_secureKey);
    }
}
