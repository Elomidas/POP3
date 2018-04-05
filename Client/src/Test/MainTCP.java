package Test;

import Model.MyLogger.MyLogger;
import Model.Protocols.TCP.*;
import Utilities.TestRegex;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MainTCP {
    public static final int    _port = 0;
    public static final String _address = "0.0.0.0";
    public static void main(String[] args) {
        Logger logger = MyLogger.getLogger("./logs/MainTCP.log");

        if((_port == 0) || _address.equals("0.0.0.0")) {
            logger.info("Nothing to test right now, address and/or port are not set");
        } else {
            TCP myTCP = new TCP();
            try {
                myTCP.setServerPort(_port);
                myTCP.setServerAddress(_address);
            } catch(TCPException e) {
                logger.log(Level.SEVERE, "Error while testing Model.Protocols.TCP Client.", e);
            } finally {
                try {
                    myTCP.Close();
                } catch(TCPException e) {
                    logger.log(Level.SEVERE, "Unable to close Model.Protocols.TCP Client.", e);
                }
            }
        }
        logger.info("End of Model.Protocols.TCP Test execution");
    }
}
