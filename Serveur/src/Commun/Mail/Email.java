package Commun.Mail;

import Commun.Utiles.TestRegex;
import Commun.Utilisateur.RepertoireUtilisateur;
import Commun.Utilisateur.Utilisateur;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by tardy on 26/02/2018.
 */
public class Email {
    protected String m_id;
    protected String m_message;
    protected String m_date;
    protected String m_subject;
    protected ArrayList<Utilisateur> m_destinataire;
    protected Utilisateur m_emetteur;
    protected boolean m_etat;

    protected static final String _DATE = "Date: ";
    protected static final String _FROM = "From: ";
    protected static final String _SUBJECT = "Subject: ";
    protected static final String _MIME = "MIME-Version: 1.0";
    protected static final String _CONTENT  = "Content-Type: text/plain; charset: UTF-8\nContent-Transfer-Encoding: quoted-printable";
    public static final String _EOM = "\n.\n";
    protected static final String _PATTERN = (_DATE + "([^\\\\]*)\n" + _FROM + "([^\\\\]*)\n" + _SUBJECT + "([^\\\\]*)\n" + _MIME + "\n" + _CONTENT + "\n(.*)\n" + _EOM);



    public Email(String m_id, String m_message, ArrayList<Utilisateur> m_destinataire, Utilisateur m_emetteur, boolean m_etat) {
        this.m_id = m_id;
        this.m_message = m_message;
        this.m_destinataire = m_destinataire;
        this.m_emetteur = m_emetteur;
        this.m_etat = m_etat;
    }

    public Email(ArrayList<Utilisateur> dest, String encoded, RepertoireUtilisateur list) {
        m_destinataire = dest;
        decode(encoded, list);
    }

    public Email(String id, ArrayList<Utilisateur> dest, Utilisateur emetteur) {
        m_id = id;
        m_destinataire = dest;
        m_emetteur = emetteur;
        DateFormat formatDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        this.m_date = formatDate.format(date);
        this.m_subject = null;
        this.m_message = "";
        this.m_etat=true;
    }

    protected void decode(String encrypted, RepertoireUtilisateur usrs) {
        String[] fields = TestRegex.Submatches(_PATTERN.replace("\n", "\\\\n"), encrypted.replace("\n", "\\n"));
        if(fields.length != 4) {
            System.out.println("Fail, not enough fields (" + fields.length + ").");
        } else {
            this.m_date = fields[0];
            this.m_subject = fields[2];
            this.m_message = fields[3];
            Utilisateur utilisateur = new Utilisateur(fields[1], usrs.getIdMax());
            this.m_emetteur = utilisateur;
        }
    }

    public String encode() {
        StringBuilder sbuilder = new StringBuilder();
        sbuilder.append(m_id)
                .append("\n")
                .append(_DATE)
                .append(m_date)
                .append("\n")
                .append(_FROM)
                .append(m_emetteur.getM_adresseEmail())
                .append("\n")
                .append(_SUBJECT)
                .append(m_subject)
                .append("\n")
                .append(_MIME)
                .append("\n")
                .append(_CONTENT)
                .append("\n")
                .append(m_message)
                .append("\n")
                .append(_EOM);
        return sbuilder.toString();
    }

    public String encodeWithReturn() {
        StringBuilder sbuilder = new StringBuilder();
        sbuilder.append(m_id)
                .append("\r\n")
                .append(_DATE)
                .append(m_date)
                .append("\r\n")
                .append(_FROM)
                .append(m_emetteur.getM_adresseEmail())
                .append("\r\n")
                .append(_SUBJECT)
                .append(m_subject)
                .append("\r\n")
                .append(_MIME)
                .append("\r\n")
                .append(_CONTENT)
                .append("\r\n")
                .append(m_message)
                .append("\r\n")
                .append("\r\n")
                .append(_EOM);
        return sbuilder.toString();
    }

    public boolean getM_etat() {
        return m_etat;
    }

    public void setM_etat(boolean m_etat) {
        this.m_etat = m_etat;
    }

    public String getM_id() {
        return m_id;
    }

    public void setM_id(String m_id) {
        this.m_id = m_id;
    }

    public String getM_message() {
        return m_message;
    }

    public void setM_message(String m_message) {
        this.m_message = m_message;
    }

    public ArrayList<Utilisateur> getM_destinataires() {
        return m_destinataire;
    }

    public void setM_destinataire(ArrayList<Utilisateur> m_destinataire) {
        this.m_destinataire = m_destinataire;
    }

    public Utilisateur getM_emetteur() {
        return m_emetteur;
    }

    public void setM_emetteur(Utilisateur m_emetteur) {
        this.m_emetteur = m_emetteur;
    }

    public String getM_subject() {
        return m_subject;
    }

    public void setM_subject(String m_subject) {
        this.m_subject = m_subject;
    }

    public int Size() {
        int size = 0;
        try {
            size = this.encode().getBytes("UTF-8").length;
        } catch(IOException e) {
            e.printStackTrace();
        }
        return size;
    }

    public void addRecipient(Utilisateur utilisateur) {
        if (!this.m_destinataire.contains(utilisateur)) {
            this.m_destinataire.add(utilisateur);
        }
    }
}
