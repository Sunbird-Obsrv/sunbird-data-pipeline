package org.ekstep.ep.samza.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import kong.unirest.UnirestException;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.task.ContentCacheConfig;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ContentData {
    static Logger LOGGER = new Logger(ContentData.class);

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
                    if (propertyMap != null && propertyMap.getKey() != null && null!=((Map<String, Object>) propertyMap.getValue()).get("nv") && !((Map<String, Object>) propertyMap.getValue()).get("nv").toString().isEmpty()) {
                        String propertyName = propertyMap.getKey();
                        Object propertyNewValue = ((Map<String, Object>) propertyMap.getValue()).get("nv");
                        properties.put(propertyName, propertyNewValue);
                    }
                }
            }
        }
        return properties;
    }

    public Map<String, Object> convertType(Map<String, Object> newProperties, List<String> contentModelListTypeFields, List<String> dateFields) {
        Map<String, Object> result = new HashMap();
        for(String entry: contentModelListTypeFields){
            if(newProperties.containsKey(entry)
                    && newProperties.get(entry) instanceof String) {
                String str = (String) newProperties.get(entry);
                if(dateFields.contains(entry)) {
                    try{
                        Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(str);
                        result.put(entry,date.getTime());
                    }catch (ParseException ex){ ex.printStackTrace();}
                }
                else {
                    List<String> value = Arrays.asList(str);
                    result.put(entry, value);
                }
            }
        }
        return result;
    }

    public Object getMetadata(String apiUrl, RestUtil restUtil, String authKey, String fieldName) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + authKey);
        try {
            String responseBody = restUtil.get(apiUrl, headers);
            Gson gson = new Gson();
            Map<String, Object> response = gson.fromJson(
                    responseBody, new TypeToken<HashMap<String, Object>>() {
                    }.getType()
            );
            return gson.fromJson(gson.toJson(response.get("result")), Map.class).get(fieldName);
        } catch (NullPointerException e) {
            LOGGER.error(null, "Exception while fetching metadata" + e);
            e.printStackTrace();
            return null;
        }
    }
}
