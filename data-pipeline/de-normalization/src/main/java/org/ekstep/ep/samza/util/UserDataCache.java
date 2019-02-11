package org.ekstep.ep.samza.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.samza.config.Config;
import org.ekstep.ep.samza.core.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDataCache {

    private static Logger LOGGER = new Logger(UserDataCache.class);

    private RedisConnect redisConnect;
    private Integer userDBIndex;
    private List fieldsList;

    public UserDataCache(Config config, RedisConnect redisConnect) {

        List defaultList = new ArrayList<String>();
        defaultList.add("type");
        defaultList.add("grade");
        defaultList.add("language");
        defaultList.add("subject");
        this.userDBIndex = config.getInt("redis.userDB.index", 1);
        this.redisConnect = redisConnect;
        this.fieldsList = defaultList;
    }

    public Map getDataForUserId(String userId) {

        try (Jedis jedis = redisConnect.getConnection()) {
            Gson gson = new Gson();
            Map userMap = new HashMap();
            jedis.select(userDBIndex);
            Map<String, Object> parsedData = null;
            String fields = jedis.get(userId);
            if (fields == null) {
                return null;
            } else {
                Type type = new TypeToken<Map<String, Object>>() {
                }.getType();
                parsedData = gson.fromJson(fields, type);
                parsedData.keySet().retainAll(fieldsList);
                parsedData.keySet().retainAll(fieldsList);
                for (Map.Entry<String, Object> entry : parsedData.entrySet()) {
                    userMap.put(entry.getKey().toLowerCase(), entry.getValue());
                }
                return userMap;
            }
        } catch (JedisException ex) {
            LOGGER.error("", "GetDataForUserId: Unable to get a resource from the redis connection pool ", ex);
            return null;
        }

    }
}
