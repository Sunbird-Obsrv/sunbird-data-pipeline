package org.ekstep.ep.samza.engine;

import org.ekstep.ep.samza.util.RedisConnect;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.util.Date;

public class DeDupEngine {


    private RedisConnect redisConnect;

    public DeDupEngine(RedisConnect redisConnect) {
        this.redisConnect = redisConnect;
    }

    public boolean isUniqueEvent(String checksum, int store) throws JedisException {
        try (Jedis jedis = redisConnect.getConnection()) {
            jedis.select(store);
            return !jedis.exists(checksum);
        }
    }

    public void storeChecksum(String checksum, int store, int expirySeconds) throws JedisException {
        try (Jedis jedis = redisConnect.getConnection()) {
            jedis.select(store);
            jedis.set(checksum, "");
            jedis.expire(checksum, expirySeconds);
        }
    }
}
