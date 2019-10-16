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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDataCache extends DataCache {

    private static Logger LOGGER = new Logger(UserDataCache.class);

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


    public UserDataCache(Config config, JobMetrics metrics, RedisConnect redisConnect) {

        super(config.getList("user.metadata.fields", Arrays.asList("usertype", "grade", "language", "subject", "state", "district", "usersignintype", "userlogintype")));
        this.metrics = metrics;
        this.databaseIndex = config.getInt("redis.userDB.index", 4);
        this.redisPool = null == redisConnect ? new RedisConnect(config) : redisConnect;
        this.redisConnection = this.redisPool.getConnection(databaseIndex);
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

            if (!userDataMap.isEmpty()) {
                addToCache(userId, gson.toJson(userDataMap));
            }
        if (userDataMap.size() <=2) {  //Since SigninType and LoginType are default values, incrementing no data metric only if other user details are not present
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

    private void addToCache(String userId, String userData) {
        try {
            redisConnection.set(userId, userData);
        } catch (JedisException ex) {
            LOGGER.error("", "AddLocationToCache: Unable to get connection from the " +
                    "redis connection pool. userId: " + userId, ex);
        }
    }
}
