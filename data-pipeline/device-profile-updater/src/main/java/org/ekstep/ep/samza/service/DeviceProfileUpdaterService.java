package org.ekstep.ep.samza.service;

import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.samza.config.Config;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.DeviceProfile;
import org.ekstep.ep.samza.task.DeviceProfileUpdaterSink;
import org.ekstep.ep.samza.task.DeviceProfileUpdaterSource;
import org.ekstep.ep.samza.util.CassandraConnect;
import org.ekstep.ep.samza.util.RedisConnect;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

import java.util.*;

public class DeviceProfileUpdaterService {

    private static Logger LOGGER = new Logger(DeviceProfileUpdaterService.class);
    private RedisConnect redisConnect;
    private Jedis deviceStoreConnection;
    private int deviceStoreDb;
    private CassandraConnect cassandraConnection;
    private String cassandra_db;
    private String cassandra_table;
    private Gson gson = new Gson();
    private Type mapType = new TypeToken<Map<String, Object>>() { }.getType();

    public DeviceProfileUpdaterService(Config config, RedisConnect redisConnect, CassandraConnect cassandraConnection) {
        this.redisConnect = redisConnect;
        this.cassandraConnection = cassandraConnection;
        this.deviceStoreDb = config.getInt("redis.database.deviceStore.id", 2);
        this.deviceStoreConnection = redisConnect.getConnection(deviceStoreDb);
        this.cassandra_db = config.get("cassandra.keyspace", "device_db");
        this.cassandra_table = config.get("cassandra.device_profile_table", "device_profile");

    }

    public void process(DeviceProfileUpdaterSource source, DeviceProfileUpdaterSink sink) {
        try {
            Map<String, String> message = source.getMap();
            updateDeviceDetails(message, sink);
        } catch (JsonSyntaxException e) {
            LOGGER.error(null, "INVALID EVENT: " + source.getMessage());
            sink.toMalformedTopic(source.getMessage());
        }
    }

    private void updateDeviceDetails(Map<String, String> deviceData, DeviceProfileUpdaterSink sink) {
        if (deviceData.size() > 0) {

            deviceData.values().removeAll(Collections.singleton(""));
            deviceData.values().removeAll(Collections.singleton("{}"));

            DeviceProfile deviceProfile = new DeviceProfile().fromMap(deviceData);
            String deviceId = deviceData.get("device_id");
            if (null != deviceId && !deviceId.isEmpty()) {

                // Update device profile details in Cassandra DB
                addDeviceDataToDB(deviceData);
                sink.deviceDBUpdateSuccess();

                // Update device profile details in Redis cache
                addDeviceDataToCache(deviceId, deviceProfile);
                sink.deviceCacheUpdateSuccess();

                sink.success();
                LOGGER.info(deviceId,"Updated successfully");
            }
            else { sink.failed(); }
        }

    }

    private void addDeviceDataToCache(String deviceId, DeviceProfile deviceProfile) {
        try {
            addToCache(deviceId, deviceProfile, deviceStoreConnection);
        } catch (JedisException ex) {
            redisConnect.resetConnection();
            try (Jedis redisConn = redisConnect.getConnection(deviceStoreDb)) {
                this.deviceStoreConnection = redisConn;
                addToCache(deviceId, deviceProfile, deviceStoreConnection);
            }
        }
    }

    private void addDeviceDataToDB(Map<String, String> deviceData) {
        Map<String, String> parseduaspec = null != deviceData.get("uaspec") ? parseSpec(deviceData.get("uaspec")) : null;
        Map<String, String> parsedevicespec = null != deviceData.get("device_spec") ? parseSpec(deviceData.get("device_spec")) : null;
        Long firstAccess = Long.parseLong(deviceData.get("first_access"));
        Long lastUpdatedDate = Long.parseLong(deviceData.get("api_last_updated_on"));
        String deviceId = deviceData.get("device_id");
        List<String> parsedKeys = new ArrayList<>(Arrays.asList("uaspec", "device_spec", "first_access", "api_last_updated_on"));
        deviceData.keySet().removeAll(parsedKeys);

        Insert query = QueryBuilder.insertInto(cassandra_db, cassandra_table)
                .values(new ArrayList<>(deviceData.keySet()), new ArrayList<>(deviceData.values()))
                .value("api_last_updated_on", lastUpdatedDate);

        if (null != parseduaspec) { query.value("uaspec", parseduaspec);}
        if (null != parsedevicespec) { query.value("device_spec", parsedevicespec); }
        cassandraConnection.upsert(query);

        /**
         * Update first_access only if it null in the device_profile database
         */
        String updateFirstAccessQuery = String.format("UPDATE %s.%s SET first_access = %d WHERE device_id = '%s' IF first_access = null",
                cassandra_db, cassandra_table, firstAccess, deviceId);
        cassandraConnection.upsert(updateFirstAccessQuery);
    }

    private Map<String, String> parseSpec(String spec) {
        return gson.fromJson(spec, mapType);
    }

    private void addToCache(String deviceId, DeviceProfile deviceProfile, Jedis redisConnection) {
        Map<String, String> deviceMap = deviceProfile.toMap();
        deviceMap.values().removeAll(Collections.singleton(""));
        deviceMap.values().removeAll(Collections.singleton("{}"));
        LOGGER.info(deviceId,"Profile: "+deviceMap);
        if (redisConnection.exists(deviceId)) {
            LOGGER.info(deviceId,"Profile exists in redis: "+deviceMap);
            Map<String, String> data = redisConnection.hgetAll(deviceId);
            LOGGER.info(deviceId,"Profile firstaccess value: "+data.get("firstaccess"));
            if(data.get("firstaccess") != null || !("0").equals(data.get("firstaccess"))) {
                LOGGER.info(deviceId,"Profile has firstaccess: "+deviceMap);
                deviceMap.remove("firstaccess");
            }
            redisConnection.hmset(deviceId, deviceMap);
        } else {
            LOGGER.info(deviceId,"Profile does not have firstaccess: "+deviceMap);
            redisConnection.hmset(deviceId, deviceMap);
        }
        LOGGER.debug(null, String.format("Device details for device id %s updated successfully", deviceId));
    }
}
