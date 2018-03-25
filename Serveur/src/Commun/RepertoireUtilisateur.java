package Commun;

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
        m_listeUtilisateurs = new ArrayList<Utilisateur>();
        loadUsersFromFile();
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


    public void loadUsersFromFile() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("data/users.pop"));

            String line = br.readLine();
            int i = 0;
            while((line != null) && (line.length() > 4)) {
                Utilisateur u = new Utilisateur(line);
                m_listeUtilisateurs.add(u);
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
}