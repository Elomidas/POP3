package Utilities;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DNS {
    private static List<ServerIntels> servers = Arrays.asList(
        new ServerIntels(
                "email.com",
                "127.0.0.1",
                1210,
                1211,
                1212),
        new ServerIntels(
                "email.fr",
                "127.0.0.1",
                1220,
                1221,
                1222)
    );

    /**
     * Find the first server matching the given domain name.
     * @param domain Domain name of the server to find.
     * @return Server matching the domain name.
     * @throws DNSException No server matching this domain name.
     */
    private static ServerIntels getServer(String domain) throws DNSException {
        Optional<ServerIntels> myserv = servers.stream()
                .filter(server -> server.getDomainName().equalsIgnoreCase(domain))
                .findFirst();
        if(!myserv.isPresent()) {
            throw new DNSException("Unable to find server with domain name \"" + domain + "\"");
        }
        return myserv.get();
    }

    /**
     * IP address of the server matching the given domain name.
     * @param domain Domain name of the server to find.
     * @return IP of the server with the given domain name.
     * @throws DNSException No server matching this domain name.
     */
    public static String getAddress(String domain) throws DNSException {
        return getServer(domain).getIP();
    }

    /**
     * POP3 port of the server matching the given domain name.
     * @param domain Domain name of the server to find.
     * @return POP3 port of the server with the given domain name.
     * @throws DNSException No server matching this domain name.
     */
    public static int getPOP3(String domain) throws DNSException {
        return getServer(domain).getPOP3();
    }

    /**
     * POP3S port of the server matching the given domain name.
     * @param domain Domain name of the server to find.
     * @return POP3S port of the server with the given domain name.
     * @throws DNSException No server matching this domain name.
     */
    public static int getPOP3S(String domain) throws DNSException {
        return getServer(domain).getPOP3S();
    }

    /**
     * SMTP port of the server matching the given domain name.
     * @param domain Domain name of the server to find.
     * @return SMTP port of the server with the given domain name.
     * @throws DNSException No server matching this domain name.
     */
    public static int getSMTP(String domain) throws DNSException {
        return getServer(domain).getSMTP();
    }
}
