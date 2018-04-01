package Model.Protocols.POP3;

import Model.MailBox.Email;
import Model.Protocols.ObjetConnecteUnderTCP;
import Model.Utilisateur.Utilisateur;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class ObjetConnecte extends ObjetConnecteUnderTCP {
    protected static final String POP3_ETAT_AUTORISATION = "Autorisation";
    private static final String POP3_ETAT_AUTHENTIFICATION = "Authentification";
    protected static final String POP3_ETAT_TRANSACTION = "Transaction";
    protected static final String POP3_REPONSE_NEGATIVE = "-ERR";
    protected static final String POP3_REPONSE_POSITIVE = "+OK";
    protected static HashMap<String, Boolean> locked;

    static {
        locked = new HashMap<>();
    }

    protected String etat;
    protected Utilisateur currentUser;
    protected boolean lock;
    protected int blankCount;

    public ObjetConnecte(Socket socket) throws IOException {
        super(socket);

        this.currentUser = null;
        this.lock = false;
        this.blankCount = 0;

        super.mailbox.getRepertoireUtilisateur().loadUsersFromFile();
    }

    public void run() {
        etat = POP3_ETAT_AUTORISATION;
        String input;
        while (continuer) {
            try {
                input = tcp.receive();

                String[] explodedCommand = input.split(" ", 2);
                String command = explodedCommand[0].toUpperCase();
                String[] parameters = new String[0];
                if(explodedCommand.length > 1) {
                    parameters = explodedCommand[1].split(" ");
                }
                String response;
                if(command.equals("")) {
                    blankCount++;
                } else {
                    blankCount = 0;
                }
                if(blankCount == 9) {
                    response = ObjetConnecte.POP3_REPONSE_NEGATIVE + " one more blank command and you will be disconnected.";
                } else if(blankCount >= 10) {
                    if(lock) {
                        unlock(currentUser.getAdresseEmail());
                    }
                    response = ObjetConnecte.POP3_REPONSE_NEGATIVE + " you've been deconnected by server.";
                    continuer = false;
                } else {
                    switch (etat) {
                        case ObjetConnecte.POP3_ETAT_AUTORISATION:
                            response = AuthorisationState(command, parameters);
                            break;
                        case ObjetConnecte.POP3_ETAT_AUTHENTIFICATION:
                            response = AuthenticationState(command, parameters);
                            break;
                        case ObjetConnecte.POP3_ETAT_TRANSACTION:
                            response = transaction(command, parameters);
                            break;
                        default:
                            System.out.println("What is that (state/command) : " + etat + "/" + command);
                            response = ObjetConnecte.POP3_REPONSE_NEGATIVE;
                            break;
                    }
                }
                System.out.println("S: " + response);
                tcp.send(response);
            } catch (IOException e) {
                e.printStackTrace();
                continuer = false;
                return;
            }
        }
        tcp.Destroy();
        System.out.println("End of POP3");
    }

    /*  ###
     *  # Automates
     *  ###
     */

    protected String AuthorisationState(String command, String[] parameters) {
        if (command.equals("USER")) {
            if(parameters.length < 1) {
                return ObjetConnecte.POP3_REPONSE_NEGATIVE + " parameter missing.";
            }
            String username = parameters[0];
            System.out.println("username : " + username);
            if(checkUser(username)) {
                etat = POP3_ETAT_AUTHENTIFICATION;
                return ObjetConnecte.POP3_REPONSE_POSITIVE;
            } else {
                return ObjetConnecte.POP3_REPONSE_NEGATIVE + " username is not valid";
            }
        }
        return ObjetConnecte.POP3_REPONSE_NEGATIVE + " command \"" + command + "\" doesn't seem valid";
    }

    private String AuthenticationState(String command, String[] parameters) {
        if(command.equals("PASS")) {
            if(parameters.length < 1) {
                return ObjetConnecte.POP3_REPONSE_NEGATIVE + " parameter missing.";
            }
            String password = parameters[0];

            if(checkPass(password)) {
                if(isFree(currentUser.getAdresseEmail())) {
                    lock(currentUser.getAdresseEmail());
                    mailbox.loadMails(currentUser);
                    mailbox.setEmailsUndeleted(currentUser);
                    etat = POP3_ETAT_TRANSACTION;
                    return ObjetConnecte.POP3_REPONSE_POSITIVE + " Model.Protocols.POP3 server ready";
                } else {
                    return ObjetConnecte.POP3_REPONSE_NEGATIVE + " unable to lock/open your repository";
                }
            }
        } else if(command.equals("QUIT")) {
            return commandeQuit();
        }
        return ObjetConnecte.POP3_REPONSE_NEGATIVE + " command \"" + command + "\" doesn't seem valid";
    }

    @Override
    protected String transaction(String command, String[] parameters) {
        switch (command) {
            case "QUIT":
                //TODO verifier cette modif
                /*
                quitTransaction();
                return quit();
                */
                return quitTransaction();
            case "RETR":
                if (parameters.length < 1) {
                    return ObjetConnecte.POP3_REPONSE_NEGATIVE + " parameter missing.";
                }
                return retr(parameters[0]);
            case "NOOP":
                return noop();
            case "RSET":
                //TODO To be tested
                return commandeRset();
            case "DELE":
                if (parameters.length < 1) {
                    return ObjetConnecte.POP3_REPONSE_NEGATIVE + " parameter missing.";
                }
                return dele(Integer.parseInt(parameters[0]));
            case "LIST":
                return list();
            case "UIDL":
                return uidl();
            case "STAT":
                return stat();
            default:
                //fermetureAutreQueQuit();
                return ObjetConnecte.POP3_REPONSE_NEGATIVE + " unknown command '" + command + "'.";
        }
    }

    /*  ###
     *  # Model.Protocols.POP3 Commands
     *  ###
     */

    private String list() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(ObjetConnecte.POP3_REPONSE_POSITIVE)
                .append(" ")
                .append(mailbox.getListeEmails().size())
                .append(" message(s) :\n");
        for(int i = 0; i < mailbox.getListeEmails().size(); i++) {
            sBuilder.append(i+1)
                    .append(" ")
                    .append(mailbox.getListeEmails().get(i).Size())
                    .append("\n");
        }
        return sBuilder.toString();
    }

    private String uidl() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(ObjetConnecte.POP3_REPONSE_POSITIVE)
                .append(" ")
                .append(mailbox.getListeEmails().size())
                .append(" message(s) :\n");
        for(int i = 0; i < mailbox.getListeEmails().size(); i++) {
            sBuilder.append(i+1)
                    .append(" ")
                    .append(mailbox.getListeEmails().get(i).getId())
                    .append("\n");
        }
        return sBuilder.toString();
    }

    @Override
    protected String commandeQuit() {
        continuer = false;
        if(lock) {
            unlock(currentUser.getAdresseEmail());
        }
        return POP3_REPONSE_POSITIVE;
    }

    protected String quitTransaction() {
        //TODO vérifier que c'est ok
        /*
        m_continuer = false;
        if(lock) {
            this.unlock(currentUser.getAdresseEmail());
        }
        */
        //TODO verifier que l'on peut enlever la première liste
        List<Email> listEmailsToRemove = new ArrayList<>();
        List<Email> listEmailsOfUser = mailbox.recupereEmails(currentUser);
        for (Email email: listEmailsOfUser) {
            if (!email.getEtat()) {
                mailbox.getListeEmails().remove(email);
                listEmailsToRemove.add(email);
            }
        }

        mailbox.removeMails(currentUser);
        //return POP3_REPONSE_POSITIVE;
        return commandeQuit();
    }

    private String retr(String id) {
        Email m = mailbox.getEmail(id);
        if(m == null) {
            return POP3_REPONSE_NEGATIVE + " unable to find this message.";
        }
        return POP3_REPONSE_POSITIVE + " \n" + m.encode();
    }

    private String noop() {
        // Nothing
        return POP3_REPONSE_POSITIVE;
    }

    @Override
    protected String commandeRset() {
        int nombreEmailsReset = 0;
        int nombreOctets = 0;
        //drop deleted tag on all message tagged as deleted

        for (Email email: mailbox.getListeEmails()) {

            if (!email.getEtat()){
                email.setEtat(true);
                nombreEmailsReset++;
                nombreOctets += email.Size();
            }


        }
        //return positive answwer + number of message in maildrop + number of octets

        return POP3_REPONSE_POSITIVE + " " + nombreEmailsReset + " " + nombreOctets;
    }

    private String dele(int idMessage) {
        //tag message as deleted
        StringBuilder stringBuilder = new StringBuilder();
        Email email = mailbox.getEmail(stringBuilder.append(idMessage).toString());
        int index = mailbox.getListeEmails().indexOf(email);
        if (email.getEtat()) {
            email.setEtat(false);
            mailbox.getListeEmails().set(index, email);
            //return positive answer or negative if message already tagged as deleted + "message" idMessage + "deleted"
            return POP3_REPONSE_POSITIVE + " message " + idMessage + "deleted";
        } else {
            return POP3_REPONSE_NEGATIVE + " message " + idMessage + "already deleted";
        }
    }

    private String stat() {
        int size = 0;
        int number = 0;
        for(Email m : mailbox.getListeEmails()) {
            size += m.Size();
            number++;
        }
        return POP3_REPONSE_POSITIVE + " " + number + " " + size;
    }

    /*  ###
     *  # Check functions
     *  ###
     */

    protected boolean checkUser(String username) {
        Utilisateur u = mailbox.getRepertoireUtilisateur().getUtilisateurParNom(username);
        if(u == null) {
            u = mailbox.getRepertoireUtilisateur().getUtilisateurParEmail(username);
        }
        if (u != null) {
            currentUser = u;
            return true;
        }
        return false;
    }

    private boolean checkPass(String password) {
        return currentUser.checkPassword(password);
    }

    /*  Check if a mail repository is free or locked
     */
    protected boolean isFree(String mail) {
        return !locked.containsKey(mail) || (!locked.get(mail));
    }

    /*  Lock a mailbox
     */
    protected void lock(String mail) {
        if(isFree(mail)) {
            locked.put(mail, true);
            lock = true;
        }
    }

    protected void unlock(String mail) {
        locked.put(mail, false);
        lock = false;
    }


}
