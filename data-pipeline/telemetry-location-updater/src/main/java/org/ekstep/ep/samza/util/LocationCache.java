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
    private int locationDbKeyExpiryTimeInSeconds;

    public LocationCache(Config config) {
        this.config = config;
        this.cassandra_db = config.get("cassandra.keyspace", "device_db");
        this.cassandra_table = config.get("cassandra.device_profile_table", "device_profile");
        String redis_host = config.get("redis.host", "localhost");
        Integer redis_port = config.getInt("redis.port", 6379);
        this.jedisPool = new JedisPool(buildPoolConfig(), redis_host, redis_port);
        this.cassandraConnetion = new CassandraConnect(config);
        this.locationDbKeyExpiryTimeInSeconds = config.getInt("location.db.redis.key.expiry.seconds", 86400);
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

    public Location getLocationForDeviceId(String did, String channel) {

        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, String> fields = jedis.hgetAll(did);
            List<Row> rows;
            if (fields.isEmpty()) {
                String query =
                    String.format("SELECT device_id, country_code, country, state_code, state, city FROM %s.%s WHERE device_id = '%s' AND channel = '%s'",
                        cassandra_db, cassandra_table, did, channel);
                rows = cassandraConnetion.execute(query);
                String countryCode = null;
                String country = null;
                String stateCode = null;
                String state = null;
                String city = null;

                if (rows.size() > 0) {
                    Row row = rows.get(0);
                    countryCode = row.getString("country_code");
                    country = row.getString("country");
                    stateCode = row.getString("state_code");
                    state = row.getString("state");
                    city = row.getString("city");
                }

                Location location = new Location(countryCode, country, stateCode, state, city);
                if (location.isLocationResolved()) {
                    addLocationToCache(did, location);
                    return location;
                } else return null;
            } else {
                return new Location(fields.get("country_code"), fields.get("country"),
                        fields.get("state_code"), fields.get("state"), fields.get("city"));
            }
        } catch (JedisException ex) {
            LOGGER.error("", "GetLocationForDeviceId: Unable to get a resource from the redis connection pool ", ex);
            return null;
        }
    }

    public void addLocationToCache(String did, Location location) {
        try (Jedis jedis = jedisPool.getResource()) {
            if(location.isLocationResolved()) {
                jedis.hset(did, "country_code", location.getCountryCode());
                jedis.hset(did, "country", location.getCountry());
                jedis.hset(did, "state_code", location.getStateCode());
                jedis.hset(did, "state", location.getState());
                jedis.hset(did, "city", location.getCity());
                jedis.expire(did, locationDbKeyExpiryTimeInSeconds);
            }
        } catch (JedisException ex) {
            LOGGER.error("", "AddLocationToCache: Unable to get a resource from the redis connection pool ", ex);
        }
    }
}
