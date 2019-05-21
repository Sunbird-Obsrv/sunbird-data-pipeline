package org.ekstep.ep.samza.util;

import com.datastax.driver.core.Row;
import org.apache.samza.config.Config;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Location;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.util.*;

public class DeviceLocationCache {

    private static Logger LOGGER = new Logger(DeviceLocationCache.class);

    private String cassandra_db;
    private String cassandra_table;
    private CassandraConnect cassandraConnection;
    private int locationDbKeyExpiryTimeInSeconds;
    private int cacheUnresolvedLocationExpiryTimeInSeconds;
    private RedisConnect redisConnect;
    private JobMetrics metrics;
    private Jedis redisConnection;

    public DeviceLocationCache(Config config, RedisConnect redisConnect, JobMetrics metrics) {
        this.cassandra_db = config.get("cassandra.keyspace", "device_db");
        this.cassandra_table = config.get("cassandra.device_profile_table", "device_profile");
        this.cassandraConnection = new CassandraConnect(config);
        this.redisConnect = redisConnect;
        this.redisConnection = this.redisConnect.getConnection();
        redisConnection.select(config.getInt("redis.database.deviceLocationStore.id", 2));
        this.locationDbKeyExpiryTimeInSeconds = config.getInt("location.db.redis.key.expiry.seconds", 86400);
        this.cacheUnresolvedLocationExpiryTimeInSeconds = config.getInt("cache.unresolved.location.key.expiry.seconds", 3600);
        this.metrics = metrics;
    }

    public Location getLocationForDeviceId(String did) {
        Location location = null;
        if (did != null && !did.isEmpty()) {
            // Get location from redis. Safe retry once for redis connection loss or error
            try {
                location = getLocationFromCache(did);
            } catch(JedisException ex) {
                redisConnect.resetConnection(); // Asumming your redis connection is stale which should not happen
                this.redisConnection = redisConnect.getConnection();
                location = getLocationFromCache(did);
            }

            if (null != location) {
                metrics.incCacheHitCounter();
                return location;
            } else {
                try {
                    location = getLocationFromDeviceProfileDB(did);
                } catch (Exception ex) {
                    metrics.incDBErrorCount();
                    cassandraConnection.reconnect();
                    location = getLocationFromDeviceProfileDB(did);
                }
            }

            if(null != location) {
                metrics.incDBHitCount();
            } else {
                metrics.incNoDataCount();
            }
            addLocationToCache(did, location);
        }
        return location;
    }

    public Location getLocationFromCache(String deviceId) {
        return new Location().fromMap(redisConnection.hgetAll(deviceId));
    }

    public Location getLocationFromDeviceProfileDB(String deviceId) {
        List<Row> rows;
        Location location = new Location();
        String query =
                String.format("SELECT device_id, country_code, country, state_code, state, city, state_custom, " +
                                "state_code_custom, district_custom FROM %s.%s WHERE device_id = '%s'",
                        cassandra_db, cassandra_table, deviceId);

        rows = cassandraConnection.execute(query);

        Iterator<Row> iterator = rows.iterator();
        if (iterator.hasNext()) {
            Row result = iterator.next();
            String locationState = result.getString("state");
            if (locationState != null && !locationState.isEmpty()) {
                location.setCountryCode(result.getString("country_code"));
                location.setCountry(result.getString("country"));
                location.setStateCode(result.getString("state_code"));
                location.setState(result.getString("state"));
                location.setCity(result.getString("city"));
                location.setDistrictCustom(result.getString("district_custom"));
                location.setStateCustomName(result.getString("state_custom"));
                location.setStateCodeCustom(result.getString("state_code_custom"));
            }
        }
        return location;
    }

    private void addLocationToCache(String did, Location location) {
        try {
            redisConnection.hmset(did, location.toMap());
            redisConnection.expire(did, location.isLocationResolved() ?
                    locationDbKeyExpiryTimeInSeconds : cacheUnresolvedLocationExpiryTimeInSeconds);
        } catch (JedisException ex) {
            LOGGER.error("", "AddLocationToCache: Unable to get a resource from the redis " +
                    "connection pool or something wrong ", ex);
        }
    }
}
