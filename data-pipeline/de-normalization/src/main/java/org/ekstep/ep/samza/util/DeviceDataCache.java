package org.ekstep.ep.samza.util;

import com.datastax.driver.core.Row;
import com.google.gson.Gson;
import org.apache.samza.config.Config;
import org.ekstep.ep.samza.core.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;
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

    public DeviceDataCache(Config config, RedisConnect redisConnect, CassandraConnect cassandraConnetion) {
        this.config = config;
        this.cassandra_db = config.get("cassandra.keyspace", "device_db");
        this.cassandra_table = config.get("cassandra.device_profile_table", "device_profile");
        this.deviceDBIndex = config.getInt("redis.deviceDB.index", 0);
        this.redisConnect = redisConnect;
        this.cassandraConnetion = cassandraConnetion;
    }

    public Map getDataForDeviceId(String did, String channel) {

        try (Jedis jedis = redisConnect.getConnection()) {
            jedis.select(deviceDBIndex);
            Gson gson = new Gson();
            Map deviceMap = new HashMap();
            // Key will be device_id:channel
            String key = String.format("%s:%s", did, channel);
            Map<String, String> fields = jedis.hgetAll(key);
            List<Row> rows;
            if (fields.isEmpty()) {
                String query =
                        String.format("SELECT device_id, device_spec, uaspec, first_access FROM %s.%s WHERE device_id = '%s' AND channel = '%s'",
                                cassandra_db, cassandra_table, did, channel);
                rows = cassandraConnetion.execute(query);
                Map<String, String> deviceSpec = null;
                Map<String, String> uaSpec = null;
                Long first_access = null;
                Map<String, String> redisFinalMap = new HashMap();
                Map<String, Object> eventFinalMap = new HashMap();

                if (rows.size() > 0) {
                    Row row = rows.get(0);
                    if(row.isNull("device_spec")) deviceSpec = new HashMap(); else deviceSpec = row.getMap("device_spec", String.class, String.class);
                    if(row.isNull("uaspec")) uaSpec = new HashMap<String, String>(); else uaSpec = row.getMap("uaspec", String.class, String.class);
                    first_access = row.getTimestamp("first_access").getTime();
                    eventFinalMap.putAll(deviceSpec);
                    eventFinalMap.put("uaspec", uaSpec);
                    eventFinalMap.put("firstaccess", first_access);
                    for (Map.Entry<String, Object> propertyMap : eventFinalMap.entrySet()) {
                        redisFinalMap.put(propertyMap.getKey().toLowerCase(), gson.toJson(propertyMap.getValue()));
                    }
                    addDataToCache(did, channel, redisFinalMap);
                    return eventFinalMap;
                }
                else
                    return null;
            } else {
                for (Map.Entry<String, String> entry : fields.entrySet())
                {
                    deviceMap.put(entry.getKey(), gson.fromJson(entry.getValue(), Object.class));
                }
                return deviceMap;
            }
        } catch (JedisException ex) {
            LOGGER.error("", "GetDataForDeviceId: Unable to get a resource from the redis connection pool ", ex);
            return null;
        }

    }

    public void addDataToCache(String did, String channel, Map deviceData) {
        try (Jedis jedis = redisConnect.getConnection()) {
            jedis.select(deviceDBIndex);
            // Key will be device_id:channel
            String key = String.format("%s:%s", did, channel);
            Map values = deviceData;
            jedis.hmset(key, values);
            jedis.expire(key, config.getInt("device.db.redis.key.expiry.seconds", 86400));
        } catch (JedisException ex) {
            LOGGER.error("", "AddDeviceDataToCache: Unable to get a resource from the redis connection pool ", ex);
        }
    }
}
