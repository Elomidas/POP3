package Model.Utilisateur;

public class Utilisateur {

    private int id;
    private String nom;
    private String mdp;
    private String adresseEmail;

    private static final String _SEPARATOR = "#@@@#";

    public String getAdresseEmail() {
        return adresseEmail;
    }

    public void setAdresseEmail(String adresseEmail) {
        this.adresseEmail = adresseEmail;
    }

    public Utilisateur(String line) {
        String[] strs = line.split(_SEPARATOR);
        for(String s : strs) {
            System.out.println("> " + s);
        }
        id = Integer.parseInt(strs[0]);
        nom = decode(strs[1]);
        adresseEmail = decode(strs[2]);
        mdp = decode(strs[3]);
    }

    public String toLine() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(id)
                .append(encode(nom))
                .append(_SEPARATOR)
                .append(encode(adresseEmail))
                .append(_SEPARATOR)
                .append(encode(mdp));
        return sBuilder.toString();
    }

    private String encode(String str) {
        return str.replace("\\", "\\\\");
    }

    private String decode(String str) {
        return str.replace("\\\\", "\\");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    public boolean checkPassword(String password) {
        return password.equals(mdp);
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "nom='" + nom + '\'' +
                ", adresseEmail='" + adresseEmail + '\'' +
                '}';
    }
}
