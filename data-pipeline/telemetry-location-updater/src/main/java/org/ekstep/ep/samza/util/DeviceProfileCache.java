package org.ekstep.ep.samza.util;

import org.apache.samza.config.Config;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.DeviceProfile;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;
import java.util.Map;


public class DeviceProfileCache {

    private static Logger LOGGER = new Logger(DeviceProfileCache.class);
    private RedisConnect redisConnect;
    private JobMetrics metrics;
    private Jedis redisConnection;
    private int databaseIndex;

    public DeviceProfileCache(Config config, JobMetrics metrics, RedisConnect redisConnect) {
        this.databaseIndex = config.getInt("redis.database.deviceLocationStore.id", 2);
        this.redisConnect = redisConnect;
        this.redisConnection = this.redisConnect.getConnection(databaseIndex);
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
                this.redisConnection.close();
                this.redisConnection = redisConnect.getConnection(databaseIndex);
                deviceProfile = getDeviceProfileFromCache(did);
            }
            if (null != deviceProfile && deviceProfile.isLocationResolved()) {
                metrics.incCacheHitCounter();
            } else {
                metrics.incEmptyCacheValueCounter();
            }
        }
        return deviceProfile;
    }

    public DeviceProfile getDeviceProfileFromCache(String deviceId) {
        Map<String, String> data = redisConnection.hgetAll(deviceId);
        if (data.size() > 0) {
            return new DeviceProfile().fromMap(data);
        } else {
            return null;
        }
    }
}
