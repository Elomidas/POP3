package POP3;

import java.util.logging.Logger;
import TCP.*;

public class POP3 {
    protected Logger m_logs;
    protected TCP m_tcp;

    public POP3(Logger logs) {
        m_logs = logs;
        m_tcp = null;
    }
}
