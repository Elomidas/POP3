package Model.MailBox;

import java.util.ArrayList;
import java.util.Date;

import static java.util.stream.Collectors.joining;

public class Mail {
    protected MailAddress m_from;
    protected Date m_date;
    protected ArrayList<MailAddress> m_to;
    protected String m_object;
    protected String m_message;

    /*  ###
     *  # CONSTRUCTORS
     *  ###
     */

    public Mail() {
        m_from = null;
        m_to = new ArrayList<>();
        m_object = "";
        m_message = "";
        m_date = new Date();
    }

    public Mail(String encrypted) throws MailException {
        this();
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
        /*  TODO
         *  ################################################
         *  # Create Mail from String received from server #
         *  ################################################
         */
    }

    protected String encode() throws MailException {
        /*  TODO
         *  #############################################
         *  # Create String from Mail to send to server #
         *  #############################################
         */
        return "";
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

    public String getEncoded() throws MailException {
        String result;
        try {
            result = this.encode();
        } catch(MailException e) {
            throw e;
        }
        return result;
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
}
