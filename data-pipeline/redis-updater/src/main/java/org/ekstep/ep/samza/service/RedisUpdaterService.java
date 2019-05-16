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
    private Config config;
    private Integer contentStore;
    private Integer dialCodeStore;

    public RedisUpdaterService(Config config, RedisConnect redisConnect) {
        this.redisConnect = redisConnect;
        this.config = config;
        this.contentStore = config.getInt("redis.database.contentStore.id", 5);
        this.dialCodeStore = config.getInt("redis.database.dialCodeStore.id", 6);
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
        Map<String, Object> parsedData;
        Gson gson = new Gson();
        String key = nodeUniqueId;
        try (Jedis jedis = redisConnect.getConnection()) {
            jedis.select(dialCodeStore);
            String contentNode = jedis.get(key);
            if(contentNode !=null){
                Type type = new TypeToken<Map<String, Object>>(){}.getType();
                parsedData = gson.fromJson(contentNode, type);
            } else {
                parsedData = new HashMap<>();
            }
            Map<String, Object> newProperties = extractProperties(message);
            parsedData.putAll(newProperties);
            if (parsedData.size() > 0) {
                parsedData.values().removeAll(Collections.singleton(null));
                addToCache(key,  gson.toJson(parsedData), dialCodeStore);
                sink.success();
            }
        } catch(JedisException ex) {
            sink.error();
            LOGGER.error("", "Exception when redis connect" + ex);
        }
    }

    @SuppressWarnings("unchecked")
    private void updateContentCache(Map<String, Object> message, RedisUpdaterSink sink) {
        String nodeUniqueId = (String) message.get("nodeUniqueId");
        Map<String, Object> parsedData = null;
        Gson gson = new Gson();
        try (Jedis jedis = redisConnect.getConnection()) {
            jedis.select(contentStore);
            String contentNode = jedis.get(nodeUniqueId);
            if (contentNode == null) {
                parsedData = new HashMap<>();
            }else{
                Type type = new TypeToken<Map<String, Object>>(){}.getType();
                parsedData = gson.fromJson(contentNode, type);
            }
            Map<String, Object> newProperties = extractProperties(message);
            parsedData.putAll(newProperties);
            if (parsedData.size() > 0) {
                parsedData.values().removeAll(Collections.singleton(null));
                addToCache(nodeUniqueId, gson.toJson(parsedData), contentStore);
                sink.success();
            }
        } catch(JedisException ex) {
            sink.error();
            LOGGER.error("", "Exception when redis connect" + ex);
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

    private void addToCache(String key, String value, Integer db) {
        if (key != null && !key.isEmpty() && !value.isEmpty() && db != null) {
            try (Jedis jedis = redisConnect.getConnection()) {
                jedis.select(db);
                jedis.set(key, value);
            } catch(JedisException ex) {
                LOGGER.error("", "addToCache: Exception when redis connect" + ex);
            }
        }
    }
}
