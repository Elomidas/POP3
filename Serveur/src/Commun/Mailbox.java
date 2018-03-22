package Commun;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by tardy on 22/03/2018.
 */
public class Mailbox {

    private RepertoireUtilisateur repertoireUtilisateur;
    private ArrayList<Email> m_listeEmails;

    public Mailbox() {
        this.repertoireUtilisateur = new RepertoireUtilisateur();
        this.m_listeEmails = new ArrayList<>();
        this.loadMails();
    }

    public RepertoireUtilisateur getRepertoireUtilisateur() {
        return this.repertoireUtilisateur;
    }

    public void setRepertoireUtilisateur(RepertoireUtilisateur repertoireUtilisateur) {
        this.repertoireUtilisateur = repertoireUtilisateur;
    }

    public ArrayList<Email> getM_listeEmails() {
        return m_listeEmails;
    }

    public void setM_listeEmails(ArrayList<Email> m_listeEmails) {
        this.m_listeEmails = m_listeEmails;
    }

    public void loadMails() {
        for (Utilisateur u: this.repertoireUtilisateur.getM_listeUtilisateurs()
                ) {

            if (u != null) {
                try {
                    BufferedReader br = new BufferedReader(new FileReader("data/" + u.getM_adresseEmail() + ".pop"));
                    int i = 0;
                    while (this.readMail(br, u)) {
                        i++;
                    }
                    System.out.println(i + " message(s) loaded.");
                    br.close();
                } catch (FileNotFoundException e) {
                    //Do nothing
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected boolean readMail(BufferedReader br, Utilisateur u) {
        ArrayList<Utilisateur> utilisateurArrayList = new ArrayList<>();
        utilisateurArrayList.add(u);
        try {
            StringBuilder sBuilder = new StringBuilder();
            String id = br.readLine();
            if(id == null) {
                return false;
            }
            while(true) {
                String line = br.readLine();
                if(line.equals(".")) {
                    sBuilder.append(".\n");
                    Email m = new Email(utilisateurArrayList, sBuilder.toString(), this.repertoireUtilisateur.getM_listeUtilisateurs());
                    m.setM_id(UUID.fromString(id));
                    m_listeEmails.add(m);
                    return true;
                } else if(line == null) {
                    return false;
                } else {
                    sBuilder.append(line)
                            .append("\n");
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
