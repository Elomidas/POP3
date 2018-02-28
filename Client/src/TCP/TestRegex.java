package TCP;

import java.util.regex.*;

public class TestRegex {
    public static boolean Match(String pattern, String target) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(target);
        return m.matches();
    }
}
