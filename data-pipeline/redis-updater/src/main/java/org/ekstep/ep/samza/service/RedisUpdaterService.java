package org.ekstep.ep.samza.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.task.RedisUpdaterSink;
import org.ekstep.ep.samza.task.RedisUpdaterSource;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.ekstep.ep.samza.util.RedisConnect;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;
import org.apache.samza.config.Config;

public class RedisUpdaterService {

    static Logger LOGGER = new Logger(RedisUpdaterService.class);
    private RedisConnect redisConnect;
    private Jedis dialCodeStoreConnection;
    private Jedis contentStoreConnection;
    private int dialCodeStoreDb;
    private int contentStoreDb;
    private Gson gson = new Gson();

    public RedisUpdaterService(Config config, RedisConnect redisConnect) {
        this.redisConnect = redisConnect;
        this.contentStoreDb = config.getInt("redis.database.contentStore.id", 5);
        this.dialCodeStoreDb = config.getInt("redis.database.dialCodeStore.id", 6);
        this.contentStoreConnection = redisConnect.getConnection(contentStoreDb);
        this.dialCodeStoreConnection = redisConnect.getConnection(dialCodeStoreDb);
    }

    public void process(RedisUpdaterSource source, RedisUpdaterSink sink) {
        Map<String, Object> message = source.getMap();
        sink.setMetricsOffset(source.getSystemStreamPartition(), source.getOffset());
        String nodeUniqueId = (String) message.get("nodeUniqueId");
        String objectType = (String) message.get("objectType");
        if (nodeUniqueId == null || objectType == null) return;
        LOGGER.info("", "processing event for nodeUniqueId: " + nodeUniqueId);
        if (!nodeUniqueId.isEmpty() && objectType.equalsIgnoreCase("DialCode")) {
            updateDialCodeCache(message, sink);
        } else if (!nodeUniqueId.isEmpty()) {
            updateContentCache(message, sink);
        }
    }

    private void updateDialCodeCache(Map<String, Object> message, RedisUpdaterSink sink) {
        String nodeUniqueId = (String) message.get("nodeUniqueId");
        Map<String, Object> parsedData = null;
        try {
            String contentNode = dialCodeStoreConnection.get(nodeUniqueId);
            if (contentNode != null) {
                Type type = new TypeToken<Map<String, Object>>() {}.getType();
                parsedData = gson.fromJson(contentNode, type);
            } else {
                parsedData = new HashMap<>();
            }
            Map<String, Object> newProperties = extractProperties(message);
            parsedData.putAll(newProperties);
            if (parsedData.size() > 0) {
                parsedData.values().removeAll(Collections.singleton(null));
                addToCache(nodeUniqueId, gson.toJson(parsedData), dialCodeStoreConnection);
                sink.success();
            }
        } catch (JedisException ex) {
            sink.error();
            LOGGER.error("", "Exception when adding to dialcode redis cache", ex);
            redisConnect.resetConnection();
            redisConnect.getConnection(dialCodeStoreDb);
            addToCache(nodeUniqueId, gson.toJson(parsedData), dialCodeStoreConnection);
        }
    }

    @SuppressWarnings("unchecked")
    private void updateContentCache(Map<String, Object> message, RedisUpdaterSink sink) {
        String nodeUniqueId = (String) message.get("nodeUniqueId");
        Map<String, Object> parsedData = null;
        try {
            String contentNode = contentStoreConnection.get(nodeUniqueId);
            if (contentNode == null) {
                parsedData = new HashMap<>();
            } else {
                Type type = new TypeToken<Map<String, Object>>() {}.getType();
                parsedData = gson.fromJson(contentNode, type);
            }
            Map<String, Object> newProperties = extractProperties(message);
            parsedData.putAll(newProperties);
            if (parsedData.size() > 0) {
                parsedData.values().removeAll(Collections.singleton(null));
                addToCache(nodeUniqueId, gson.toJson(parsedData), contentStoreConnection);
                sink.success();
            }
        } catch (JedisException ex) {
            sink.error();
            LOGGER.error("", "Exception when adding to content store redis cache", ex);
            redisConnect.resetConnection();
            redisConnect.getConnection(contentStoreDb);
            addToCache(nodeUniqueId, gson.toJson(parsedData), contentStoreConnection);
        }
    }

    @SuppressWarnings("unchecked")
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

    private void addToCache(String key, String value, Jedis redisConnection) {
        if (key != null && !key.isEmpty() && !value.isEmpty()) {
            redisConnection.set(key, value);
        }
    }
}
