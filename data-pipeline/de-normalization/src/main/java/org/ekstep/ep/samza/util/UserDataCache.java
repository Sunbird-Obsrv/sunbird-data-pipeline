package org.ekstep.ep.samza.util;

import com.google.gson.Gson;
import org.apache.samza.config.Config;
import org.ekstep.ep.samza.core.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

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
        defaultList.add("gradeList");
        defaultList.add("languageList");
        defaultList.add("subjectList");
        this.userDBIndex = config.getInt("redis.userDB.index", 1);
        this.redisConnect = redisConnect;
        this.fieldsList = defaultList;
    }

    public Map getDataForUserId(String userId) {

        try (Jedis jedis = redisConnect.getConnection()) {
            Gson gson = new Gson();
            Map userMap = new HashMap();
            jedis.select(userDBIndex);
            Map<String, String> fields = jedis.hgetAll(userId);
            if (fields.isEmpty()) {
                return null;
            } else {
                fields.keySet().retainAll(fieldsList);
                for (Map.Entry<String, String> entry : fields.entrySet())
                {
                    userMap.put(entry.getKey().toLowerCase(), gson.fromJson(entry.getValue(), Object.class));
                }
                return userMap;
            }
        } catch (JedisException ex) {
            LOGGER.error("", "GetDataForUserId: Unable to get a resource from the redis connection pool ", ex);
            return null;
        }

    }
}
