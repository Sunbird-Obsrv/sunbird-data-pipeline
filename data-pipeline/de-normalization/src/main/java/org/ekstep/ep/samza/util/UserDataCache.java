package org.ekstep.ep.samza.util;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.apache.samza.config.Config;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.core.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.lang.reflect.Type;
import java.util.*;

public class UserDataCache extends DataCache {

    private static Logger LOGGER = new Logger(UserDataCache.class);

    private String cassandra_db;
    private String cassandra_user_table;
    private String cassandra_location_table;
    private CassandraConnect cassandraConnection;
    private RedisConnect redisPool;
    private Jedis redisConnection;
    private int locationDbKeyExpiryTimeInSeconds;
    private Type mapType = new TypeToken<Map<String, Object>>() {
    }.getType();
    private Gson gson = new Gson();
    private JobMetrics metrics;
    private int databaseIndex;

    public UserDataCache(Config config, JobMetrics metrics) {
        super(config.getList("user.metadata.fields", Arrays.asList("usertype", "grade", "language", "subject", "state", "district")));
        this.metrics = metrics;
        this.databaseIndex = config.getInt("redis.userDB.index", 4);
        this.redisPool = new RedisConnect(config);
        this.redisConnection = this.redisPool.getConnection(databaseIndex);
        this.cassandra_db = config.get("middleware.cassandra.keyspace", "sunbird");
        this.cassandra_user_table = config.get("middleware.cassandra.user_table", "user");
        this.cassandra_location_table = config.get("middleware.cassandra.location_table", "location");
        List<String> cassandraHosts = Arrays.asList(config.get("middleware.cassandra.host", "127.0.0.1").split(","));
        this.cassandraConnection = new CassandraConnect(cassandraHosts, config.getInt("middleware.cassandra.port", 9042));
        this.locationDbKeyExpiryTimeInSeconds = config.getInt("location.db.redis.key.expiry.seconds", 86400);

    }

    public Map<String, Object> getUserData(String userId) {
        if ("anonymous".equalsIgnoreCase(userId)) return null;
        Map<String, Object> userDataMap;
        try {
            userDataMap = getUserDataFromCache(userId);
            if (!userDataMap.isEmpty()) {
                userDataMap.keySet().retainAll(this.fieldsList);
                metrics.incUserCacheHitCount();
            }
        } catch (JedisException ex) {
            redisPool.resetConnection();
            redisConnection = redisPool.getConnection(databaseIndex);
            userDataMap = getUserDataFromCache(userId);
        }

        Map<String, Object> userLocationMap;
        if (!userDataMap.containsKey("state")) {
            try {
                userLocationMap = fetchFallbackUserLocationFromDB(userId);
            } catch (Exception ex) {
                metrics.incUserDBErrorCount();
                cassandraConnection.reconnect();
                userLocationMap = fetchFallbackUserLocationFromDB(userId);
            }

            if (!userLocationMap.isEmpty()) {
                metrics.incUserDbHitCount();
                userDataMap.putAll(userLocationMap);
                addToCache(userId, gson.toJson(userDataMap));
            }
        }

        if (userDataMap == null || userDataMap.isEmpty()) {
            metrics.incNoDataCount();
        }
        return userDataMap;
    }

    private Map<String, Object> getUserDataFromCache(String userId) {
        Map<String, Object> cacheData = new HashMap<>();
        String data = redisConnection.get(userId);
        if (data != null && !data.isEmpty()) {
            cacheData = gson.fromJson(data, mapType);
        }
        return cacheData;
    }

    private Map<String, Object> fetchFallbackUserLocationFromDB(String userId) {
        // if (userId == null) return null;
        Map<String, Object> userLocation = new HashMap<>();
        List<String> locationIds = getUserOrgLocationIds(userId);
        if (locationIds != null && !locationIds.isEmpty()) {
            userLocation = getUserLocation(locationIds);
        }
        return userLocation;
    }

    private List<String> getUserOrgLocationIds(String userId) {
        List<String> locationIds = null;
        String locationQuery = QueryBuilder.select("locationids")
                .from(cassandra_db, cassandra_user_table)
                .where(QueryBuilder.eq("id", userId))
                .toString();
        Row row = cassandraConnection.findOne(locationQuery);
        if (null != row) {
            locationIds = row.getList("locationids", String.class);
        }
        return locationIds;
    }

    private Map<String, Object> getUserLocation(List<String> locationIds) {
        String resolveLocation = QueryBuilder.select().all()
                .from(cassandra_db, cassandra_location_table)
                .where(QueryBuilder.in("id", locationIds))
                .toString();
        List<Row> rows = cassandraConnection.find(resolveLocation);
        // Location location = new Location();
        Map<String, Object> result = new HashMap<>();
        if (rows.size() > 0) {
            rows.forEach(record -> {
                String name = record.getString("name");
                String type = record.getString("type");
                if (type.toLowerCase().equals("state")) {
                    result.put("state", name);
                } else if (type.toLowerCase().equals("district")) {
                    result.put("district", name);
                }
            });
        }
        return result;
    }

    private void addToCache(String userId, String userData) {
        try {
            redisConnection.set(userId, userData);
            redisConnection.expire(userId, locationDbKeyExpiryTimeInSeconds);
        } catch (JedisException ex) {
            LOGGER.error("", "AddLocationToCache: Unable to get connection from the " +
                    "redis connection pool. userId: " + userId, ex);
        }
    }
}
