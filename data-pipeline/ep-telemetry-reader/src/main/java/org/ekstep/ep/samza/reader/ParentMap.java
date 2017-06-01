package org.ekstep.ep.samza.reader;

import java.util.Map;

class ParentMap {
    Map<String, Object> map;
    String childKey;

    ParentMap(Map<String, Object> map, String childKey) {
        this.map = map;
        this.childKey = childKey;
    }

    <T> T readChild() {
        if (map != null && map.containsKey(childKey) && map.get(childKey) != null)
            return (T) map.get(childKey);
        return null;
    }

    void addChild(Object value) {
        if (map != null)
            map.put(childKey, value);
    }
}
