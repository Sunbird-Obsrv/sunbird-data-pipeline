package org.ekstep.ep.samza.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.apache.samza.config.Config;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.DeviceProfile;
import org.ekstep.ep.samza.task.DeviceProfileUpdaterSink;
import org.ekstep.ep.samza.task.DeviceProfileUpdaterSource;
import org.ekstep.ep.samza.util.PostgresConnect;
import org.ekstep.ep.samza.util.RedisConnect;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

public class DeviceProfileUpdaterService {

    private static Logger LOGGER = new Logger(DeviceProfileUpdaterService.class);
    private RedisConnect redisConnect;
    private Jedis deviceStoreConnection;
    private int deviceStoreDb;
    private PostgresConnect postgresConnect;
    private String postgres_table;
    private Gson gson = new Gson();
    private Type mapType = new TypeToken<Map<String, Object>>() { }.getType();

    public DeviceProfileUpdaterService(Config config, RedisConnect redisConnect, PostgresConnect postgresConnect) {
        this.redisConnect = redisConnect;
        this.postgresConnect = postgresConnect;
        this.deviceStoreDb = config.getInt("redis.database.deviceStore.id", 2);
        this.deviceStoreConnection = redisConnect.getConnection(deviceStoreDb);
        this.postgres_table = config.get("postgres.device_profile_table", "device_profile");
    }

    public void process(DeviceProfileUpdaterSource source, DeviceProfileUpdaterSink sink) throws Exception {
        try {
            Map<String, String> message = source.getMap();
            updateDeviceDetails(message, sink);
        } catch (JsonSyntaxException e) {
            LOGGER.error(null, "INVALID EVENT: " + source.getMessage(), e);
            sink.toMalformedTopic(source.getMessage());
        }
    }

    private void updateDeviceDetails(Map<String, String> deviceData, DeviceProfileUpdaterSink sink) throws Exception {
        if (deviceData.size() > 0) {

            deviceData.values().removeAll(Collections.singleton(""));
            deviceData.values().removeAll(Collections.singleton("{}"));

            DeviceProfile deviceProfile = new DeviceProfile().fromMap(deviceData);
            String deviceId = deviceData.get("device_id");
            if (null != deviceId && !deviceId.isEmpty()) {

                // Update device profile details in Postgres DB
                addDeviceDataToDB(deviceId, deviceData);
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

    private void addDeviceDataToDB(String deviceId, Map<String, String> deviceData) throws Exception {
        Long firstAccess = Long.parseLong(deviceData.get("first_access"));
        Long lastUpdatedDate = Long.parseLong(deviceData.get("api_last_updated_on"));
        List<String> parsedKeys = new ArrayList<>(Arrays.asList("first_access", "api_last_updated_on"));
        deviceData.keySet().removeAll(parsedKeys);

        deviceData.put("api_last_updated_on", new Timestamp(lastUpdatedDate).toString());
        deviceData.put("updated_date", new Timestamp(System.currentTimeMillis()).toString());

        if(null != deviceData.get("uaspec")) {
            deviceData.replace("uaspec", gson.fromJson(deviceData.get("uaspec"), JsonObject.class).toString());
        }
        if(null != deviceData.get("device_spec")) {
            deviceData.replace("device_spec", gson.fromJson(deviceData.get("device_spec"), JsonObject.class).toString());
        }

        String columns = formatValues(deviceData.keySet(),",");
        String values = formatValues(deviceData.values(),"','");

        String upsertQuery = String.format("INSERT INTO %s (%s) VALUES ('%s') ON CONFLICT(device_id) DO UPDATE SET (%s)=('%s');",postgres_table,columns,values,columns,values);
        postgresConnect.execute(upsertQuery);

        String updateFirstAccessQuery = String.format("UPDATE %s SET first_access = '%s' WHERE device_id = '%s' AND first_access IS NULL",
                postgres_table, new Timestamp(firstAccess).toString(), deviceId);
        postgresConnect.execute(updateFirstAccessQuery);

        if(null != deviceData.get("user_declared_state")) {
            String updateUserDeclaredOnQuery = String.format("UPDATE %s SET user_declared_on = '%s' WHERE device_id = '%s' AND user_declared_on IS NULL",
                    postgres_table, new Timestamp(lastUpdatedDate).toString(), deviceId);
            postgresConnect.execute(updateUserDeclaredOnQuery);
        }

    }

    private void addToCache(String deviceId, DeviceProfile deviceProfile, Jedis redisConnection) {
        Map<String, String> deviceMap = deviceProfile.toMap();
        deviceMap.values().removeAll(Collections.singleton(""));
        deviceMap.values().removeAll(Collections.singleton("{}"));
        if (redisConnection.exists(deviceId)) {
            Map<String, String> data = redisConnection.hgetAll(deviceId);
            if(data.get("firstaccess") != null && !("0").equals(data.get("firstaccess"))) {
                deviceMap.remove("firstaccess");
            }
            if(data.get("user_declared_on") != null && data.get("user_declared_state") == null) {
                deviceMap.remove("user_declared_on");
            }
            redisConnection.hmset(deviceId, deviceMap);
        } else {
            redisConnection.hmset(deviceId, deviceMap);
        }
        LOGGER.debug(null, String.format("Device details for device id %s updated successfully", deviceId));
    }

    private String formatValues(Collection<?> values, String delimiter) {
        return values.stream().map(Object::toString).collect(Collectors.joining(delimiter));
    }
}
