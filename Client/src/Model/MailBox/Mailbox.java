package Model.MailBox;

import Model.Protocols.POP3.POP3;
import Model.Protocols.POP3.POP3Exception;
import Model.Protocols.POP3.POP3S;
import Model.Protocols.ProtocolUnderTCPException;
import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;

import java.io.*;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.FileHandler;

public class Mailbox {
    protected HashMap<String, Mail> m_mails;
    protected String[] m_UUIDs;
    protected MailAddress m_user;
    protected POP3 m_pop;

    //Constructor
    public Mailbox() {
        m_mails = new HashMap<>();
        m_user = null;
        m_pop = null;
        m_UUIDs = new String[0];
    }

    protected void openStorage() {
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

    protected boolean readMail(BufferedReader br) {
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
                } else if(line == null) {
                    return false;
                } else {
                    sBuilder.append(line + "\n");
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    protected void saveStorage() {
        if((m_user != null) && (m_mails != null)) {
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

    //Step 1 : join the server (check address an port, return bool)
    public boolean joinServer(String address, int port) throws MailException {
        m_pop = new POP3S();
        try {
            m_pop.Connect(address, port);
        } catch(ProtocolUnderTCPException e) {
            throw new MailException("Your configuration " + address + ":" + port + " seems invalid...", e);
        }
        return this.ServerJoined();
    }

    //Step 2 : set user (check mail validity, throw exception if not valid)
    public void setUser(String user) throws MailException {
        m_user = MailAddress.createFromString(user);
    }

    //Step 3 : try the password with previously set username
    public boolean Authenticate(String password) throws MailException {
        if(this.ServerJoined() == false) {
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

    //Check if server and port are correctly set
    public boolean ServerJoined() {
        if(m_pop == null) {
            return false;
        }
        return (m_pop.Status() != POP3._DISCONNECTED);
    }

    //Check if user is authenticated
    public boolean Usable() {
        if(this.ServerJoined() == false) {
            return false;
        }
        return m_pop.CheckConnected();
    }

    protected void assertUsable() throws MailException {
        if(this.Usable() == false) {
            throw new MailException("You are not connected.");
        }
    }

    //Return user address
    public String getUser() {
        return m_user.getAddress();
    }

    //Close connection
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

    //TODO later
    public void AddMail(String strMail, String id) throws MailException {
        Mail m;
        try {
            m = new Mail(strMail, id);
        } catch(MailException e) {
            throw e;
        }
        m_mails.put(id, m);
    }

    //Number of mail currently downloadable
    public int getMailNumber() {
        return m_UUIDs.length;
    }

    //Number of mail currently downloaded
    public int getSize() {
        return m_mails.size();
    }

    /*  Get few mails.
     *  Ignore new mails
     *  Parameters :
     *      first :     Index of the first mail
     *      length :    Number of mails wanted
     *  Return :
     *      Mail array
     *  Throw :
     *      MailException in case of error
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
                if (m_mails.containsKey(UUID) == false) {
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

    /*  Get few mails.
     *  Check if there is new mails
     *  Parameters :
     *      first :     Index of the first mail
     *      length :    Number of mails wanted
     *  Return :
     *      Mail array
     *  Throw :
     *      MailException in case of error
     */
    public Mail[] getMailsUpdated(int first, int length) throws MailException{
        this.Update();
        return this.getMails(first, length);
    }

    //TODO later
    public void SendMail(String to, String object, String message) throws MailException {
        this.assertUsable();
        //TODO
    }

    //Update the list of downloadable mails
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

    //Delete a mail
    public void DeleteMail(Mail m) throws MailException {
        this.DeleteMail(m.getID());
    }

    //Delete a mail
    public void DeleteMail(String id) throws MailException {
        this.assertUsable();
        try {
            m_mails.get(id).Delete();
            m_pop.Delete(id);
        } catch(POP3Exception e) {
            throw new MailException("Unable to delete mail " + id + ".", e);
        }
    }

    //Cancel all delete tags
    public void Reset() throws MailException {
        this.assertUsable();
        try {
            m_pop.Reset();
        } catch(POP3Exception e) {
            throw new MailException("Unable to reset this repository.", e);
        }
    }
}
