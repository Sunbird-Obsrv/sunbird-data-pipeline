package org.ekstep.ep.samza.util;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.exceptions.QueryExecutionException;
import org.apache.samza.config.Config;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Location;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.util.*;

public class LocationCache {

    private static Logger LOGGER = new Logger(LocationCache.class);

    private String cassandra_db;
    private String cassandra_table;
    private CassandraConnect cassandraConnection;
    private RedisConnect redisConnect;
    private int locationDbKeyExpiryTimeInSeconds;
    private Config config;
    private JobMetrics metrics;

    public LocationCache(Config config, RedisConnect redisConnect, JobMetrics metrics) {
        this.config = config;
        this.cassandra_db = config.get("cassandra.keyspace", "device_db");
        this.cassandra_table = config.get("cassandra.device_profile_table", "device_profile");
        this.redisConnect = redisConnect;
        this.cassandraConnection = new CassandraConnect(config);
        this.locationDbKeyExpiryTimeInSeconds = config.getInt("location.db.redis.key.expiry.seconds", 86400);
        this.metrics = metrics;
    }

    public Location getLocationForDeviceId(String did, String channel) {
        try(Jedis jedis = redisConnect.getConnection()) {
            jedis.select(config.getInt("redis.database.deviceLocationStore.id", 2));
            // Key will be device_id:channel
            String key = String.format("%s:%s", did, channel);
            Map<String, String> fields = jedis.hgetAll(key);
            List<Row> rows;
            if (fields.isEmpty()) {
                metrics.incCacheMissCounter();
                String query =
                    String.format("SELECT device_id, country_code, country, state_code, state, city, state_custom, state_code_custom, district_custom FROM %s.%s WHERE device_id = '%s' AND channel = '%s'",
                        cassandra_db, cassandra_table, did, channel);
                try {
                    rows = cassandraConnection.execute(query);
                } catch (QueryExecutionException ex) {
                    rows = Collections.emptyList();
                    metrics.incDBErrorCount();
                    LOGGER.error("", "GetLocationForDeviceId: Cassandra query execution failure", ex);
                }

                String countryCode = "";
                String country = "";
                String stateCode = "";
                String state = "";
                String city = "";
                String districtCustom = "";
                String stateCustomName = "";
                String stateCodeCustom = "";

                if (rows.size() > 0) {
                    Row row = rows.get(0);
                    countryCode = row.getString("country_code");
                    country = row.getString("country");
                    stateCode = row.getString("state_code");
                    state = row.getString("state");
                    city = row.getString("city");
                    districtCustom = row.getString("district_custom");
                    stateCustomName = row.getString("state_custom");
                    stateCodeCustom = row.getString("state_code_custom");

                }

                Location location = new Location(countryCode, country, stateCode, state, city, districtCustom, stateCustomName, stateCodeCustom);
                addLocationToCache(did, channel, location);
                if (location.isLocationResolved()) {
                    return location;
                } else {
                    metrics.incNoDataCount();
                    return null;
                }
            } else {
                metrics.incCacheHitCounter();
                Location location = new Location(fields.get("country_code"), fields.get("country"),
                        fields.get("state_code"), fields.get("state"), fields.get("city"), fields.get("district_custom"),fields.get("state_custom"),fields.get("state_code_custom"));
                if(!location.isLocationResolved()) {
                    metrics.incNoDataCount();
                }
                return location;
            }
        } catch (JedisException ex) {
            LOGGER.error("", "GetLocationForDeviceId: Unable to get a resource from the redis connection pool ", ex);
            return null;
        }
    }

    public void addLocationToCache(String did, String channel, Location location) {
        try(Jedis jedis = redisConnect.getConnection()) {
            jedis.select(config.getInt("redis.database.deviceLocationStore.id", 2));
            // Key will be device_id:channel
            String key = String.format("%s:%s", did, channel);
            Map<String, String> values = new HashMap<>();
            values.put("country_code", Location.getValueOrDefault(location.getCountryCode(), ""));
            values.put("country", Location.getValueOrDefault(location.getCountry(), ""));
            values.put("state_code", Location.getValueOrDefault(location.getStateCode(), ""));
            values.put("state", Location.getValueOrDefault(location.getState(), ""));
            values.put("city", Location.getValueOrDefault(location.getCity(), ""));
            values.put("district_custom", Location.getValueOrDefault(location.getDistrictCustom(), ""));
            values.put("state_custom", Location.getValueOrDefault(location.getstateCustomName(), ""));
            values.put("state_code_custom", Location.getValueOrDefault(location.getstateCodeCustom(), ""));

            jedis.hmset(key, values);
            jedis.expire(key, locationDbKeyExpiryTimeInSeconds);
        } catch (JedisException ex) {
            LOGGER.error("", "AddLocationToCache: Unable to get a resource from the redis connection pool or something wrong ", ex);
        }
    }
}
