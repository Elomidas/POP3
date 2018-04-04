package Commun.Utilisateur;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import static java.lang.System.out;

/**
 * Created by tardy on 22/03/2018.
 */
public class RepertoireUtilisateur {

    private ArrayList<Utilisateur> m_listeUtilisateurs;

    public RepertoireUtilisateur() {
        m_listeUtilisateurs = loadUsersFromFile();
    }

    public RepertoireUtilisateur(String domain) {
        m_listeUtilisateurs = loadUsersFromFile("." + domain);
    }

    public ArrayList<Utilisateur> getM_listeUtilisateurs() {
        return m_listeUtilisateurs;
    }

    public void setM_listeUtilisateurs(ArrayList<Utilisateur> m_listeUtilisateurs) {
        this.m_listeUtilisateurs = m_listeUtilisateurs;
    }

    public Utilisateur getUtilisateurParEmail(String email) {
        for(int i = 0; i < m_listeUtilisateurs.size(); i++) {
            Utilisateur utilisateur = m_listeUtilisateurs.get(i);
            if (utilisateur.getM_adresseEmail().equals(email)) {
                return utilisateur;
            }
        }
        return null;
    }

    public Utilisateur getUtilisateurParNom(String nomUtilisateur) {
        for(int i = 0; i < m_listeUtilisateurs.size(); i++) {
            Utilisateur utilisateur = m_listeUtilisateurs.get(i);
            if (utilisateur.getM_adresseEmail().equals(nomUtilisateur)) {
                return utilisateur;
            }
        }
        return null;
    }

    public ArrayList<Utilisateur> loadUsersFromFile() {
        return loadUsersFromFile("");
    }

    public ArrayList<Utilisateur> loadUsersFromFile(String domain) {
        ArrayList<Utilisateur> utilisateurs = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("data/users" + domain + ".pop"));

            String line = br.readLine();
            int i = 0;
            while((line != null) && (line.length() > 4)) {
                Utilisateur u = new Utilisateur(line);
                utilisateurs.add(u);
                line = br.readLine();
                i++;
            }
            out.println(i + " users added.");
            br.close();
        } catch(FileNotFoundException e) {
            out.println("Unable to open users.pop:" + e.getMessage());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return utilisateurs;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Utilisateur utilisateur: m_listeUtilisateurs
             ) {
            stringBuilder.append("Nom : ").append(utilisateur.getM_nom()).append(" Email: ").append(utilisateur.getM_adresseEmail()).append("\n");
        }
        return stringBuilder.toString();
    }

    public void addUtilisateur(Utilisateur u) {
        this.getM_listeUtilisateurs().add(u);
    }

    public int getIdMax(){
        return this.getM_listeUtilisateurs().size()-1;
    }
}
