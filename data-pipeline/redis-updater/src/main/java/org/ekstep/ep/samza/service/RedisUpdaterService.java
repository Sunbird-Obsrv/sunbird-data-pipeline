package org.ekstep.ep.samza.service;

import com.google.gson.Gson;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.task.RedisUpdaterSink;
import org.ekstep.ep.samza.task.RedisUpdaterSource;

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
        this.contentStore = config.getInt("redis.database.contentStore.id", 2);
        this.dialCodeStore = config.getInt("redis.database.dialCodeStore.id", 3);
    }

    public void process(RedisUpdaterSource source, RedisUpdaterSink sink) {
        Map<String, Object> message = source.getMap();
        String nodeUniqueId = (String) message.get("nodeUniqueId");
        String objectType = (String) message.get("objectType");

        if (nodeUniqueId == null || objectType == null) return;

        LOGGER.info("", "processing event for nodeUniqueId: "+ nodeUniqueId);
        if(!nodeUniqueId.isEmpty() && objectType.equalsIgnoreCase("DialCode")) {
            updateDialCodeCache(message, sink);
        } else if (!nodeUniqueId.isEmpty()) {
            updateContentCache(message, sink);
        }
    }

    private void updateDialCodeCache(Map<String, Object> message, RedisUpdaterSink sink) {
        String nodeUniqueId = (String) message.get("nodeUniqueId");

        try (Jedis jedis = redisConnect.getConnection()) {
            jedis.select(dialCodeStore);
            Map<String, String> contentNode = jedis.hgetAll(nodeUniqueId);
            if (contentNode == null) {
                contentNode = new HashMap<>();
            }
            Map<String, String> newProperties = extractProperties(message);
            contentNode.forEach(newProperties::putIfAbsent);
            if (newProperties.size() > 0) {
                addToCache(nodeUniqueId, newProperties, dialCodeStore);
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
        try (Jedis jedis = redisConnect.getConnection()) {
            jedis.select(contentStore);
            Map<String, String> contentNode = jedis.hgetAll(nodeUniqueId);
            if (contentNode == null) {
                contentNode = new HashMap<>();
            }
            Map<String, String> newProperties = extractProperties(message);
            contentNode.forEach(newProperties::putIfAbsent);
            if (newProperties.size() > 0) {
                addToCache(nodeUniqueId, newProperties, contentStore);
                sink.success();
            }
        } catch(JedisException ex) {
            sink.error();
            LOGGER.error("", "Exception when redis connect" + ex);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> extractProperties(Map<String, Object> message) {
        Map<String, String> properties = new HashMap<>();
        Map transactionData = (Map) message.get("transactionData");
        if (transactionData != null) {
            Map<String, Object> addedProperties = (Map<String, Object>) transactionData.get("properties");
            if (addedProperties != null && !addedProperties.isEmpty()) {
                for (Map.Entry<String, Object> propertyMap : addedProperties.entrySet()) {
                    if (propertyMap != null && propertyMap.getKey() != null) {
                        String propertyName = propertyMap.getKey();
                        Object propertyNewValue = ((Map<String, Object>) propertyMap.getValue()).get("nv");
                        // New value from transaction data is null,
                        // then replace it with empty string
                        if (propertyNewValue == null)
                            properties.put(propertyName, "");
                        else{
                            Gson gson = new Gson();
                            properties.put(propertyName, gson.toJson(propertyNewValue));
                        }
                    }
                }
            }
        }
        return properties;
    }

    private void addToCache(String key, Map<String, String> value, Integer db) {
        if (key != null && !key.isEmpty() && value.size() > 0 && db != null) {
            try (Jedis jedis = redisConnect.getConnection()) {
                jedis.select(db);
                jedis.hmset(key, value);
                jedis.expire(key, config.getInt("location.db.redis.key.expiry.seconds", 86400));
            } catch(JedisException ex) {
                LOGGER.error("", "addToCache: Exception when redis connect" + ex);
            }
        }
    }
}
