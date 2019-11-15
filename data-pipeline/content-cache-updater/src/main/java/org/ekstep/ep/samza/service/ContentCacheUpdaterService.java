package org.ekstep.ep.samza.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.task.*;
import org.ekstep.ep.samza.util.RedisConnect;
import redis.clients.jedis.Jedis;
import org.apache.samza.config.Config;
import redis.clients.jedis.exceptions.JedisException;

import java.lang.reflect.Type;
import java.util.*;

public class ContentCacheUpdaterService {

    private static Logger LOGGER = new Logger(ContentCacheUpdaterService.class);
    private RedisConnect redisConnect;
    private Jedis dialCodeStoreConnection;
    private Jedis contentStoreConnection;
    private JobMetrics metrics;
    private int dialCodeStoreDb;
    private int contentStoreDb;
    Jedis redisConnection;
    int storeId;
    private Gson gson = new Gson();
    Type mapType = new TypeToken<Map<String, Object>>() {
    }.getType();
    private List<String> contentModelListTypeFields;

    public ContentCacheUpdaterService(Config config, RedisConnect redisConnect, JobMetrics metrics) {
        this.redisConnect = redisConnect;
        this.metrics = metrics;
        this.contentStoreDb = config.getInt("redis.database.contentStore.id", 5);
        this.dialCodeStoreDb = config.getInt("redis.database.dialCodeStore.id", 6);
        this.contentStoreConnection = redisConnect.getConnection(contentStoreDb);
        this.dialCodeStoreConnection = redisConnect.getConnection(dialCodeStoreDb);
        this.contentModelListTypeFields = config.getList("contentModel.fields.listType", new ArrayList<>());
    }

    public void process(ContentCacheUpdaterSource source, ContentCacheUpdaterSink sink) {
        Map<String, Object> message = source.getMap();
        String nodeUniqueId = (String) message.get("nodeUniqueId");
        String objectType = (String) message.get("objectType");
        Map<String, Object> parsedData = null;
        if (nodeUniqueId == null || objectType == null) return;
        LOGGER.info("", "processing event for nodeUniqueId: " + nodeUniqueId);
        try {
            if (!nodeUniqueId.isEmpty() && objectType.equalsIgnoreCase("DialCode")) {
                parsedData = updateDialCodeCache(message, sink);
                redisConnection=dialCodeStoreConnection;
                storeId=dialCodeStoreDb;
            } else if (!nodeUniqueId.isEmpty()) {
                parsedData = updateContentCache(message, sink);
                redisConnection=contentStoreConnection;
                storeId=contentStoreDb;
            }
            if(null != parsedData) {
                redisConnect.addJsonToCache(nodeUniqueId, gson.toJson(parsedData), redisConnection);
                sink.success();
            }
        }catch (JedisException ex) {
            metrics.incCacheErrorCounter();
            LOGGER.error("", "Exception when adding to dialcode redis cache", ex);
            redisConnect.resetConnection();
            try (Jedis redisConn = redisConnect.getConnection(storeId)) {
                this.redisConnection = redisConn;
                if (null != parsedData)
                    redisConnect.addJsonToCache(nodeUniqueId, gson.toJson(parsedData), redisConnection);
            }
        }

    }

    public Map<String, Object> updateDialCodeCache(Map<String, Object> message, ContentCacheUpdaterSink sink) {
        String nodeUniqueId = (String) message.get("nodeUniqueId");
        Map<String, Object> parsedData = null;
            String contentNode = dialCodeStoreConnection.get(nodeUniqueId);
            if (contentNode != null) {
                parsedData = gson.fromJson(contentNode, mapType);
            } else {
                parsedData = new HashMap<>();
            }
            Map<String, Object> newProperties = extractProperties(message);
            parsedData.putAll(newProperties);
            if (parsedData.size() > 0) {
                parsedData.values().removeAll(Collections.singleton(null));
                parsedData.values().removeAll(Collections.singleton(""));
            }
        return parsedData;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> updateContentCache(Map<String, Object> message, ContentCacheUpdaterSink sink) {
        String nodeUniqueId = (String) message.get("nodeUniqueId");
        Map<String, Object> parsedData = null;
        Map<String, List<String>> listTypeFields = new HashMap();
            String contentNode = contentStoreConnection.get(nodeUniqueId);
            if (contentNode == null) {
                parsedData = new HashMap<>();
            } else {
                parsedData = gson.fromJson(contentNode, mapType);
            }
            Map<String, Object> newProperties = extractProperties(message);
            for (Map.Entry<String, Object> entry : newProperties.entrySet()) {
                if (contentModelListTypeFields.contains(entry.getKey())
                        && entry.getValue() instanceof String
                        && !((String) entry.getValue()).isEmpty()
                ) {
                    String str = (String) entry.getValue();
                    List<String> value = toList(str);
                    listTypeFields.put(entry.getKey(), value);
                    LOGGER.info("", "type cast to List for field: " + entry.getKey() + " ,nodeUniqueId: " + nodeUniqueId);
                }
            }
            if (!listTypeFields.isEmpty()) {
                newProperties.putAll(listTypeFields);
            }
            parsedData.putAll(newProperties);
            if (parsedData.size() > 0) {
                parsedData.values().removeAll(Collections.singleton(null));
                parsedData.values().removeAll(Collections.singleton(""));
                redisConnect.addJsonToCache(nodeUniqueId, gson.toJson(parsedData), contentStoreConnection);
                sink.success();
            }
        return parsedData;
    }

    private List<String> toList(String value) {
        List<String> val = new ArrayList<>();
        val.add(value);
        return val;
    }

    private Map<String, Object> extractProperties(Map<String, Object> message) {
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
}
