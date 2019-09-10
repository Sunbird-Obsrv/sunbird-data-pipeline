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
            Map<String, Object> message = source.getMap();
            updateDeviceDetails(message, sink);
        } catch (JsonSyntaxException e) {
            LOGGER.error(null, "INVALID EVENT: " + source.getMessage());
            sink.toMalformedTopic(source.getMessage());
        }
    }

    private void updateDeviceDetails(Map<String, Object> message, DeviceProfileUpdaterSink sink) {
        Map<String, String> deviceData = new HashMap<String, String>();

        for (Map.Entry<String, Object> entry : message.entrySet()) {
            if (entry.getValue() instanceof String) {
                deviceData.put(entry.getKey(), entry.getValue().toString());
            }
        }
        if (deviceData.size() > 0) {
            deviceData.values().removeAll(Collections.singleton("null"));
            deviceData.values().removeAll(Collections.singleton(""));
            deviceData.values().removeAll(Collections.singleton("{}"));
            DeviceProfile deviceProfile = new DeviceProfile().fromMap(deviceData);
            String deviceId = deviceData.get("device_id");
            if (null != deviceId && !deviceId.isEmpty()) {
                addDeviceDataToCache(deviceId, deviceProfile, deviceStoreConnection);
                sink.deviceCacheUpdateSuccess();
                addDeviceDataToDB(deviceData, cassandraConnection);
                sink.deviceDBUpdateSuccess();
                sink.success();
            }
            else { sink.failed(); }
        }

    }

    private void addDeviceDataToCache(String deviceId, DeviceProfile deviceProfile, Jedis redisConnection) {
        try {
            addToCache(deviceId, deviceProfile, redisConnection);
        } catch (JedisException ex) {
            redisConnect.resetConnection();
            try (Jedis redisConn = redisConnect.getConnection(deviceStoreDb)) {
                this.deviceStoreConnection = redisConn;
                addToCache(deviceId, deviceProfile, deviceStoreConnection);
            }
        }
    }

    private void addDeviceDataToDB(Map<String, String> deviceData, CassandraConnect cassandraConnection) {
        Map<String, String> parseduaspec = null != deviceData.get("uaspec") ? parseSpec(deviceData.get("uaspec")) : null;
        deviceData.values().removeAll(Collections.singleton(deviceData.get("uaspec")));
        Map<String, String> parsedevicespec = null != deviceData.get("device_spec") ? parseSpec(deviceData.get("device_spec")) : null;
        deviceData.values().removeAll(Collections.singleton(deviceData.get("device_spec")));

        Insert query = QueryBuilder.insertInto(cassandra_db, cassandra_table)
                .values(new ArrayList<>(deviceData.keySet()), new ArrayList<>(deviceData.values())).value("uaspec", parseduaspec).value("device_spec", parsedevicespec);
        cassandraConnection.upsert(query);
    }

    private Map<String, String> parseSpec(String spec) {
        Map<String, String> parseSpec = new HashMap<String, String>();
        parseSpec = gson.fromJson(spec, mapType);

        return parseSpec;
    }

    private void addToCache(String deviceId, DeviceProfile deviceProfile, Jedis redisConnection) {
            redisConnection.hmset(deviceId, deviceProfile.toMap());
            LOGGER.info(deviceId, "Updated successfully");
    }
}
