package Model.Utilisateur;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import static java.lang.System.out;


public class RepertoireUtilisateur {

    private ArrayList<Utilisateur> listeUtilisateurs;

    public RepertoireUtilisateur() {
        listeUtilisateurs = new ArrayList<>();
        loadUsersFromFile();
    }

    public ArrayList<Utilisateur> getListeUtilisateurs() {
        return listeUtilisateurs;
    }

    public void setListeUtilisateurs(ArrayList<Utilisateur> listeUtilisateurs) {
        listeUtilisateurs = listeUtilisateurs;
    }

    public Utilisateur getUtilisateurParEmail(String email) {
        for (Utilisateur utilisateur : listeUtilisateurs) {
            if (utilisateur.getAdresseEmail().equals(email)) {
                return utilisateur;
            }
        }
        return null;
    }

    public Utilisateur getUtilisateurParNom(String nomUtilisateur) {
        for (Utilisateur utilisateur : listeUtilisateurs) {
            if (utilisateur.getAdresseEmail().equals(nomUtilisateur)) {
                return utilisateur;
            }
        }
        return null;
    }


    public void loadUsersFromFile() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("storage/users.pop"));

            String line = br.readLine();
            int i = 0;
            while((line != null) && (line.length() > 4)) {
                Utilisateur u = new Utilisateur(line);
                listeUtilisateurs.add(u);
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
        for (Utilisateur utilisateur: listeUtilisateurs
             ) {
            stringBuilder.append("Nom : ").append(utilisateur.getNom()).append(" Email: ").append(utilisateur.getAdresseEmail()).append("\n");
        }
        return stringBuilder.toString();
    }

    public void addUtilisateur(Utilisateur u) {
        getListeUtilisateurs().add(u);
    }
}
