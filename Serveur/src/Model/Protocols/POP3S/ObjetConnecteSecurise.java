package Model.Protocols.POP3S;

import Model.Protocols.POP3.ObjetConnecte;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.HashMap;

import static java.lang.System.*;

public class ObjetConnecteSecurise extends ObjetConnecte {

    private Timestamp timeConnexion;
    private int processId;

    static {
        locked = new HashMap<>();
    }


    public ObjetConnecteSecurise(Socket socket) throws IOException {
        super(socket);
    }

    public void run() {
       etat = POP3_ETAT_AUTORISATION;
        String input;
        try {
            tcp.send(ObjetConnecteSecurise.POP3_REPONSE_POSITIVE + " Model.Protocols.POP3 server ready "  + generateTimbre());
        } catch (InvocationTargetException | NoSuchMethodException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        while (continuer) {
            try {
//                out.println("Wait...");
                input = tcp.receive();
//                out.println(input + " received");

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
                        case ObjetConnecte.POP3_ETAT_TRANSACTION:
                            response = transaction(command, parameters);
                            break;
                        default:
                            out.println("What is that (state/command) : " + etat + "/" + command);
                            response = ObjetConnecte.POP3_REPONSE_NEGATIVE;
                            break;
                    }
                }
                out.println("S: " + response);
                tcp.send(response);
            } catch (IOException e) {

                System.out.println(e.getMessage());
                continuer = false;
                quitTransaction();
                //TODO vérifier utilité du return ici
                return;
            }
        }
        tcp.Destroy();
        out.println("End of POP3S");
    }

    private int getProcessId() throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        java.lang.management.RuntimeMXBean runtime =
                java.lang.management.ManagementFactory.getRuntimeMXBean();
        java.lang.reflect.Field jvm = runtime.getClass().getDeclaredField("jvm");
        jvm.setAccessible(true);
        sun.management.VMManagement mgmt =
                (sun.management.VMManagement) jvm.get(runtime);
        java.lang.reflect.Method pid_method =
                mgmt.getClass().getDeclaredMethod("getProcessId");
        pid_method.setAccessible(true);

        processId = (Integer) pid_method.invoke(mgmt);
        return processId;
    }

    private Long getTimestamp(){
        timeConnexion = new Timestamp(System.currentTimeMillis());
        return  timeConnexion.getTime();

    }

    private String generateTimbre() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, NoSuchFieldException {
        return "<"+getProcessId()+"."+getTimestamp()+"@localhost>";
    }

    private boolean decrypteTimbre(String encryptUser) throws NoSuchAlgorithmException {
        StringBuilder decrypt = new StringBuilder();

        MessageDigest md = MessageDigest.getInstance("MD5");
        decrypt.append("<")
                .append(processId)
                .append(".")
                .append(timeConnexion.getTime())
                .append("@localhost>")
                .append(currentUser.getMdp());
        byte[] digestedBytes = md.digest(decrypt.toString().getBytes());
        StringBuilder returnBuilder = new StringBuilder();
        for (byte digestedByte : digestedBytes) {
            returnBuilder.append(String.format("%02X", digestedByte));
        }
        return returnBuilder.toString().equals(encryptUser);
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
                    if(decrypteTimbre(password)) {
                        if (isFree(currentUser.getAdresseEmail())) {
                            lock(currentUser.getAdresseEmail());
                            mailbox.loadMails(currentUser);
                            mailbox.setEmailsUndeleted(currentUser);
                            etat = POP3_ETAT_TRANSACTION;
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
