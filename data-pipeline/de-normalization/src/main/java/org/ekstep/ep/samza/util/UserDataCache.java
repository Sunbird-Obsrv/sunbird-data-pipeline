package org.ekstep.ep.samza.util;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.apache.commons.collections.MapUtils;
import org.apache.samza.config.Config;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.core.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDataCache extends DataCache {

    private static Logger LOGGER = new Logger(UserDataCache.class);

    private String cassandra_db;
    private String cassandra_user_table;
    private String cassandra_location_table;
    private CassandraConnect cassandraConnection;
    private RedisConnect redisPool;
    private Jedis redisConnection;
    private int locationDbKeyExpiryTimeInSeconds;
    private String userSignInTypeDefault;
    private String userLoginInTypeDefault;
    private Type mapType = new TypeToken<Map<String, Object>>() {
    }.getType();
    private Gson gson = new Gson();
    private JobMetrics metrics;
    private int databaseIndex;


    public UserDataCache(Config config, JobMetrics metrics, CassandraConnect cassandraConnect, RedisConnect redisConnect) {

        super(config.getList("user.metadata.fields", Arrays.asList("usertype", "grade", "language", "subject", "state", "district", "usersignintype", "userlogintype")));
        this.metrics = metrics;
        this.databaseIndex = config.getInt("redis.userDB.index", 4);
        this.redisPool = null == redisConnect ? new RedisConnect(config) : redisConnect;
        this.redisConnection = this.redisPool.getConnection(databaseIndex);
        this.cassandra_db = config.get("middleware.cassandra.keyspace", "sunbird");
        this.cassandra_user_table = config.get("middleware.cassandra.user_table", "user");
        this.cassandra_location_table = config.get("middleware.cassandra.location_table", "location");
        List<String> cassandraHosts = Arrays.asList(config.get("middleware.cassandra.host", "127.0.0.1").split(","));
        this.cassandraConnection = null == cassandraConnect ? new CassandraConnect(cassandraHosts, config.getInt("middleware.cassandra.port", 9042)) : cassandraConnect;
        this.locationDbKeyExpiryTimeInSeconds = config.getInt("location.db.redis.key.expiry.seconds", 86400);
        this.userSignInTypeDefault = config.get("user.signin.type.default", "Anonymous");
        this.userLoginInTypeDefault = config.get("user.login.type.default", "NA");
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
            try (Jedis redisConn = redisPool.getConnection(databaseIndex)) {
                this.redisConnection = redisConn;
                userDataMap = getUserDataFromCache(userId);
            }
        }
        userDataMap = getUserSigninLoginDetails(userDataMap);
        Map<String, Object> userLocationMap;
        if (!"Anonymous".equalsIgnoreCase(userDataMap.get("usersignintype").toString())
                && !userDataMap.containsKey("state")) {
            try {
                userLocationMap = fetchFallbackUserLocationFromDB(userId);
            } catch (Exception ex) {
                metrics.incUserDBErrorCount();
                cassandraConnection.reconnectCluster();
                userLocationMap = fetchFallbackUserLocationFromDB(userId);
            }

            if (!userLocationMap.isEmpty()) {
                userDataMap.putAll(userLocationMap);
                addToCache(userId, gson.toJson(userDataMap));
            }
        }

        if (MapUtils.isEmpty(userDataMap) || userDataMap.size() <=2) {
            metrics.incNoDataCount();
        }
        return userDataMap;
    }

    private Map<String, Object> getUserSigninLoginDetails(Map<String, Object> userDataMap) {
        Map<String,Object> userMap = null!=userDataMap ? userDataMap : new HashMap<>();
        if (!userMap.containsKey("usersignintype")) {
            userMap.put("usersignintype", userSignInTypeDefault);
        }
        if (!userMap.containsKey("userlogintype"))
            userMap.put("userlogintype", userLoginInTypeDefault);
        return userMap;
    }

    private Map<String, Object> getUserDataFromCache(String userId) {
        Map<String, Object> cacheData = new HashMap<>();
        String data = redisConnection.get(userId);
        if (data != null && !data.isEmpty()) {
            cacheData = gson.fromJson(data, mapType);
        }
        return cacheData;
    }

    public Map<String, Object> fetchFallbackUserLocationFromDB(String userId) {
        // if (userId == null) return null;
        Map<String, Object> userLocation = new HashMap<>();
        List<String> locationIds = getUserOrgLocationIds(userId);
        if (locationIds != null && !locationIds.isEmpty()) {
            userLocation = getUserLocation(locationIds);
        }
        metrics.incUserDbHitCount();
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
        metrics.incUserDbHitCount();
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
