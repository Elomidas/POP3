package Uilities;

import java.util.ArrayList;
import java.util.regex.*;

public class TestRegex {
    public static final String _MAIL = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    public static final String _IP = "(?:\\d+\\.)+\\d+";

    public static boolean Match(String pattern, String target) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(target);
        return m.matches();
    }

    public static String[] Submatches(String pattern, String target) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(target);
        ArrayList<String> strs = new ArrayList<>();
        while(m.find()) {
            strs.add(m.group());
        }
        return (String[])strs.toArray();
    }

    public static boolean CheckMail(String mail) {
        return Match(_MAIL, mail);
    }

    public static boolean CheckIP(String ip) {
        return Match(_IP, ip);
    }
}