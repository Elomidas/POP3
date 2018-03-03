package Model.MailBox;

import Model.Protocols.POP3.POP3;
import Model.Protocols.POP3.POP3Exception;
import Uilities.TestRegex;

import java.util.ArrayList;

public class Mailbox {
    protected ArrayList<Mail> m_mails;
    protected String m_user;
    protected POP3 m_pop;

    public Mailbox() {
        m_mails = new ArrayList<>();
        m_user = "";
        m_pop = null;
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
        if(TestRegex.CheckMail(user) == false) {
            throw new MailException(user + " isn't a correct mail address.");
        }
        m_user = user;
    }

    public boolean Authenticate(String password) throws MailException {
        if(this.ServerJoined() == false) {
            throw new MailException("You should try to join the server before trying to authenticate yourself.");
        }
        boolean result = false;
        try {
            m_pop.Authentication(m_user, password);
        } catch(POP3Exception e) {
            throw new MailException("Unable to authenticate user '" + m_user + "'", e);
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

    public String getUser() {
        return m_user;
    }

    public void AddMail(String strMail) throws MailException {
        try {
            Mail m = new Mail(strMail);
            m_mails.add(m);
        } catch(MailException e) {
            throw e;
        }
    }

    public int getSize() {

        return m_mails.size();
    }

    public ArrayList<Mail> getMails() {
        return m_mails;
    }

    public void SendMail(String to, String object, String message) throws MailException {
        //
    }
}
