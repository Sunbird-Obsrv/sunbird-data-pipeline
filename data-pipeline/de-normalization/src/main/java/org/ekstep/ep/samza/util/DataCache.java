package org.ekstep.ep.samza.util;

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

    public Map getData(String key) {

        try (Jedis jedis = redisConnect.getConnection()) {
            Gson gson = new Gson();
            Map<String, Object> parsedData = null;
            Map dataMap = new HashMap();
            jedis.select(redisDBIndex);
            String dataNode = jedis.get(key);
            if (dataNode == null) {
                return null;
            } else {
                Type type = new TypeToken<Map<String, Object>>() {
                }.getType();
                parsedData = gson.fromJson(dataNode, type);
                parsedData.keySet().retainAll(fieldsList);
                for (Map.Entry<String, Object> entry : parsedData.entrySet()) {
                    dataMap.put(entry.getKey().toLowerCase().replace("_", ""), entry.getValue());
                }
                return dataMap;
            }
        } catch (JedisException ex) {
            LOGGER.error("", "GetData: Unable to get a resource from the redis connection pool ", ex);
            return null;
        }

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
