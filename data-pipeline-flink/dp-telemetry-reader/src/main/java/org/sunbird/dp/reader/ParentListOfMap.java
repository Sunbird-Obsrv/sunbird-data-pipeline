package org.sunbird.dp.reader;

import java.util.List;
import java.util.Map;

class ParentListOfMap implements ParentType {
    
    List<Map<String, Object>> list;
    String childKey;

    ParentListOfMap(List<Map<String, Object>> list, String childKey) {
        this.list = list;
        this.childKey = childKey;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public <T> T readChild() {
        if (list == null) {
            return null;
        }
        for (Object itemsObject : list) {
            if (!(itemsObject instanceof Map)) {
                continue;
            }
            Map<String, Object> items = (Map<String, Object>) itemsObject;
            if (items.containsKey(childKey)) {
                Object o = items.get(childKey);
                if (o instanceof List && ((List) o).size() > 0) {
                    return (T) ((List) o).get(0);
                }
            }
        }
        return null;
    }

    @Override
    public void addChild(Object value) {
        throw new RuntimeException("Not Implemented");
    }
}
