/**
 * Created by tardy on 26/02/2018.
 */
public class Utilisateur {

    protected int m_id;
    protected String m_nom;
    protected String m_mdp;
    protected String m_adresseEmail;

    protected static final String _SEPARATOR = "#@@@#";

    public String getM_adresseEmail() {
        return m_adresseEmail;
    }

    public void setM_adresseEmail(String m_adresseEmail) {
        this.m_adresseEmail = m_adresseEmail;
    }

    public Utilisateur(String line) {
        String[] strs = line.split(_SEPARATOR);
        for(String s : strs) {
            System.out.println("> " + s);
        }
        m_id = Integer.parseInt(strs[0]);
        m_nom = decode(strs[1]);
        m_adresseEmail = decode(strs[2]);
        m_mdp = decode(strs[3]);
    }

    public Utilisateur(int m_id, String m_nom, String m_mdp, String m_adresseEmail) {

        this.m_id = m_id;
        this.m_nom = m_nom;
        this.m_mdp = m_mdp;
        this.m_adresseEmail = m_adresseEmail;
    }

    public Utilisateur(int m_id, String m_nom, String m_mdp) {
        this.m_id = m_id;
        this.m_nom = m_nom;
        this.m_mdp = m_mdp;
    }

    public String toLine() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(m_id)
                .append(encode(m_nom))
                .append(_SEPARATOR)
                .append(encode(m_adresseEmail))
                .append(_SEPARATOR)
                .append(encode(m_mdp));
        return sBuilder.toString();
    }

    protected String encode(String str) {
        return str.replace("\\", "\\\\");
    }

    protected String decode(String str) {
        return str.replace("\\\\", "\\");
    }

    public int getM_id() {
        return m_id;
    }

    public void setM_id(int m_id) {
        this.m_id = m_id;
    }

    public String getM_nom() {
        return m_nom;
    }

    public void setM_nom(String m_nom) {
        this.m_nom = m_nom;
    }

    public String getM_mdp() {
        return m_mdp;
    }

    public void setM_mdp(String m_mdp) {
        this.m_mdp = m_mdp;
    }

    public boolean checkPassword(String password) {
        return password.equals(m_mdp);
    }

}
