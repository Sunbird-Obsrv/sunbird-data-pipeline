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

    public RedisUpdaterService(Config config, RedisConnect redisConnect) {
        this.redisConnect = redisConnect;
        this.config = config;
    }

    @SuppressWarnings("unchecked")
    public void process(RedisUpdaterSource source, RedisUpdaterSink sink) {
        Map<String, Object> message = source.getMap();
        String nodeUniqueId = (String) message.get("nodeUniqueId");
        String objectType = (String) message.get("objectType");

        LOGGER.info("", "processing event for nodeUniqueId: "+ nodeUniqueId);
        if (nodeUniqueId != null && !nodeUniqueId.isEmpty() && objectType != null && objectType.equalsIgnoreCase("content")) {
            try (Jedis jedis = redisConnect.getConnection()) {
                Map<String, String> contentNode = jedis.hgetAll(nodeUniqueId);
                if (contentNode == null) {
                    contentNode = new HashMap<>();
                }
                Map transactionData = (Map) message.get("transactionData");
                if (transactionData != null) {
                    Map<String, Object> addedProperties = (Map<String, Object>) transactionData.get("properties");
                    if (addedProperties != null && !addedProperties.isEmpty()) {
                        for (Map.Entry<String, Object> propertyMap : addedProperties.entrySet()) {
                            if (propertyMap != null && propertyMap.getKey() != null) {
                                String propertyName = propertyMap.getKey();
                                Object propertyNewValue = ((Map<String, Object>) propertyMap.getValue()).get("nv");
                                // New value from transaction data is null, then remove
                                // the property from map
                                if (propertyNewValue == null)
                                    contentNode.remove(propertyName);
                                else{
                                    Gson gson = new Gson();
                                    contentNode.put(propertyName, gson.toJson(propertyNewValue));
                                }
                            }
                        }
                        addToCache(nodeUniqueId, contentNode);
                        sink.success();
                    }
                }
            } catch(JedisException ex) {
                sink.error();
                LOGGER.error("", "Exception when redis connect" + ex);
            }
        } else {
            sink.failed();
        }
    }

    private void addToCache(String key, Map<String, String> value) {
        if (key != null && !key.isEmpty() && value.size() > 0) {
            try (Jedis jedis = redisConnect.getConnection()) {
                jedis.select(config.getInt("redis.database.contentStore.id", 1));
                jedis.hmset(key, value);
                jedis.expire(key, config.getInt("location.db.redis.key.expiry.seconds", 86400));
            } catch(JedisException ex) {
                LOGGER.error("", "addToCache: Exception when redis connect" + ex);
            }
        }
    }
}
