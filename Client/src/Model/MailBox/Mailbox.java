package Model.MailBox;

import Model.Protocols.POP3.POP3;
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

    public void setUser(String user) throws MailException {
        if(TestRegex.CheckMail(user) == false) {
            throw new MailException(user + " isn't a correct mail address.");
        }
        m_user = user;
    }

    public boolean Authenticate(String password) {
        //
    }

    public boolean Usable() {
        if(m_pop == null) {
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

    public void SendMail(String to, String object, String message) throws MailException {
        //
    }
}
