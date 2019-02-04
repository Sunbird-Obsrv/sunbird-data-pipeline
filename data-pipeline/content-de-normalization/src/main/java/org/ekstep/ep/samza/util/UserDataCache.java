package org.ekstep.ep.samza.util;

import org.apache.samza.config.Config;
import org.ekstep.ep.samza.core.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDataCache {

    private static Logger LOGGER = new Logger(UserDataCache.class);

    private RedisConnect redisConnect;
    private Integer userDBIndex;

    public UserDataCache(Config config, RedisConnect redisConnect) {

        this.userDBIndex = config.getInt("redis.userDB.index", 1);
        this.redisConnect = redisConnect;
    }

    public Map getDataForUserId(String userId) {

        try (Jedis jedis = redisConnect.getConnection()) {
            jedis.select(userDBIndex);
            Map fields = jedis.hgetAll(userId);
            if (fields.isEmpty()) {
                return null;
            } else {
                return fields;
            }
        } catch (JedisException ex) {
            LOGGER.error("", "GetDataForUserId: Unable to get a resource from the redis connection pool ", ex);
            return null;
        }

    }
}
