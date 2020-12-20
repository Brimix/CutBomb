package games.CutBomb;

import java.util.LinkedHashMap;
import java.util.Map;

public class Util {
    public Map<String, Object> makeMap(String key, Object value){
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(key, value);
        return map;
    }
}
