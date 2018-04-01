package Model.Protocols.POP3S;

//import Commun.TcpPOP3S;

import Model.Protocols.POP3.ObjetConnecte;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.HashMap;

import static java.lang.System.*;

/**
 * Created by tardy on 12/03/2018.
 */
public class ObjetConnecteSecurise extends ObjetConnecte {

    private Timestamp timeConnexion;
    private int processId;

    static {
        m_locked = new HashMap<>();
    }


    public ObjetConnecteSecurise(Socket socket) throws IOException {
        super(socket);
    }

    public void run() {
        this.initialize();

       this.m_etat = POP3_ETAT_AUTORISATION;
        String input;
        try {
            m_tcp.send(ObjetConnecteSecurise.POP3_REPONSE_POSITIVE + " POP3 server ready "  + generateTimbre());
        } catch (InvocationTargetException | NoSuchMethodException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        while (m_continuer) {
            try {
//                out.println("Wait...");
                input = m_tcp.receive();
//                out.println(input + " received");

                String[] explodedCommand = input.split(" ", 2);
                String command = explodedCommand[0].toUpperCase();
                String[] parameters = new String[0];
                if(explodedCommand.length > 1) {
                    parameters = explodedCommand[1].split(" ");
                }
                String response;
                if(command.equals("")) {
                    m_blankCount++;
                } else {
                    m_blankCount = 0;
                }
                if(m_blankCount == 9) {
                    response = ObjetConnecte.POP3_REPONSE_NEGATIVE + " one more blank command and you will be disconnected.";
                } else if(m_blankCount >= 10) {
                    if(m_lock) {
                        this.unlock(m_current.getM_adresseEmail());
                    }
                    response = ObjetConnecte.POP3_REPONSE_NEGATIVE + " you've been deconnected by server.";
                    m_continuer = false;
                } else {
                    switch (m_etat) {
                        case ObjetConnecte.POP3_ETAT_AUTORISATION:
                            response = this.AuthorisationState(command, parameters);
                            break;
                        case ObjetConnecte.POP3_ETAT_TRANSACTION:
                            response = this.TransactionState(command, parameters);
                            break;
                        default:
                            out.println("What is that (state/command) : " + m_etat + "/" + command);
                            response = ObjetConnecte.POP3_REPONSE_NEGATIVE;
                            break;
                    }
                }
                out.println("S POP3S: " + response);
                m_tcp.send(response);
            } catch (IOException e) {

                System.out.println(e.getMessage());
                m_continuer = false;
                this.quitTransaction();
                return;
            }
        }
        this.m_tcp.Destroy();
        out.println("End of POP3S");
    }

    public int getProcessId() throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        java.lang.management.RuntimeMXBean runtime =
                java.lang.management.ManagementFactory.getRuntimeMXBean();
        java.lang.reflect.Field jvm = runtime.getClass().getDeclaredField("jvm");
        jvm.setAccessible(true);
        sun.management.VMManagement mgmt =
                (sun.management.VMManagement) jvm.get(runtime);
        java.lang.reflect.Method pid_method =
                mgmt.getClass().getDeclaredMethod("getProcessId");
        pid_method.setAccessible(true);

        this.processId = (Integer) pid_method.invoke(mgmt);
        return processId;
    }

    public Long getTimestamp(){
        this.timeConnexion = new Timestamp(System.currentTimeMillis());
        return  timeConnexion.getTime();

    }

    public String generateTimbre() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, NoSuchFieldException {
        return "<"+getProcessId()+"."+getTimestamp()+"@localhost>";
    }

    public boolean decrypteTimbre(String encryptUser) throws NoSuchAlgorithmException {
        StringBuilder decrypt = new StringBuilder();

        MessageDigest md = MessageDigest.getInstance("MD5");
        decrypt.append("<")
                .append(this.processId)
                .append(".")
                .append(this.timeConnexion.getTime())
                .append("@localhost>")
                .append(this.m_current.getM_mdp());
        byte[] digestedBytes = md.digest(decrypt.toString().getBytes());
        StringBuilder returnBuilder = new StringBuilder();
        for(int i = 0; i < digestedBytes.length; i++) {
            returnBuilder.append(String.format("%02X", digestedBytes[i]));
        }
        if(returnBuilder.toString().equals(encryptUser))
            return true;
        return false;
    }

    /*  ###
     *  # Automates
     *  ###
     */

    protected String AuthorisationState(String command, String[] parameters) {
        if (command.equals("APOP")) {
            if(parameters.length <= 1) {
                return ObjetConnecte.POP3_REPONSE_NEGATIVE + " parameter missing.";
            }
            String username = parameters[0];
            String password = parameters[1];

            if(checkUser(username)) {
                try {
                    if(this.decrypteTimbre(password)) {
                        if (this.isFree(m_current.getM_adresseEmail())) {
                            this.lock(m_current.getM_adresseEmail());
                            this.m_mailbox.loadMails(m_current);
                            this.m_mailbox.setEmailsUndeleted(m_current);
                            m_etat = POP3_ETAT_TRANSACTION;
                            return ObjetConnecte.POP3_REPONSE_POSITIVE;
                        } else {
                            return ObjetConnecte.POP3_REPONSE_NEGATIVE + " unable to lock/open your repository";
                        }
                    } else {
                        return ObjetConnecte.POP3_REPONSE_NEGATIVE + " password is not valid";
                    }
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

            } else {
                return ObjetConnecte.POP3_REPONSE_NEGATIVE + " username is not valid";
            }
        }
        return ObjetConnecte.POP3_REPONSE_NEGATIVE + " command \"" + command + "\" doesn't seem valid";
    }
}
