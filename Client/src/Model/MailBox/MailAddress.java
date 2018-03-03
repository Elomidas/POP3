package Model.MailBox;

import Uilities.TestRegex;

public class MailAddress {
    protected String m_address;

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
    }

    public String getAddress() {
        return m_address;
    }

    public String toString() {
        return m_address;
    }
}
