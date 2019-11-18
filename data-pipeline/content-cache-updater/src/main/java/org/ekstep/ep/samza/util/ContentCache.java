package org.ekstep.ep.samza.util;

import java.util.*;

public class ContentCache {

    public Map<String, Object> getParsedData(Map<String, Object> parsedData, Map<String, Object> newProperties) {
        parsedData.putAll(newProperties);
        if (parsedData.size() > 0) {
            parsedData.values().removeAll(Collections.singleton(null));
            parsedData.values().removeAll(Collections.singleton(""));
        }
        return parsedData;
    }

    public Map<String, Object> extractProperties(Map<String, Object> message) {
        Map<String, Object> properties = new HashMap<>();
        Map transactionData = (Map) message.get("transactionData");
        if (transactionData != null) {
            Map<String, Object> addedProperties = (Map<String, Object>) transactionData.get("properties");
            if (addedProperties != null && !addedProperties.isEmpty()) {
                for (Map.Entry<String, Object> propertyMap : addedProperties.entrySet()) {
                    if (propertyMap != null && propertyMap.getKey() != null) {
                        String propertyName = propertyMap.getKey();
                        Object propertyNewValue = ((Map<String, Object>) propertyMap.getValue()).get("nv");
                        properties.put(propertyName, propertyNewValue);
                    }
                }
            }
        }
        return properties;
    }

    public Map<String, List<String>> convertType(Map<String, Object> newProperties, List<String> contentModelListTypeFields) {
        Map<String, List<String>> listTypeFields = new HashMap();
        for(String entry: contentModelListTypeFields){
            if(newProperties.containsKey(entry) && !((String) newProperties.get(entry)).isEmpty()
                    && newProperties.get(entry) instanceof String) {
                String str = (String) newProperties.get(entry);
                List<String> value = Arrays.asList(str);
                listTypeFields.put(entry, value);
            }
        }
        return listTypeFields;
    }
}
