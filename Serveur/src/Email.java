import javax.print.DocFlavor;

/**
 * Created by tardy on 26/02/2018.
 */
public class Email {
    protected int m_id;
    protected String m_message;
    protected Utilisateur m_destinataire;
    protected Utilisateur m_emetteur;
    protected boolean m_etat;


    public Email(int m_id, String m_message, Utilisateur m_destinataire, Utilisateur m_emetteur, boolean m_etat) {
        this.m_id = m_id;
        this.m_message = m_message;
        this.m_destinataire = m_destinataire;
        this.m_emetteur = m_emetteur;
        this.m_etat = m_etat;
    }

    public boolean getM_etat() {
        return m_etat;
    }

    public void setM_etat(boolean m_etat) {
        this.m_etat = m_etat;
    }

    public int getM_id() {
        return m_id;
    }

    public void setM_id(int m_id) {
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
