package org.ekstep.ep.samza.util;

import com.datastax.driver.core.Row;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.samza.config.Config;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.core.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;
import java.lang.reflect.Type;
import java.util.*;

public class DeviceDataCache {

    private static Logger LOGGER = new Logger(DeviceDataCache.class);

    private String cassandra_db;
    private String cassandra_table;
    private CassandraConnect cassandraConnection;
    private Config config;
    private RedisConnect redisPool;
    private Jedis redisConnection;
    private Gson gson = new Gson();
    private JobMetrics metrics;
    private int databaseIndex;

    public DeviceDataCache(Config config, JobMetrics metrics) {
        this.config = config;
        this.metrics = metrics;
        this.databaseIndex = config.getInt("redis.deviceDB.index", 3);
        this.redisPool = new RedisConnect(config);
        this.redisConnection = this.redisPool.getConnection(databaseIndex);
        this.cassandra_db = config.get("cassandra.keyspace", "device_db");
        this.cassandra_table = config.get("cassandra.device_profile_table", "device_profile");
        this.cassandraConnection = new CassandraConnect(config);
    }

    /*
    public Map getDataForDeviceId(String did) {

        try {
            Map<String, Object> parsedData = null;
            Map deviceMap = new HashMap();
            // Key will be device_id:channel
            String fields = redisConnection.get(did);
            List<Row> rows;
            if (fields == null) {
                String query =
                        String.format("SELECT device_id, device_spec, uaspec, first_access FROM %s.%s WHERE device_id = '%s'",
                                cassandra_db, cassandra_table, did);
                rows = cassandraConnetion.execute(query);
                Map<String, String> deviceSpec = null;
                Map<String, String> uaSpec = null;
                Long first_access = null;
                Map<String, Object> eventFinalMap = new HashMap<>();

                if (rows.size() > 0) {
                    Row row = rows.get(0);
                    if (row.isNull("device_spec")) deviceSpec = new HashMap<>();
                    else deviceSpec = row.getMap("device_spec", String.class, String.class);
                    if (row.isNull("uaspec")) uaSpec = new HashMap<>();
                    else uaSpec = row.getMap("uaspec", String.class, String.class);
                    if (!row.isNull("first_access")) first_access = row.getTimestamp("first_access").getTime();
                    eventFinalMap.putAll(deviceSpec);
                    eventFinalMap.put("uaspec", uaSpec);
                    if (first_access != null) eventFinalMap.put("firstaccess", first_access);
                    addDataToCache(did, gson.toJson(eventFinalMap));
                    return eventFinalMap;
                } else
                    return null;
            } else {
                Type type = new TypeToken<Map<String, Object>>() {
                }.getType();
                parsedData = gson.fromJson(fields, type);
                for (Map.Entry<String, Object> entry : parsedData.entrySet()) {
                    deviceMap.put(entry.getKey(), entry.getValue());
                }
                return deviceMap;
            }
        } catch (JedisException ex) {
            LOGGER.error("", "GetDataForDeviceId: Unable to get a resource from the redis connection pool ", ex);
            return null;
        }

    }
    */

    public Map<String, Object> getDataForDeviceId(String did) {
        Map<String, Object> deviceDataMap = null;
        if(did != null && !did.isEmpty()) {
            try {
                deviceDataMap = getDeviceDataFromCache(did);
            } catch(JedisException ex) {
                redisPool.resetConnection();
                this.redisConnection = redisPool.getConnection(databaseIndex);
                deviceDataMap = getDeviceDataFromCache(did);
            }

            if(deviceDataMap != null && !deviceDataMap.isEmpty()) {
                metrics.incDeviceCacheHitCount();
                return deviceDataMap;
            } else {
                try {
                    deviceDataMap = getDeviceDataFromDB(did);
                } catch (Exception ex) {
                    metrics.incDeviceDBErrorCount();
                    cassandraConnection.reconnect();
                    deviceDataMap = getDeviceDataFromDB(did);
                }
            }

            if(null != deviceDataMap) {
                metrics.incDeviceDbHitCount();
            } else {
                metrics.incNoDataCount();
            }

            addDataToCache(did, gson.toJson(deviceDataMap));
        }
        return deviceDataMap;
    }

    private Map<String, Object> getDeviceDataFromCache(String did) {
        Map<String, Object> deviceMap = new HashMap<>();
        String deviceData = redisConnection.get(did);
        if (deviceData != null && !deviceData.isEmpty()) {
            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> parsedData = gson.fromJson(deviceData, type);
            for (Map.Entry<String, Object> entry : parsedData.entrySet()) {
                deviceMap.put(entry.getKey(), entry.getValue());
            }
        }
        return deviceMap;
    }

    private Map<String, Object> getDeviceDataFromDB(String did) {
        String query =
                String.format("SELECT device_id, device_spec, uaspec, first_access FROM %s.%s WHERE device_id = '%s'",
                        cassandra_db, cassandra_table, did);
        Row row = cassandraConnection.findOne(query);
        Map<String, String> deviceSpec;
        Map<String, String> uaSpec;
        Long first_access = null;
        Map<String, Object> deviceDataMap = new HashMap<>();
        if (null != row) {
            if (row.isNull("device_spec")) {
                deviceSpec = new HashMap<>();
            } else {
                deviceSpec = row.getMap("device_spec", String.class, String.class);
            }

            if (row.isNull("uaspec")) {
                uaSpec = new HashMap<>();
            } else {
                uaSpec = row.getMap("uaspec", String.class, String.class);
            }

            if (!row.isNull("first_access")) first_access = row.getTimestamp("first_access").getTime();
            deviceDataMap.putAll(deviceSpec);
            deviceDataMap.put("uaspec", uaSpec);
            if (first_access != null) deviceDataMap.put("firstaccess", first_access);
        }

        return deviceDataMap;

    }

    public void addDataToCache(String did, String deviceData) {
        if (deviceData != null) {
            try {
                redisConnection.set(did, deviceData);
                redisConnection.expire(did, config.getInt("device.db.redis.key.expiry.seconds", 86400));
            } catch (JedisException ex) {
                LOGGER.error("", "AddDeviceDataToCache: Unable to get a resource from the redis connection pool ", ex);
            }
        }
    }
}
