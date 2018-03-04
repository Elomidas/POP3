package Model.MailBox;

import Uilities.TestRegex;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

public class Mail {
    protected String m_id;
    protected MailAddress m_from;
    protected String m_date;
    protected ArrayList<MailAddress> m_to;
    protected String m_subject;
    protected String m_message;

    protected static final String _DATE = "Date: ";
    protected static final String _FROM = "From: ";
    protected static final String _SUBJECT = "Subject: ";
    protected static final String _MIME = "MIME-Version: 1.0";
    protected static final String _CONTENT  = "Content-Type: text/plain; charset)UTF-8\nContent-Transfer-Encoding: quoted-printable";
    public static final String _EOM = "\n.\n";
    protected static final String _PATTERN = (_DATE + " ([^\n]*)\n" + _FROM + " ([^\n]*)\n" + _SUBJECT + " ([^\n]*)\n" + _MIME + "\n" + _CONTENT + "\n(.*)" + _EOM).replace("\n", "\\n");

    /*  ###
     *  # CONSTRUCTORS
     *  ###
     */

    public Mail(String id) {
        m_id = id;
        m_from = null;
        m_to = new ArrayList<>();
        m_subject = "";
        m_message = "";
        m_date = Date.from(Instant.now()).toString();
    }

    public Mail(String encrypted, String id) throws MailException {
        this(id);
        try {
            this.fromEncoded(encrypted);
        } catch(MailException e) {
            throw e;
        }
    }

    /*  ###
     *  # TRANSFORMATION FUNCTIONS
     *  ###
     */

    protected void decode(String encrypted) throws MailException {
        String[] fields = TestRegex.Submatches(_PATTERN, encrypted);
        if(fields.length != 4) {
            throw new MailException(encrypted + " isn't a valid mail string, only " + fields.length + " field(s) found");
        }
        this.m_date = fields[0];
        this.m_from = MailAddress.createFromString(fields[1]);
        this.m_subject = fields[2];
        this.m_message = fields[3];
    }

    protected String encode() {
        StringBuilder sbuilder = new StringBuilder();
        sbuilder.append(_DATE)
                .append(m_date)
                .append("\n")
                .append(_FROM)
                .append(m_from)
                .append(_SUBJECT)
                .append(m_subject)
                .append("\n")
                .append(_MIME)
                .append("\n")
                .append(_CONTENT)
                .append("\n\n")
                .append(m_message)
                .append("\n")
                .append(_EOM);
        return sbuilder.toString();
    }

    /*  ###
     *  # ACCESSORS
     *  ###
     */

    public String getMessage() {
        return m_message;
    }

    public String getFrom() {
        String address = "unknown";
        if(m_from != null) {
            address = m_from.getAddress();
        }
        return address;
    }

    public ArrayList<MailAddress> getTo() {
        return m_to;
    }

    public String getToString() {
        String field = "";
        if((m_to != null) && (m_to.size() > 0)) {
            StringBuilder builder = new StringBuilder();
            String separator = "";
            for(MailAddress address : m_to) {
                if(separator.equals("")) {
                    separator = ", ";
                } else {
                    builder.append(separator);
                }
                builder.append(address.toString());
            }
            field = builder.toString();
        }
        return field;
    }

    public String getEncoded() {
        String result;
        result = this.encode();
        return result;
    }

    public String getID() {
        return m_id;
    }

    /*  ###
     *  # MUTATORS
     *  ###
     */
    public void fromEncoded(String encrypted) throws MailException {
        try {
            this.decode(encrypted);
        } catch(MailException e) {
            throw e;
        }
    }

    public void setFrom(MailAddress from) {
        if(from != null) {
            m_from = from;
        }
    }

    public void setTo(MailAddress to) {
        if(to != null) {
            if(m_to == null) {
                m_to = new ArrayList<>();
            }
            m_to.add(to);
        }
    }
}
