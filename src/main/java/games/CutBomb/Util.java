package games.CutBomb;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class Util {
    public static Map<String, Object> makeMap(String key, Object value){
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(key, value);
        return map;
    }

    public static boolean isGuest(Authentication auth) {
        return (auth == null) || (auth instanceof AnonymousAuthenticationToken);
    }

    public static String parseDate(Date date){
        String s = date.toString();
        return s.substring(0, 19);
    }
}
