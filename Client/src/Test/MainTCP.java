package Test;

import MyLogger.MyLogger;
import TCP.*;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MainTCP {
    public static void main(String[] args) {
        Logger logger = MyLogger.getLogger("./logs/MainTCP.log");

        TCP myTCP = new TCP(logger);
        try {
            myTCP.Run();
        } catch(TCPException e) {
            logger.log(Level.SEVERE, "Error in TCP Test execution", e);
        }
        logger.info("End of TCP Test execution");
    }
}
