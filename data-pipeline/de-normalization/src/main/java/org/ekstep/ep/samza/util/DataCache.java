package org.ekstep.ep.samza.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.core.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;
import java.lang.reflect.Type;
import java.util.*;

public class DataCache {

    private static Logger LOGGER = new Logger(DataCache.class);

    protected RedisConnect redisConnect;
    protected int databaseIndex;
    protected Jedis redisConnection;
    protected JobMetrics metrics;
    protected List fieldsList;
    private Gson gson = new Gson();

    public DataCache(List fieldsList) {
        this.fieldsList = fieldsList;
    }

    public Map<String, Object> getData(String key) {
        Map<String, Object> cacheDataMap;
        try {
            cacheDataMap = getDataFromCache(key);
            metrics.incCacheHitCounter();
        } catch (JedisException ex) {
            LOGGER.error("", "Exception when retrieving data from redis cache ", ex);
            redisConnect.resetConnection();
            redisConnection = redisConnect.getConnection(databaseIndex);
            cacheDataMap = getDataFromCache(key);
        }
        return cacheDataMap;
    }

    private Map<String, Object> getDataFromCache(String key) {
        Map<String, Object> cacheData = new HashMap<>();
        String dataNode = redisConnection.get(key);
        if(dataNode != null && !dataNode.isEmpty()) {
            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> parsedData = gson.fromJson(dataNode, type);
            parsedData.keySet().retainAll(fieldsList);
            parsedData.values().removeAll(Collections.singleton(""));
            for (Map.Entry<String, Object> entry : parsedData.entrySet()) {
                cacheData.put(entry.getKey().toLowerCase().replace("_", ""), entry.getValue());
            }
        }
        return cacheData;
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
