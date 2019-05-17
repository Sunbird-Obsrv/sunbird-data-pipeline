package org.ekstep.ep.samza.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.ekstep.ep.samza.core.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DataCache {

    private static Logger LOGGER = new Logger(DataCache.class);

    RedisConnect redisConnect;
    Integer redisDBIndex;
    List fieldsList;
    LRUCache lruCache;

    public Map getData(String key) {
        String dataNode = null;
        Cache<String, String> cache = lruCache.getConnection();
        // get from LRU cache if present;
        dataNode = cache.getIfPresent(key);
        Map dataMap = new HashMap();
        if (dataNode != null) {
            LOGGER.info("", "fetching from LRU: " + key);
            dataMap = parseData(dataNode);
            return dataMap;
        } else {
            LOGGER.info("", "fetching from Redis: " + key);
            try (Jedis jedis = redisConnect.getConnection()) {
                jedis.select(redisDBIndex);
                dataNode = jedis.get(key);
                if (dataNode == null) {
                    return null;
                } else {
                    // put to LRU cache
                    LOGGER.info("", "putting to LRU: " + key);
                    cache.put(key, dataNode);
                    dataMap = parseData(dataNode);
                    return dataMap;
                }
            } catch (JedisException ex) {
                LOGGER.error("", "GetData: Unable to get a resource from the redis connection pool ", ex);
                return null;
            }
        }
    }

    private Map<String, Object> parseData(String value) {
        Map dataMap = new HashMap();
        Gson gson = new Gson();
        Map<String, Object> data = null;
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        data = gson.fromJson(value, type);
        data.keySet().retainAll(fieldsList);
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            dataMap.put(entry.getKey().toLowerCase().replace("_", ""), entry.getValue());
        }
        return dataMap;
    }

    public List<Map> getData(List<String> keys) {
        List<Map> list = new ArrayList<>();
        for (String entry : keys) {
            Map data = getData(entry);
            if (data != null && !data.isEmpty()) {
                list.add(data);
            }
        }
        return list;
    }
}