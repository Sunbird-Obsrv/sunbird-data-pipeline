package org.ekstep.ep.samza.util;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

public class DeDupEngine {

    private RedisConnect redisConnect;
    private Jedis redisConnection;
    private int store;
    private int expirySeconds;

    public DeDupEngine(RedisConnect redisConnect, int store, int expirySeconds) {
      
        this.redisConnect = redisConnect;
        this.redisConnection = redisConnect.getConnection();
        this.store = store;
        this.redisConnection.select(store);
        this.expirySeconds = expirySeconds;
    }

    public boolean isUniqueEvent(String checksum) throws JedisException {

        boolean unique = false;
        try {
            unique = !redisConnection.exists(checksum);
        } catch (JedisException ex) {
            this.redisConnection.close();
            this.redisConnection = redisConnect.getConnection(store, 10000);
            unique = !redisConnection.exists(checksum);
        }
        return unique;
    }

    public void storeChecksum(String checksum) throws JedisException {

        try {
            redisConnection.setex(checksum, expirySeconds, "");
        } catch (JedisException ex) {
            this.redisConnection.close();
            this.redisConnection = redisConnect.getConnection(10000);
            this.redisConnection.select(store);
            redisConnection.setex(checksum, expirySeconds, "");
        }
    }

    public Jedis getRedisConnection() {
        return redisConnection;
    }

}
