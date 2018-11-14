package org.ekstep.ep.samza.util;

import com.datastax.driver.core.Row;
import org.apache.samza.config.Config;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Location;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class LocationCache {

    private static Logger LOGGER = new Logger(LocationCache.class);

    private String cassandra_db;
    private String cassandra_table;
    private Config config;
    private CassandraConnect cassandraConnetion;
    private JedisPool jedisPool;

    public LocationCache(Config config) {
        this.config = config;
        this.cassandra_db = config.get("cassandra.keyspace", "device_db");
        this.cassandra_table = config.get("cassandra.device_profile_table", "device_profile");
        String redis_host = config.get("redis.host", "localhost");
        Integer redis_port = config.getInt("redis.port", 6379);
        this.jedisPool = new JedisPool(buildPoolConfig(), redis_host, redis_port);
        this.cassandraConnetion = new CassandraConnect(config);
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

    public Location getLocationForDeviceId(String did) {

        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, String> fields = jedis.hgetAll(did);
            List<Row> rows;
            if (fields.isEmpty()) {
                String query = String.format("SELECT device_id, state, city FROM %s.%s WHERE device_id = '%s'",
                        cassandra_db, cassandra_table, did);
                rows = cassandraConnetion.execute(query);
                String state = null;
                String district = null;
                if (rows.size() > 0) {
                    Row r = rows.get(0);
                    state = r.getString("state");
                    district = r.getString("city");
                }

                if (state != null && district != null) {
                    Location location = new Location();
                    location.setDistrict(district);
                    location.setState(state);
                    addLocationToCache(did, state, district);
                    return location;
                } else return null;
            } else {
                Location location = new Location();
                location.setDistrict(fields.get("district"));
                location.setState(fields.get("state"));
                return location;
            }
        } catch (JedisException ex) {
            LOGGER.error("", "GetLocationForDeviceId: Unable to get a resource from the redis connection pool ", ex);
            return null;
        }
    }

    public void addLocationToCache(String did, String state, String district) {
        try (Jedis jedis = jedisPool.getResource()) {
            if(state != null) jedis.hset(did, "state", state);
            if(district != null) jedis.hset(did, "district", district);
        } catch (JedisException ex) {
            LOGGER.error("", "AddLocationToCache: Unable to get a resource from the redis connection pool ", ex);
        }
    }
}
