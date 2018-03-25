package Model.Protocols.POP3;

import Model.Protocols.ProtocolUnderTCPException;
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

    /**
     * Trying to join the server
     * @param address   Server's address (IP or URL)
     * @param port      Port on which trying to join the server
     * @throws POP3Exception Exception during connection
     */
    @Override
    public void Connect(String address, int port) throws POP3Exception {
        try {
            super.Connect(address, port);
        } catch (ProtocolUnderTCPException e) {
            throw new POP3Exception("Unable to connect.", e);
        }
        if(this.CheckConnected()) {
            try {
                String response = super.getTcp().Receive();
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

    /**
     * Authentication function with password encoded through MD5 protocol
     * @param login     User's login
     * @param password  User's password
     * @return true if authentication succeeded, false else
     * @throws POP3Exception Error with authentication
     */
    @Override
    public boolean Authentication(String login, String password) throws POP3Exception {
        if(this.CheckConnected() == false) {
            throw new POP3Exception("Unable to authenticate, client not connected to server.");
        }
        if(m_authenticated) {
            throw new POP3Exception("Already authenticated.");
        }
        if(APOP(login, password)) {
            m_authenticated = true;
        }
        return m_authenticated;
    }

    /**
     * Send APOP command
     * @param login     User's login
     * @param password  User's password
     * @return true if authentication succeeded, false else
     * @throws POP3Exception Error during message sending
     */
    protected boolean APOP(String login, String password) throws POP3Exception {
        if(checkKey()) {
            String digestedPassword = this.encrypt(password);
            StringBuilder sBuilder = new StringBuilder();
            sBuilder.append("APOP ")
                    .append(login)
                    .append(" ")
                    .append(digestedPassword);
            String response = null;
            try {
                response = dialog(sBuilder.toString());
            } catch (ProtocolUnderTCPException e) {
                throw new POP3Exception("APOP failed.", e);
            }
            if(TestRegex.CheckPOP(response)) {
                return true;
            } else {
                throw new POP3Exception("Unable to authenticate : \n" + response);
            }
        }
        return false;
    }

    /**
     * Encrypt the string given in parameter with the secure key previously get
     * @param clear String to be encrypted
     * @return Encrypted String
     * @throws POP3Exception If secure key is not set
     */
    protected String encrypt(String clear) throws POP3Exception {
        if(this.checkKey() == false) {
            throw new POP3Exception("Secure Key not set");
        }
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(m_secureKey)
                .append(clear);
        byte[] digestedBytes = m_digest.digest(sBuilder.toString().getBytes());
        StringBuilder returnBuilder = new StringBuilder();
        for(int i = 0; i < digestedBytes.length; i++) {
            returnBuilder.append(String.format("%02X", digestedBytes[i]));
        }
        return returnBuilder.toString();
    }

    /**
     * Check if secure key is correctly set
     * @return true if secure key is correct, false else
     */
    protected  boolean checkKey() {
        return TestRegex.CheckMD5(m_secureKey);
    }
}
