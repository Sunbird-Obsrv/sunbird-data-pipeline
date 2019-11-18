package org.ekstep.ep.samza.util;

import org.apache.samza.config.Config;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.exceptions.JedisException;
import com.google.gson.Gson;

import java.time.Duration;


public class RedisConnect {
    private JedisPool jedisPool;
    private Config config;
    private Gson gson = new Gson();

    public RedisConnect(Config config) {
        this.config = config;
        String redis_host = config.get("redis.host", "localhost");
        Integer redis_port = config.getInt("redis.port", 6379);
        this.jedisPool = new JedisPool(buildPoolConfig(), redis_host, redis_port);
    }

    private JedisPoolConfig buildPoolConfig() {
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(config.getInt("redis.connection.max", 2));
        poolConfig.setMaxIdle(config.getInt("redis.connection.idle.max", 2));
        poolConfig.setMinIdle(config.getInt("redis.connection.idle.min", 1));
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(config.getInt("redis.connection.minEvictableIdleTimeSeconds", 120)).toMillis());
        poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(config.getInt("redis.connection.timeBetweenEvictionRunsSeconds", 300)).toMillis());
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);
        return poolConfig;
    }

    public Jedis getConnection() {
        return jedisPool.getResource();
    }

    public Jedis getConnection(int database) {
        Jedis conn = jedisPool.getResource();
        conn.select(database);
        return conn;
    }

    public void resetConnection() {
        this.jedisPool.close();
        String redis_host = config.get("redis.host", "localhost");
        Integer redis_port = config.getInt("redis.port", 6379);
        this.jedisPool = new JedisPool(buildPoolConfig(), redis_host, redis_port);
    }

    public Jedis resetConnection(int database) {
        resetConnection();
        return getConnection(database);
    }

    public void addToCache(String key, String value, Jedis redisConnection, int storeId) {
        try {
            if (key != null && !key.isEmpty() && null != value && !value.isEmpty()) {
                redisConnection.set(key, value);
            }
        } catch(JedisException ex) {
            redisConnection = resetConnection(storeId);
            if (null != value)
                redisConnection.set(key, value);
        }
    }

    public String readFromCache(String key, Jedis redisConnection, int storeId) {
        try {
            return redisConnection.get(key);
        }
        catch (JedisException ex) {
            redisConnection = resetConnection(storeId);
            return redisConnection.get(key);
        }
    }
}
