package Messagerie;

import Utilitaires.TestRegex;

public class MailAddress {
    protected String m_address;
    protected String m_username;

    static public MailAddress createFromString(String address) throws MailException {
        if(TestRegex.CheckMail(address)) {
            return new MailAddress(address);
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("'")
                    .append(address)
                    .append("' isn't a valid mail address.");
            throw new MailException(builder.toString());
        }
    }

    protected MailAddress(String address) {
        m_address = address;
        m_username = "";
    }

    public String getAddress() {
        return m_address;
    }

    public String getUsername() {
        return m_username;
    }

    public void setUsername(String username) {
        m_username = username;
    }

    public String toString() {
        String result;
        if(m_username == "") {
            result = m_address;
        } else {
            StringBuilder builder = new StringBuilder(m_username);
            builder.append(" <")
                    .append(m_address)
                    .append(">");
            result = builder.toString();
        }
        return result;
    }
}
