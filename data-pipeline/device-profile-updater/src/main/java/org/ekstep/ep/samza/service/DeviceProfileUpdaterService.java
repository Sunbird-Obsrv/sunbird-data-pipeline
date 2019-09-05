package org.ekstep.ep.samza.service;

import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.samza.config.Config;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.task.DeviceProfileUpdaterSink;
import org.ekstep.ep.samza.task.DeviceProfileUpdaterSource;
import org.ekstep.ep.samza.util.CassandraConnect;
import org.ekstep.ep.samza.util.RedisConnect;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.lang.reflect.Type;
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
    private List<String> contentModelListTypeFields;
    private Type mapType = new TypeToken<Map<String, Object>>() {
    }.getType();

    public DeviceProfileUpdaterService(Config config, RedisConnect redisConnect, CassandraConnect cassandraConnection) {
        this.redisConnect = redisConnect;
        this.cassandraConnection = cassandraConnection;
        this.deviceStoreDb = config.getInt("redis.database.deviceStore.id", 2);
        this.deviceStoreConnection = redisConnect.getConnection(deviceStoreDb);
        this.cassandra_db = config.get("cassandra.keyspace", "device_db");
        this.cassandra_table = config.get("cassandra.device_profile_table", "device_profile");

    }

    public void process(DeviceProfileUpdaterSource source, DeviceProfileUpdaterSink sink) {
            Map<String, Object> message = source.getMap();
            updateDeviceDetails(message, sink);
    }

    private void updateDeviceDetails(Map<String, Object> message, DeviceProfileUpdaterSink sink) {
        Map<String, String> deviceData = new HashMap<String, String>();

        for (Map.Entry<String, Object> entry : message.entrySet()) {
            if (entry.getValue() instanceof String) {
                deviceData.put(entry.getKey(), entry.getValue().toString());
            }
        }
        if (deviceData.size() > 0) {
            deviceData.values().removeAll(Collections.singleton(null));
            deviceData.values().removeAll(Collections.singleton(""));
            addDeviceDataToCache(deviceData, deviceStoreConnection);
            sink.deviceCacheUpdateSuccess();
            addDeviceDataToDB(deviceData, cassandraConnection);
            sink.deviceDBUpdateSuccess();
            sink.success();
        }

    }

    private void addDeviceDataToCache(Map<String, String> deviceData, Jedis redisConnection) {
        String deviceId = deviceData.get("device_id");
        try {
            if (null != deviceId && !deviceId.isEmpty() && null != deviceData && !deviceData.isEmpty()) {
                redisConnection.hmset(deviceId, deviceData);
            }
        } catch (JedisException ex) {
            redisConnect.resetConnection();
            try (Jedis redisConn = redisConnect.getConnection(deviceStoreDb)) {
                this.deviceStoreConnection = redisConn;
                if (null != deviceData)
                    addToCache(deviceId, gson.toJson(deviceData), deviceStoreConnection);
            }
        }
    }

    private void addDeviceDataToDB(Map<String, String> deviceData, CassandraConnect cassandraConnection) {
        String deviceId = deviceData.get("device_id");
        if (null != deviceId && !deviceId.isEmpty() && null != deviceData && !deviceData.isEmpty()) {
            Insert query = QueryBuilder.insertInto(cassandra_db, cassandra_table).values(new ArrayList<>(deviceData.keySet()), new ArrayList<>(deviceData.values()));
            cassandraConnection.upsert(query);
        }
    }

    private void addToCache(String key, String value, Jedis redisConnection) {
        if (null != key && !key.isEmpty() && null != value && !value.isEmpty()) {
            redisConnection.set(key, value);
            LOGGER.info(key,"Updated successfully");
        }
    }
}
