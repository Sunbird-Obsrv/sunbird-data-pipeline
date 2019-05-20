package org.ekstep.ep.samza.engine;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.util.Date;

public class DeDupEngine {


    private Jedis jedis;

    public DeDupEngine(Jedis redisConnection, int store) {
        this.jedis = redisConnection;
        jedis.select(store);
    }

    public boolean isUniqueEvent(String checksum) throws JedisException {
        return jedis.get(checksum) == null;
    }

    public void storeChecksum(String checksum, int expirySeconds) throws JedisException {
        jedis.set(checksum, new Date().toString());
        jedis.expire(checksum, expirySeconds);
    }
}
