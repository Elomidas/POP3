import java.util.UUID;

/**
 * Created by tardy on 26/02/2018.
 */
public class Email {
    protected UUID m_id;
    protected String m_message;
    protected String m_date;
    protected String m_subject;
    protected Utilisateur m_destinataire;
    protected Utilisateur m_emetteur;
    protected boolean m_etat;

    protected static final String _DATE = "Date: ";
    protected static final String _FROM = "From: ";
    protected static final String _SUBJECT = "Subject: ";
    protected static final String _MIME = "MIME-Version: 1.0";
    protected static final String _CONTENT  = "Content-Type: text/plain; charset)UTF-8\nContent-Transfer-Encoding: quoted-printable";
    public static final String _EOM = "\n.\n";



    public Email(UUID m_id, String m_message, Utilisateur m_destinataire, Utilisateur m_emetteur, boolean m_etat) {
        this.m_id = m_id;
        this.m_message = m_message;
        this.m_destinataire = m_destinataire;
        this.m_emetteur = m_emetteur;
        this.m_etat = m_etat;
    }


    public String encode() {
        StringBuilder sbuilder = new StringBuilder();
        sbuilder.append(_DATE)
                .append(m_date)
                .append("\n")
                .append(_FROM)
                .append(m_emetteur)
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

    public boolean getM_etat() {
        return m_etat;
    }

    public void setM_etat(boolean m_etat) {
        this.m_etat = m_etat;
    }

    public UUID getM_id() {
        return m_id;
    }

    public void setM_id(UUID m_id) {
        this.m_id = m_id;
    }

    public String getM_message() {
        return m_message;
    }

    public void setM_message(String m_message) {
        this.m_message = m_message;
    }

    public Utilisateur getM_destinataire() {
        return m_destinataire;
    }

    public void setM_destinataire(Utilisateur m_destinataire) {
        this.m_destinataire = m_destinataire;
    }

    public Utilisateur getM_emetteur() {
        return m_emetteur;
    }

    public void setM_emetteur(Utilisateur m_emetteur) {
        this.m_emetteur = m_emetteur;
    }
}
