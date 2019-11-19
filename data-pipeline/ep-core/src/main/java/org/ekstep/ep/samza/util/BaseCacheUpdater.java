package org.ekstep.ep.samza.util;

import org.apache.samza.config.Config;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;
import org.ekstep.ep.samza.util.RedisConnect;

public class BaseCacheUpdater {

    private RedisConnect redisConnect;
    private Jedis connection;

    public BaseCacheUpdater(RedisConnect redisConnect) {
        this.redisConnect = redisConnect;
    }

    public void addToCache(String key, String value, int storeId) {
        try {
            connection = redisConnect.getConnection(storeId);
            if (key != null && !key.isEmpty() && null != value && !value.isEmpty()) {
                connection.set(key, value);
            }
        } catch(JedisException ex) {
            connection = redisConnect.resetConnection(storeId);
            if (null != value)
                connection.set(key, value);
        }
    }

    public String readFromCache(String key, int storeId) {
        try {
            connection = redisConnect.getConnection(storeId);
            return connection.get(key);
        }
        catch (JedisException ex) {
            connection = redisConnect.resetConnection(storeId);
            return connection.get(key);
        }
    }
}
