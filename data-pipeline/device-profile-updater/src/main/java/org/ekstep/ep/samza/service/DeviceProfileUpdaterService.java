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
    private String deviceTopic;
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
        this.deviceTopic = config.get("input.device.topic.name", "device.profile");
        this.cassandra_db = config.get("cassandra.keyspace", "device_db");
        this.cassandra_table = config.get("cassandra.device_profile_table", "device_profile");

    }

    public void process(DeviceProfileUpdaterSource source, DeviceProfileUpdaterSink sink) {

        if (deviceTopic.equalsIgnoreCase(source.getSystemStreamPartition().getStream())) {
            Map<String, Object> message = source.getMap();
            updateDeviceProfileCache(message, sink);
        }
    }

    private void updateDeviceProfileCache(Map<String, Object> message, DeviceProfileUpdaterSink sink) {
        String deviceId = (String) message.get("device_id");
        Map<String, String> deviceData = new HashMap<String, String>();

        for (Map.Entry<String, Object> entry : message.entrySet()) {
            if (entry.getValue() instanceof String) {
                deviceData.put(entry.getKey(), entry.getValue().toString());
            }
        }
        if (deviceData.size() > 0) {
            deviceData.values().removeAll(Collections.singleton(null));
            deviceData.values().removeAll(Collections.singleton(""));
            addDeviceDataToRedisCache(deviceId, deviceData, deviceStoreConnection);
            sink.deviceCacheUpdateSuccess();
            addDeviceDataToCassandra(deviceId, deviceData, cassandraConnection);
            sink.deviceDBUpdateSuccess();
            sink.success();
        }

    }

    private void addDeviceDataToRedisCache(String deviceId, Map<String, String> deviceData, Jedis redisConnection) {
        try {
            if (deviceId != null && !deviceId.isEmpty() && null != deviceData && !deviceData.isEmpty()) {
                redisConnection.hmset(deviceId, deviceData);
            }
        } catch (JedisException ex) {
            redisConnect.resetConnection();
            try (Jedis redisConn = redisConnect.getConnection(deviceStoreDb)) {
                this.deviceStoreConnection = redisConn;
                if (null != deviceData)
                    addToRedisCache(deviceId, gson.toJson(deviceData), deviceStoreConnection);
            }
        }
    }

    private void addDeviceDataToCassandra(String deviceId, Map<String, String> deviceData, CassandraConnect cassandraConnection) {
        if (deviceId != null && !deviceId.isEmpty() && null != deviceData && !deviceData.isEmpty()) {
            Insert query = QueryBuilder.insertInto(cassandra_db, cassandra_table).values(new ArrayList<>(deviceData.keySet()), new ArrayList<>(deviceData.values()));
            boolean result = cassandraConnection.upsert(query);
        }
    }

    private void addToRedisCache(String key, String value, Jedis redisConnection) {
        if (key != null && !key.isEmpty() && null != value && !value.isEmpty()) {
            redisConnection.set(key, value);
        }
    }
}
