package org.ekstep.ep.samza.util;

import com.datastax.driver.core.Row;
import com.github.benmanes.caffeine.cache.Cache;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.samza.config.Config;
import org.ekstep.ep.samza.core.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceDataCache {

    private static Logger LOGGER = new Logger(DeviceDataCache.class);

    private String cassandra_db;
    private String cassandra_table;
    private CassandraConnect cassandraConnetion;
    private RedisConnect redisConnect;
    private Integer deviceDBIndex;
    private Config config;
    private LRUCache lruCache;

    public DeviceDataCache(Config config, RedisConnect redisConnect, CassandraConnect cassandraConnetion, LRUCache lruCache) {
        this.config = config;
        this.cassandra_db = config.get("cassandra.keyspace", "device_db");
        this.cassandra_table = config.get("cassandra.device_profile_table", "device_profile");
        this.deviceDBIndex = config.getInt("redis.deviceDB.index", 0);
        this.redisConnect = redisConnect;
        this.cassandraConnetion = cassandraConnetion;
        this.lruCache = lruCache;
    }

    public Map getDataForDeviceId(String did, String channel) {
        Cache<String, String> cache = lruCache.getConnection();
        String key = String.format("%s:%s", did, channel);

        String dataNode = null;
        dataNode = cache.getIfPresent(key);
        if(dataNode != null) {
            System.out.println("fetching from LRU: " + key);
            LOGGER.warn("", "fetching from LRU: " + key);
            return parseData(dataNode);
        } else {
            System.out.println("fetching from DB: " + key);
            LOGGER.warn("", "fetching from DB: " + key);

            Map<String, Object> value = getFromDB(did, channel);
            Gson gson = new Gson();
            String data = gson.toJson(value);
            System.out.println("DB data : "+ data);
            cache.put(key, data);

            return value;
        }
    }

    private Map parseData(String value) {
        Map deviceMap = new HashMap();
        Gson gson = new Gson();
        Map<String, Object> parsedData = null;
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        parsedData = gson.fromJson(value, type);
        for (Map.Entry<String, Object> entry : parsedData.entrySet()) {
            deviceMap.put(entry.getKey(), entry.getValue());
        }
        return deviceMap;
    }

    private Map<String, Object> getFromDB(String did, String channel) {
        Gson gson = new Gson();
        List<Row> rows;
        String query =
                String.format("SELECT device_id, device_spec, uaspec, first_access FROM %s.%s WHERE device_id = '%s' AND channel = '%s'",
                        cassandra_db, cassandra_table, did, channel);
        rows = cassandraConnetion.execute(query);
        Map<String, String> deviceSpec = null;
        Map<String, String> uaSpec = null;
        Long first_access = null;
        Map<String, Object> eventFinalMap = new HashMap();

        if (rows.size() > 0) {
            Row row = rows.get(0);
            if (row.isNull("device_spec")) deviceSpec = new HashMap();
            else deviceSpec = row.getMap("device_spec", String.class, String.class);
            if (row.isNull("uaspec")) uaSpec = new HashMap<String, String>();
            else uaSpec = row.getMap("uaspec", String.class, String.class);
            if (!row.isNull("first_access")) first_access = row.getTimestamp("first_access").getTime();
            eventFinalMap.putAll(deviceSpec);
            eventFinalMap.put("uaspec", uaSpec);
            if (first_access != null) eventFinalMap.put("firstaccess", first_access);
            return eventFinalMap;
        } else
            return null;
    }
}
