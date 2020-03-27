package org.sunbird.dp.reader;

import org.sunbird.dp.util.Logger;

import java.util.Map;

// import org.ekstep.ep.samza.core.Logger;

class ParentMap implements ParentType {
    static Logger LOGGER = new Logger(ParentMap.class);
    Map<String, Object> map;
    String childKey;

    ParentMap(Map<String, Object> map, String childKey) {
        this.map = map;
        this.childKey = childKey;
    }

    @Override
    public <T> T readChild() {
        if (map != null && map.containsKey(childKey) && map.get(childKey) != null) {
            Object child = map.get(childKey);
            return (T) child;
        }
        return null;
    }

    @Override
    public void addChild(Object value) {
        if (map != null)
            map.put(childKey, value);
    }
}
