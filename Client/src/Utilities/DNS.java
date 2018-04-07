package Utilities;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DNS {
    private static List<ServerIntels> servers = getConfig();

    private static List<ServerIntels> getConfig() {
        String path = "config/DNS.csv";
        BufferedReader bufferedReader = null;
        String line;
        List<ServerIntels> list = new ArrayList<>();

        try {
            bufferedReader = new BufferedReader(new FileReader(path));
            //Do not read first line
            if(bufferedReader.readLine() != null) {
                while ((line = bufferedReader.readLine()) != null) {

                    // use comma as separator
                    String[] parameters = line.split(",");
                    if (parameters.length == 5) {
                        list.add(new ServerIntels(
                                parameters[0],
                                parameters[1],
                                Integer.parseInt(parameters[2]),
                                Integer.parseInt(parameters[3]),
                                Integer.parseInt(parameters[4])
                        ));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    /**
     * Find the first server matching the given domain name.
     * @param domain Domain name of the server to find.
     * @return Server matching the domain name.
     * @throws DNSException No server matching this domain name.
     */
    private static ServerIntels getServer(String domain) throws DNSException {
        System.out.println(domain);
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

    /**
     * Get the number of servers.
     * @return The number of servers.
     */
    public static int getServersNumber() {
        return servers.size();
    }

    public static List<String> getDomains() {
        return servers.stream().map(ServerIntels::getDomainName).collect(Collectors.toList());
    }
}
