package org.ekstep.ep.samza.util;

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
import java.util.Map;

public class UserDataCache extends DataCache {

    private static Logger LOGGER = new Logger(UserDataCache.class);

    private RedisConnect redisPool;
    private Jedis redisConnection;
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
        this.userSignInTypeDefault = config.get("user.signin.type.default", "Anonymous");
        this.userLoginInTypeDefault = config.get("user.login.type.default", "NA");
    }

    public Map<String, Object> getUserData(String userId) {
        if ("anonymous".equalsIgnoreCase(userId)) return null;
        Map<String, Object> userDataMap;
        try {
            userDataMap = getUserDataFromCache(userId);
        } catch (JedisException ex) {
            this.redisConnection.close();
            this.redisConnection = redisPool.getConnection(databaseIndex);
            userDataMap = getUserDataFromCache(userId);
        }
        if (null != userDataMap && !userDataMap.isEmpty()) {
            userDataMap.keySet().retainAll(this.fieldsList);
            metrics.incUserCacheHitCount();
        }
        userDataMap = getUserSigninLoginDetails(userDataMap);

        if (userDataMap.size() <= 2) {  //Since SigninType and LoginType are default values, incrementing no data metric only if other user details are not present
            metrics.incEmptyCacheValueCounter();
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
}
