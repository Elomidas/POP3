package Utilities;

public class ServerIntels {
    private String domainName;
    private String addressIP;
    private int portPOP3;
    private int portPOP3S;
    private int portSMTP;

    ServerIntels(String domain, String ip, int POP3, int POP3S, int SMTP) {
        addressIP = ip;
        portPOP3 = POP3;
        portPOP3S = POP3S;
        portSMTP = SMTP;
    }

    String getDomainName() {
        return domainName;
    }

    String getIP() {
        return addressIP;
    }

    int getPOP3() {
        return portPOP3;
    }

    int getPOP3S() {
        return portPOP3S;
    }

    int getSMTP() {
        return portSMTP;
    }
}
