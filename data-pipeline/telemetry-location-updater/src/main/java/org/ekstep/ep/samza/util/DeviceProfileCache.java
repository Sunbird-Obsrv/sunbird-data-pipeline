package org.ekstep.ep.samza.util;

import com.datastax.driver.core.Row;
import org.apache.samza.config.Config;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.DeviceProfile;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;


public class DeviceProfileCache {

    private static Logger LOGGER = new Logger(DeviceProfileCache.class);
    private String cassandra_db;
    private String cassandra_table;
    private CassandraConnect cassandraConnection;
    private int locationDbKeyExpiryTimeInSeconds;
    private int cacheUnresolvedLocationExpiryTimeInSeconds;
    private RedisConnect redisConnect;
    private JobMetrics metrics;
    private Jedis redisConnection;
    private int databaseIndex;

    public DeviceProfileCache(Config config, JobMetrics metrics) {
        this.cassandra_db = config.get("cassandra.keyspace", "device_db");
        this.cassandra_table = config.get("cassandra.device_profile_table", "device_profile");
        this.databaseIndex = config.getInt("redis.database.deviceLocationStore.id", 2);
        this.cassandraConnection = new CassandraConnect(config);
        this.redisConnect = new RedisConnect(config);
        this.redisConnection = this.redisConnect.getConnection(databaseIndex);
        this.locationDbKeyExpiryTimeInSeconds = config.getInt("location.db.redis.key.expiry.seconds", 86400);
        this.cacheUnresolvedLocationExpiryTimeInSeconds = config.getInt("cache.unresolved.location.key.expiry.seconds", 3600);
        this.metrics = metrics;
    }

    public DeviceProfile getDeviceProfileForDeviceId(String did) {
        DeviceProfile deviceProfile = null;
        if (did != null && !did.isEmpty()) {
            // Get location from redis. Safe retry once for redis connection loss or error
            try {
                deviceProfile = getDeviceProfileFromCache(did);
            } catch (JedisException ex) {
                LOGGER.error(null, "Reconnecting with Redis store due to exception: ", ex);
                redisConnect.resetConnection();
                try (Jedis redisConn = redisConnect.getConnection(databaseIndex)) {
                    this.redisConnection = redisConn;
                    deviceProfile = getDeviceProfileFromCache(did);
                }
            }

            if (null != deviceProfile && deviceProfile.isLocationResolved()) {
                metrics.incCacheHitCounter();
                return deviceProfile;
            } else {
                try {
                    deviceProfile = getDeviceProfileFromDeviceProfileDB(did);
                } catch (Exception ex) {
                    metrics.incDBErrorCount();
                    LOGGER.error(null, "Reconnecting with Cassandra store due to exception: ", ex);
                    cassandraConnection.reconnect();
                    deviceProfile = getDeviceProfileFromDeviceProfileDB(did);
                }
            }

            if (null != deviceProfile && deviceProfile.isLocationResolved()) {
                metrics.incDBHitCount();
            } else {
                metrics.incNoDataCount();
            }
            addDeviceProfileToCache(did, deviceProfile);
        }
        return deviceProfile;
    }

    public DeviceProfile getDeviceProfileFromCache(String deviceId) {
        return new DeviceProfile().fromMap(redisConnection.hgetAll(deviceId));
    }

    public DeviceProfile getDeviceProfileFromDeviceProfileDB(String deviceId) {
        DeviceProfile deviceProfile = new DeviceProfile();
        String query =
                String.format("SELECT device_id, country_code, country, state_code, state, city, state_custom, " +
                                "state_code_custom, district_custom,device_id, device_spec, uaspec, first_access FROM %s.%s WHERE device_id = '%s'",
                        cassandra_db, cassandra_table, deviceId);
        Row result = cassandraConnection.findOne(query);
        if (null != result) {
            String locationState = result.getString("state");
            if (locationState != null && !locationState.isEmpty()) {
                deviceProfile.setCountryCode(result.getString("country_code"));
                deviceProfile.setCountry(result.getString("country"));
                deviceProfile.setStateCode(result.getString("state_code"));
                deviceProfile.setState(result.getString("state"));
                deviceProfile.setCity(result.getString("city"));
                deviceProfile.setDistrictCustom(result.getString("district_custom"));
                deviceProfile.setStateCustomName(result.getString("state_custom"));
                deviceProfile.setStateCodeCustom(result.getString("state_code_custom"));
                if(!result.isNull("device_spec"))
                deviceProfile.setDevicespec(result.getMap("device_spec", String.class, String.class));
                if(!result.isNull("uaspec"))
                deviceProfile.setUaspec(result.getMap("uaspec", String.class, String.class));
                if(!result.isNull("first_access"))
                deviceProfile.setFirstaccess(result.getTimestamp("first_access").getTime());
            }
        }
        return deviceProfile;
    }

    private void addDeviceProfileToCache(String did, DeviceProfile deviceProfile) {
        try {
            redisConnection.hmset(did, deviceProfile.toMap());
            redisConnection.expire(did, deviceProfile.isLocationResolved() ?
                    locationDbKeyExpiryTimeInSeconds : cacheUnresolvedLocationExpiryTimeInSeconds);
        } catch (JedisException ex) {
            LOGGER.error("", "AddLocationToCache: Unable to get a resource from the redis " +
                    "connection pool or something wrong ", ex);
        }
    }
}
