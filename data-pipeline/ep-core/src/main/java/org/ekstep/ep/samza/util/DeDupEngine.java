package org.ekstep.ep.samza.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

public class DeDupEngine {

    private Jedis redisConnection;
    private int expirySeconds;

    public DeDupEngine(Jedis redisConnection, int store, int expirySeconds) {
        this.redisConnection = redisConnection;
        this.redisConnection.select(store);
        this.expirySeconds = expirySeconds;
    }

    public boolean isUniqueEvent(String checksum) throws JedisException {
        return !redisConnection.exists(checksum);
    }

    public void storeChecksum(String checksum) throws JedisException {
        redisConnection.set(checksum, "");
        redisConnection.expire(checksum, expirySeconds);
    }
}
