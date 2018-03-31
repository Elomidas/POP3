package Commun.Mail;

import Commun.Utilisateur.RepertoireUtilisateur;
import Commun.Utilisateur.Utilisateur;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tardy on 22/03/2018.
 */
public class Mailbox {

    private RepertoireUtilisateur repertoireUtilisateur;
    private ArrayList<Email> m_listeEmails;
    private int id;

    public Mailbox() {
        this.repertoireUtilisateur = new RepertoireUtilisateur();
        this.m_listeEmails = new ArrayList<>();
        id = 1;
    }

    public Mailbox(int i) {
        this.repertoireUtilisateur = new RepertoireUtilisateur();
        this.m_listeEmails = new ArrayList<>();
        id = 1;
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
        for (Utilisateur u: this.repertoireUtilisateur.getM_listeUtilisateurs()) {
            loadMails(u);
        }
    }

    public void loadMails(Utilisateur u) {
        if (u != null) {
            try {
                BufferedReader br = new BufferedReader(new FileReader("data/" + u.getM_adresseEmail() + ".pop"));
                int i = 0;
                while (this.readMail(br, u)) {
                    id++;
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



    public List<Email> recupereEmails(Utilisateur utilisateur) {
        //To be tested
        List<Email> listEmails = new ArrayList<Email>();
        for (Email email: m_listeEmails) {
            if (email.getM_emetteur().equals(utilisateur)) {
                listEmails.add(email);
            }
            for (Utilisateur utilisateur1 : email.getM_destinataires()) {
                if (utilisateur1.equals(utilisateur) && !listEmails.contains(email)) {
                    listEmails.add(email);
                }
            }
        }
        return listEmails;
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
                    m.setM_id(id);
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


    public int removeMails(Utilisateur u) {
        String temp ="";
        String idMail ="";
        int i = 0;
        if(u != null) {
            try {
                BufferedReader br = new BufferedReader(new FileReader("data/" + u.getM_adresseEmail() + ".pop"));
                BufferedWriter bw = new BufferedWriter(new FileWriter("data/" + u.getM_adresseEmail() + "temp.pop"));
                idMail = br.readLine();
                Email email = getEmail(idMail);
                if (email != null) {
                    bw.write(email.encode());
                }

                while ((temp = br.readLine()) != null) {
                    if (temp.equals(".")) {
                        idMail = br.readLine();
                        if (idMail != null) {
                            email = getEmail(idMail);
                            if (email != null) {
                                System.out.println("Email est contenu dans la liste" + idMail);

                                bw.write(email.encode());
                            } else {
                                System.out.println("Email pas dans la liste" + idMail);
                            }
                        }
                    }
                }
                bw.close();
                br.close();
                File oldFile =  new File("data/" + u.getM_adresseEmail() + ".pop");
                oldFile.delete();
                File newFile = new File("data/" + u.getM_adresseEmail() + "temp.pop");
                newFile.renameTo(new File("data/" + u.getM_adresseEmail() + ".pop"));
            } catch(FileNotFoundException e) {
                //Do nothing
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return i;
    }

    public void setEmailsUndeleted(Utilisateur utilisateur) {

        List<Email> listeEmailsDeUtilisateur = recupereEmails(utilisateur);
        for (Email email: listeEmailsDeUtilisateur
                ) {
            email.setM_etat(true);
            m_listeEmails.set(m_listeEmails.indexOf(email), email);
        }
    }

    public Email getEmail(String emailId){
        for (Email email: this.getM_listeEmails()) {
            if (email.getM_id().equals(emailId)) {
                return email;
            }
        }
        return null;
    }

    public Email createEmail(ArrayList<Utilisateur> dest, Utilisateur emetteur){
        String i = String.valueOf(id);
        id++;
        return new Email(i,new ArrayList<Utilisateur>(),emetteur);
    }

}
