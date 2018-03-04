package Model.MailBox;

import Model.Protocols.POP3.POP3;
import Model.Protocols.POP3.POP3Exception;

import java.io.*;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.FileHandler;

public class Mailbox {
    protected HashMap<String, Mail> m_mails;
    protected String[] m_UUIDs;
    protected MailAddress m_user;
    protected POP3 m_pop;

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
                if(line == ".") {
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
        try {

            BufferedWriter writer = new BufferedWriter(new FileWriter(m_user.getAddress() + ".pop"));

            Set<String> keys = m_mails.keySet();
            for(String key : keys) {
                writer.write(key + "\n");
                writer.write(m_mails.get(key).getEncoded());
            }

            writer.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public boolean joinServer(String address, int port) throws MailException {
        m_pop = new POP3();
        try {
            m_pop.Connect(address, port);
        } catch(POP3Exception e) {
            throw new MailException("Your configuration " + address + ":" + port + " seems invalid...", e);
        }
        return this.ServerJoined();
    }

    public void setUser(String user) throws MailException {
        m_user = MailAddress.createFromString(user);
    }

    public boolean Authenticate(String password) throws MailException {
        if(this.ServerJoined() == false) {
            throw new MailException("You should try to join the server before trying to authenticate yourself.");
        }
        boolean result = false;
        try {
            if(m_pop.Authentication(m_user.getAddress(), password)) {
                this.openStorage();
                return true;
            }
        } catch(POP3Exception e) {
            throw new MailException("Unable to authenticate user '" + m_user.getAddress() + "'", e);
        }
        return false;
    }

    public boolean ServerJoined() {
        if(m_pop == null) {
            return false;
        }
        return (m_pop.Status() != POP3._DISCONNECTED);
    }

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

    public String getUser() {
        return m_user.getAddress();
    }

    public void Close() throws MailException {
        this.saveStorage();
        try {
            m_pop.Disconnect();
        } catch(POP3Exception e) {
            throw new MailException("Unable to quit.", e);
        }
    }

    public void AddMail(String strMail, String id) throws MailException {
        Mail m;
        try {
            m = new Mail(strMail, id);
        } catch(MailException e) {
            throw e;
        }
        m_mails.put(id, m);
    }

    public int getMailNumber() {
        return m_UUIDs.length;
    }

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
        Mail[] array = new Mail[length];
        for(int i = 0; (i < length) && (i < m_UUIDs.length); i++) {
            String UUID = m_UUIDs[first+i];
            //If we didn't retrieve this mail before, we retrieve it now
            if(m_mails.containsKey(UUID) == false) {
                String message;
                try {
                    message = m_pop.getMail(UUID);
                } catch(POP3Exception e) {
                    throw new MailException("Unable to create mail with UUID " + UUID, e);
                }
                Mail m = new Mail(message);
                m_mails.put(UUID, m);
            }
            array[i] = m_mails.get(UUID);
        }
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

    public void SendMail(String to, String object, String message) throws MailException {
        this.assertUsable();
        //TODO
    }

    public void Update() throws MailException {
        this.assertUsable();
        try {
            if(m_pop.getMailNumber() != m_UUIDs.length) {
                m_UUIDs = m_pop.getUUIDList();
            }
        } catch(POP3Exception e) {
            throw new MailException("Unable to get an updated UUID list.", e);
        }
    }

    public void DeleteMail(Mail m) throws MailException {
        this.DeleteMail(m.getID());
    }

    public void DeleteMail(String id) throws MailException {
        this.assertUsable();
        try {
            m_pop.Delete(id);
        } catch(POP3Exception e) {
            throw new MailException("Unable to delete mail " + id + ".", e);
        }
    }

    public void Reset() throws MailException {
        this.assertUsable();
        try {
            m_pop.Reset();
        } catch(POP3Exception e) {
            throw new MailException("Unable to reset this repository.", e);
        }
    }
}
