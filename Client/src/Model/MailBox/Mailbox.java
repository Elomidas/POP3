package Model.MailBox;

import Model.Protocols.POP3.POP3;
import Model.Protocols.POP3.POP3Exception;
import Model.Protocols.POP3.POP3S;
import Model.Protocols.ProtocolUnderTCPException;
import Model.Protocols.SMTP.SMTP;
import Model.Protocols.SMTP.SMTPDispatcher;
import Model.Protocols.SMTP.SMTPException;
import Utilities.DNS;
import Utilities.TestRegex;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Mailbox {
    private HashMap<String, Mail> mails;
    private String[] UUIDs;
    private MailAddress user;
    private POP3S pop3;
    private SMTPDispatcher smtp;

    //Constructor
    public Mailbox() {
        mails = new HashMap<>();
        user = null;
        pop3 = null;
        smtp = null;
        UUIDs = new String[0];
    }

    /**
     * Open the user's file and lock it.
     */
    private void openStorage() {
        if(user != null) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(user.getAddress() + ".pop"));
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
                    mails.put(UUID, new Mail(sBuilder.toString()));
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
        if(user != null) {
            try {

                BufferedWriter writer = new BufferedWriter(new FileWriter("storage/" + user.getAddress() + ".pop"));

                Set<String> keys = mails.keySet();
                for (String key : keys) {
                    writer.write(key + "\n");
                    writer.write(mails.get(key).getEncoded());
                }

                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Try to join the server. Useful to test validity of address:port
     * @param domain Domain name of the server to join.
     * @return true if the server has been reached, false else.
     * @throws MailException Error while joining the server.
     */
    public boolean joinServer(String domain) throws MailException {
        try {
            if(pop3 != null && pop3.CheckConnected()) {
                pop3.Close();
            }
        } catch (ProtocolUnderTCPException e) {
            throw new MailException("Unable to close POP3", e);
        }
        pop3 = new POP3S();
        try {
            pop3.Connect(domain);
        } catch(ProtocolUnderTCPException e) {
            pop3 = null;
            throw new MailException("Your domain name " + domain + " seems invalid as POP3S server...", e);
        }
        if(smtp == null) {
            try {
                smtp = new SMTPDispatcher();
            } catch (SMTPException e) {
                throw new MailException("Unable to prepare SMTP connections.", e);
            }
        }
        return this.ServerJoined();
    }

    /**
     * Set user's mail. Check it's validity.
     * @param user User's mail
     * @throws MailException Error if mail isn't valid.
     */
    public void setUser(String user) throws MailException {
        this.user = MailAddress.createFromString(user);
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
            if(pop3.Authentication(user.getAddress(), password)) {
                this.openStorage();
                this.Update();
                return true;
            }
        } catch(POP3Exception e) {
            throw new MailException("Unable to authenticate user '" + user.getAddress() + "'", e);
        }
        return false;
    }

    /**
     * Check if server and port are correctly set.
     * @return true if all is correctly set, false else.
     */
    private boolean ServerJoined() {
        return pop3 != null && (pop3.Status() != POP3._DISCONNECTED);
    }

    /**
     * Check if user is authenticated.
     * @return true if user is authenticated, false else.
     */
    private boolean Usable() {
        return this.ServerJoined() && pop3.CheckConnected();
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
        return user.getAddress();
    }

    /**
     * Close Mailbox
     * @throws MailException Error while closing Mailbox
     */
    public void Close() throws MailException {
        this.saveStorage();
        if(this.Usable()) {
            try {
                pop3.Disconnect();
            } catch (POP3Exception e) {
                throw new MailException("Unable to quit.", e);
            }
        }
    }

    /**
     * Send a mail with SMTP
     * @param to Address to which send the mail, separated with a ';'
     * @param subject mail's subject
     * @param mail mail's body
     * @return Error list
     * @throws MailException Error while doing something
     */
    public List<String> SendMail(String to, String subject, String mail) throws MailException {
        try {
            return smtp.SendMail(to, this.user.getAddress(), subject, mail);
        } catch (SMTPException e) {
            throw new MailException("Unable to send mail(s).", e);
        }
    }

    /**
     * Get number of mails on the server that can be download.
     * @return number of mail
     */
    public int getMailNumber() {
        return UUIDs.length;
    }

    /**
     * Get number of mails currently downloaded
     * @return number of mail
     */
    public int getSize() {
        return mails.size();
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
        int size = (UUIDs.length - first);
        if(size > length) {
            size = length;
        }
        Mail[] array = new Mail[0];
        if(size > 0) {
            array = new Mail[size];
            int fromLast = UUIDs.length - first - 1;
            for (int i = 0; i < size; i++) {
                String UUID = UUIDs[fromLast - i];
                //If we didn't retrieve this mail before, we retrieve it now
                if (!mails.containsKey(UUID)) {
                    String message;
                    try {
                        message = pop3.getMail(UUID);
                    } catch (POP3Exception e) {
                        throw new MailException("Unable to create mail with UUID " + UUID, e);
                    }
                    System.out.println(message);
                    String[] parts = message.split(" - ", 2);
                    Mail m = new Mail(parts[1], parts[0]);
                    mails.put(UUID, m);
                }
                array[i] = mails.get(UUID);
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
    private void Update() throws MailException {
        this.assertUsable();
        try {
            if(UUIDs.length == 0 || pop3.getMailNumber() != UUIDs.length) {
                UUIDs = pop3.getUUIDList();
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
            mails.get(id).Delete();
            pop3.Delete(id);
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
            pop3.Reset();
        } catch(POP3Exception e) {
            throw new MailException("Unable to reset this repository.", e);
        }
    }
}