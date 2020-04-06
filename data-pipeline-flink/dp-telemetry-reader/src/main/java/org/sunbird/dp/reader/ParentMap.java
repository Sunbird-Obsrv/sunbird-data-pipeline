package org.sunbird.dp.reader;

import java.util.Map;

class ParentMap implements ParentType {

	Map<String, Object> map;
    String childKey;

    ParentMap(Map<String, Object> map, String childKey) {
        this.map = map;
        this.childKey = childKey;
    }

    @SuppressWarnings("unchecked")
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
