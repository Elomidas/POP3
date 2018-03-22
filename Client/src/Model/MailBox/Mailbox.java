package Model.MailBox;

import Model.Protocols.POP3.POP3;
import Model.Protocols.POP3.POP3Exception;
import Model.Protocols.POP3.POP3S;
import Model.Protocols.ProtocolUnderTCPException;

import java.io.*;
import java.util.HashMap;
import java.util.Set;

public class Mailbox {
    private HashMap<String, Mail> m_mails;
    private String[] m_UUIDs;
    private MailAddress m_user;
    private POP3 m_pop;

    //Constructor
    public Mailbox() {
        m_mails = new HashMap<>();
        m_user = null;
        m_pop = null;
        m_UUIDs = new String[0];
    }

    /**
     * Open the user's file and lock it.
     */
    private void openStorage() {
        if(m_user != null) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(m_user.getAddress() + ".pop"));
                int i = 0;
                while (this.readMail(br)) {
                    i++;
                }
                System.out.println(i + " message loaded.");
                br.close();
            } catch(FileNotFoundException e) {
                //Do nothing
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Read a mail from file.
     * @param br BufferReader initialized with the file to read.
     * @return false if the en of the file has been reached.
     */
    private boolean readMail(BufferedReader br) {
        try {
            StringBuilder sBuilder = new StringBuilder();
            String UUID = br.readLine();
            if(UUID == null) {
                return false;
            }
            while(true) {
                String line = br.readLine();
                if(line.equals(".")) {
                    sBuilder.append(".\n");
                    m_mails.put(UUID, new Mail(sBuilder.toString()));
                    return true;
                } else {
                    sBuilder.append(line).append("\n");
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Save current mails in user's storage
     */
    private void saveStorage() {
        if(m_user != null) {
            try {

                BufferedWriter writer = new BufferedWriter(new FileWriter("storage/" + m_user.getAddress() + ".pop"));

                Set<String> keys = m_mails.keySet();
                for (String key : keys) {
                    writer.write(key + "\n");
                    writer.write(m_mails.get(key).getEncoded());
                }

                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Try to join the server. Useful to test validity of address:port
     * @param address Address of the server to join (can be an IP or an URL)
     * @param port Port on which joining the server.
     * @return true if the server has been reached, false else.
     * @throws MailException Error while joining the server.
     */
    public boolean joinServer(String address, int port) throws MailException {
        m_pop = new POP3();
        try {
            m_pop.Connect(address, port);
        } catch(ProtocolUnderTCPException e) {
            throw new MailException("Your configuration " + address + ":" + port + " seems invalid...", e);
        }
        return this.ServerJoined();
    }

    /**
     * Set user's mail. Check it's validity.
     * @param user User's mail
     * @throws MailException Error if mail isn't valid.
     */
    public void setUser(String user) throws MailException {
        m_user = MailAddress.createFromString(user);
    }

    /**
     * Try this password for the previous username.
     * @param password user's password.
     * @return true if authentication successfully end
     * @throws MailException Error while authenticating
     */
    public boolean Authenticate(String password) throws MailException {
        if(!this.ServerJoined()) {
            throw new MailException("You should try to join the server before trying to authenticate yourself.");
        }
        try {
            if(m_pop.Authentication(m_user.getAddress(), password)) {
                this.openStorage();
                this.Update();
                return true;
            }
        } catch(POP3Exception e) {
            throw new MailException("Unable to authenticate user '" + m_user.getAddress() + "'", e);
        }
        return false;
    }

    /**
     * Check if server and port are correctly set.
     * @return true if all is correctly set, false else.
     */
    private boolean ServerJoined() {
        return m_pop != null && (m_pop.Status() != POP3._DISCONNECTED);
    }

    /**
     * Check if user is authenticated.
     * @return true if user is authenticated, false else.
     */
    private boolean Usable() {
        return this.ServerJoined() && m_pop.CheckConnected();
    }

    /**
     * Check if Mailbox is usable.
     * @throws MailException Mailbox isn't usable.
     */
    private void assertUsable() throws MailException {
        if(!this.Usable()) {
            throw new MailException("You are not connected.");
        }
    }

    /**
     * Get user's address.
     * @return User's address.
     */
    public String getUser() {
        return m_user.getAddress();
    }

    /**
     * Close Mailbox
     * @throws MailException Error while closing Mailbox
     */
    public void Close() throws MailException {
        this.saveStorage();
        if(this.Usable()) {
            try {
                m_pop.Disconnect();
            } catch (POP3Exception e) {
                throw new MailException("Unable to quit.", e);
            }
        }
    }

    /**
     * Do something. TODO later
     * @param strMail mail to be send
     * @param id mail ID
     * @throws MailException Error while doing something
     */
    public void AddMail(String strMail, String id) throws MailException {
        Mail m;
        try {
            m = new Mail(strMail, id);
        } catch(MailException e) {
            throw e;
        }
        m_mails.put(id, m);
    }

    /**
     * Get number of mails on the server that can be download.
     * @return number of mail
     */
    public int getMailNumber() {
        return m_UUIDs.length;
    }

    /**
     * Get number of mails currently downloaded
     * @return number of mail
     */
    public int getSize() {
        return m_mails.size();
    }

    /**
     * Get few mails
     * @param first index of the first mail to get
     * @param length maximum number of mail returned
     * @return Array of mails, maximum length passed in params
     * @throws MailException Error while generating mails'array
     */
    public Mail[] getMails(int first, int length) throws MailException {
        this.assertUsable();
        int size = (m_UUIDs.length - first);
        if(size > length) {
            size = length;
        }
        Mail[] array = new Mail[0];
        if(size > 0) {
            array = new Mail[size];
            int fromLast = m_UUIDs.length - first - 1;
            for (int i = 0; i < size; i++) {
                String UUID = m_UUIDs[fromLast - i];
                //If we didn't retrieve this mail before, we retrieve it now
                if (!m_mails.containsKey(UUID)) {
                    String message;
                    try {
                        message = m_pop.getMail(UUID);
                    } catch (POP3Exception e) {
                        throw new MailException("Unable to create mail with UUID " + UUID, e);
                    }
                    System.out.println(message);
                    String[] parts = message.split(" - ", 2);
                    Mail m = new Mail(parts[1], parts[0]);
                    m_mails.put(UUID, m);
                }
                array[i] = m_mails.get(UUID);
            }
        }
        this.saveStorage();
        return array;
    }

    /**
     * Same as getMails but update repository before generating array
     * @param first index of the first mail to get
     * @param length maximum number of mail returned
     * @return Array of mails, maximum length passed in params
     * @throws MailException Error while generating mails'array
     */
    public Mail[] getMailsUpdated(int first, int length) throws MailException{
        this.Update();
        return this.getMails(first, length);
    }

    /**
     * Update the list of downloadable mails
     * @throws MailException Error while updating the list
     */
    public void Update() throws MailException {
        this.assertUsable();
        try {
            if(m_UUIDs.length == 0 || m_pop.getMailNumber() != m_UUIDs.length) {
                m_UUIDs = m_pop.getUUIDList();
            }
        } catch(POP3Exception e) {
            e.printStackTrace();
            throw new MailException("Unable to get an updated UUID list.", e);
        }
    }

    /**
     * Delete a mail from its ID
     * @param id Mail's ID
     * @throws MailException Error while deleting the mail
     */
    public void DeleteMail(String id) throws MailException {
        this.assertUsable();
        try {
            m_mails.get(id).Delete();
            m_pop.Delete(id);
        } catch(POP3Exception e) {
            throw new MailException("Unable to delete mail " + id + ".", e);
        }
    }

    /**
     * Reset all mails on the server, cancel deleting
     * @throws MailException Error while resetting mail
     */
    public void Reset() throws MailException {
        this.assertUsable();
        try {
            m_pop.Reset();
        } catch(POP3Exception e) {
            throw new MailException("Unable to reset this repository.", e);
        }
    }
}
