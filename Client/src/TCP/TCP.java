package TCP;

import java.io.*;
import java.net.*;
import java.util.logging.Logger;

public class TCP {
    protected int m_port;
    protected Socket m_socket;
    protected InetAddress m_server;
    protected BufferedReader m_input;
    protected PrintStream m_output;
    protected Logger m_logger;

    public TCP(Logger logs) {
        m_port = 0;
        m_socket = null;
        m_server = null;
        m_input = null;
        m_output = null;
        m_logger = logs;
    }

    protected String getSocketString() {
        String info;
        if(m_socket == null) {
            info = "Socket not connected, stored address is " + m_server + ":" + m_port;
        } else {
            info = m_socket.getInetAddress().getHostAddress() + ":" + m_socket.getPort();
        }
        return info;
    }

    public void setServerPort(int port) {
        m_port = port;
    }

    protected void createIO() throws TCPException {
        if(m_socket == null) {
            String err = "Application isn't able to create IO streams : socket can't be null";
            throw new TCPException(err);
        } else {
            try {
                m_input = new BufferedReader(new InputStreamReader(m_socket.getInputStream()));
                m_output = new PrintStream(m_socket.getOutputStream());
            } catch(Exception e) {
                String err = "Unable to create IO streams from server " + getSocketString();
                throw new TCPException(err, e);
            }
        }
    }

    public void setServerAddress(String address) throws TCPException {
        try {
            if(TestRegex.Match("(\\d+\\.)+\\d+", address)) {
                //Here, address contains an IP address
                m_server = InetAddress.getByAddress(address.getBytes());
            } else {
                //Here, address does not contain an IP address, so we handle it as a domain name
                m_server = InetAddress.getByName(address);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void Connect() throws TCPException {
        if((m_port != 0) && (m_server != null)) {
            try {
                m_socket = new Socket(m_server, m_port);
                BufferedReader in = new BufferedReader(new InputStreamReader(m_socket.getInputStream()));
                PrintStream out = new PrintStream(m_socket.getOutputStream());
                createIO();
            } catch(Exception e) {
                throw new TCPException("Error while trying to connect to server " + m_server.getHostAddress() + " on port " + m_port, e);
            }
        } else {
            String err;
            if(m_port != 0) {
                err = "You didn't set a valid port (" + m_port + ").";
            } else if(m_server != null) {
                err = "Server isn't set (" + m_server + "). Check your configuration.";
            } else {
                err = "Both of the given port (" + m_port + ") and server address (" + m_server + ") are invalid. Why did you do that ?";
            }
            throw new TCPException(err);
        }
    }

    public void Run() throws  TCPException {

        try {
            Connect();
            m_logger.info("Test OK");

        } catch (Exception e) {
            throw e;
        }
    }
}
