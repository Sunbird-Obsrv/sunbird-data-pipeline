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
import com.google.gson.*;

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
            System.out.println("fetching from LRU: " + key);
            LOGGER.warn("", "fetching from LRU: " + key);
            dataMap = parseData(dataNode);
            return dataMap;
        }
        return null;
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
