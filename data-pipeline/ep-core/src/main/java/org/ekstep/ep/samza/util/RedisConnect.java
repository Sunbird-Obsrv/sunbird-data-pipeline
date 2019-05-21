package org.ekstep.ep.samza.util;

import org.apache.samza.config.Config;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;


public class RedisConnect {
    private JedisPool jedisPool;
    private Config config;

    public RedisConnect(Config config) {
        this.config = config;
        String redis_host = config.get("redis.host", "localhost");
        Integer redis_port = config.getInt("redis.port", 6379);
        this.jedisPool = new JedisPool(buildPoolConfig(), redis_host, redis_port);
    }

    private JedisPoolConfig buildPoolConfig() {
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(config.getInt("redis.connection.max", 20));
        poolConfig.setMaxIdle(config.getInt("redis.connection.idle.max", 20));
        poolConfig.setMinIdle(config.getInt("redis.connection.idle.min", 10));
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
        String redis_host = config.get("redis.host", "localhost");
        Integer redis_port = config.getInt("redis.port", 6379);
        this.jedisPool = new JedisPool(buildPoolConfig(), redis_host, redis_port);
    }
}
