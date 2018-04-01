package Model.MailBox;

import Model.Utilisateur.RepertoireUtilisateur;
import Model.Utilisateur.Utilisateur;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Mailbox {

    private RepertoireUtilisateur repertoireUtilisateur;
    private ArrayList<Email> listeEmails;
    private int id;

    public Mailbox() {
        this.repertoireUtilisateur = new RepertoireUtilisateur();
        this.listeEmails = new ArrayList<>();
        id = 1;
    }

    public RepertoireUtilisateur getRepertoireUtilisateur() {
        return repertoireUtilisateur;
    }

    public void setRepertoireUtilisateur(RepertoireUtilisateur repertoireUtilisateur) {
        this.repertoireUtilisateur = repertoireUtilisateur;
    }

    public ArrayList<Email> getListeEmails() {
        return listeEmails;
    }

    public void setListeEmails(ArrayList<Email> listeEmails) {
        this.listeEmails = listeEmails;
    }

    public void loadMails() {
        for (Utilisateur u: repertoireUtilisateur.getListeUtilisateurs()) {
            loadMails(u);
        }
    }

    public void loadMails(Utilisateur u) {
        if (u != null) {
            try {
                BufferedReader br = new BufferedReader(new FileReader("storage/" + u.getAdresseEmail() + ".pop"));
                int i = 0;
                while (readMail(br, u)) {
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
        List<Email> listEmails = new ArrayList<>();
        for (Email email: listeEmails) {
            if (email.getEmetteur().equals(utilisateur)) {
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


    private boolean readMail(BufferedReader br, Utilisateur u) {
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
                    Email m = new Email(utilisateurArrayList, sBuilder.toString(), repertoireUtilisateur.getListeUtilisateurs());
                    m.setId(id);
                    listeEmails.add(m);
                    return true;
                    //TODO a check condition ci dessous
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


    public void removeMails(Utilisateur u) {
        String temp;
        String idMail;
        if(u != null) {
            try {
                BufferedReader br = new BufferedReader(new FileReader("storage/" + u.getAdresseEmail() + ".pop"));
                BufferedWriter bw = new BufferedWriter(new FileWriter("storage/" + u.getAdresseEmail() + "temp.pop"));
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
                File oldFile =  new File("storage/" + u.getAdresseEmail() + ".pop");
                oldFile.delete();
                File newFile = new File("storage/" + u.getAdresseEmail() + "temp.pop");
                newFile.renameTo(new File("storage/" + u.getAdresseEmail() + ".pop"));
            } catch(FileNotFoundException e) {
                //Do nothing
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setEmailsUndeleted(Utilisateur utilisateur) {

        List<Email> listeEmailsDeUtilisateur = recupereEmails(utilisateur);
        for (Email email: listeEmailsDeUtilisateur
                ) {
            email.setEtat(true);
            listeEmails.set(listeEmails.indexOf(email), email);
        }
    }

    public Email getEmail(String emailId){
        for (Email email: getListeEmails()) {
            if (email.getId().equals(emailId)) {
                return email;
            }
        }
        return null;
    }

    public Email createEmail(Utilisateur emetteur){
        String i = String.valueOf(id);
        id++;
        return new Email(i, new ArrayList<>(),emetteur);
    }

}
