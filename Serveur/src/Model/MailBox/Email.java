package Model.MailBox;

import Utilities.TestRegex;
import Model.Utilisateur.Utilisateur;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Email {
    private String id;
    private String message;
    private String date;
    private String subject;
    private ArrayList<Utilisateur> destinataire;
    private Utilisateur emetteur;
    private boolean etat;

    private static final String _DATE = "Date: ";
    private static final String _FROM = "From: ";
    private static final String _SUBJECT = "Subject: ";
    private static final String _MIME = "MIME-Version: 1.0";
    private static final String _CONTENT  = "Content-Type: text/plain; charset: UTF-8\nContent-Transfer-Encoding: quoted-printable";
    private static final String _EOM = "\n.\n";
    private static final String _PATTERN = (_DATE + "([^\\\\]*)\n" + _FROM + "([^\\\\]*)\n" + _SUBJECT + "([^\\\\]*)\n" + _MIME + "\n" + _CONTENT + "\n(.*)\n" + _EOM);


    public Email(ArrayList<Utilisateur> dest, String encoded, ArrayList<Utilisateur> list) {
        this.destinataire = dest;
        decode(encoded, list);
    }

    public Email(String id, ArrayList<Utilisateur> dest, Utilisateur emetteur) {
        this.id = id;
        this.destinataire = dest;
        this.emetteur = emetteur;
        DateFormat formatDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        this.date = formatDate.format(date);
        this.subject = null;
        this.message = "";
    }

    private void decode(String encrypted, ArrayList<Utilisateur> usrs) {
        String[] fields = TestRegex.Submatches(_PATTERN.replace("\n", "\\\\n"), encrypted.replace("\n", "\\n"));
        if(fields.length != 4) {
            System.out.println("Fail, not enough fields (" + fields.length + ").");
        } else {
            date = fields[0];
            subject = fields[2];
            message = fields[3];
            for (Utilisateur u : usrs) {
                if (u.getAdresseEmail().equals(fields[1])) {
                    emetteur = u;
                }
            }
        }
    }

    public String encode() {
        StringBuilder sbuilder = new StringBuilder();
        sbuilder.append(id)
                .append("\n")
                .append(_DATE)
                .append(date)
                .append("\n")
                .append(_FROM)
                .append(emetteur.getAdresseEmail())
                .append("\n")
                .append(_SUBJECT)
                .append(subject)
                .append("\n")
                .append(_MIME)
                .append("\n")
                .append(_CONTENT)
                .append("\n")
                .append(message)
                .append("\n")
                .append(_EOM);
        return sbuilder.toString();
    }

    public String encodeWithReturn() {
        StringBuilder sbuilder = new StringBuilder();
        sbuilder.append(id)
                .append("\r\n")
                .append(_DATE)
                .append(date)
                .append("\r\n")
                .append(_FROM)
                .append(emetteur.getAdresseEmail())
                .append("\r\n")
                .append(_SUBJECT)
                .append(subject)
                .append("\r\n")
                .append(_MIME)
                .append("\r\n")
                .append(_CONTENT)
                .append("\r\n")
                .append(message)
                .append("\r\n")
                .append("\r\n")
                .append(_EOM);
        return sbuilder.toString();
    }

    public boolean getEtat() {
        return etat;
    }

    public void setEtat(boolean etat) {
        this.etat = etat;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<Utilisateur> getM_destinataires() {
        return destinataire;
    }

    public void setDestinataire(ArrayList<Utilisateur> destinataire) {
        this.destinataire = destinataire;
    }

    public Utilisateur getEmetteur() {
        return emetteur;
    }

    public void setEmetteur(Utilisateur emetteur) {
        this.emetteur = emetteur;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int Size() {
        int size = 0;
        try {
            size = encode().getBytes("UTF-8").length;
        } catch(IOException e) {
            e.printStackTrace();
        }
        return size;
    }

    public void addRecipient(Utilisateur utilisateur) {
        if (!destinataire.contains(utilisateur)) {
            destinataire.add(utilisateur);
        }
    }
}
