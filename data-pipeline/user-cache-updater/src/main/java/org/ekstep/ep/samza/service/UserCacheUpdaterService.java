package org.ekstep.ep.samza.service;

import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.exceptions.DriverException;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.apache.samza.config.Config;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.task.UserCacheUpdaterSink;
import org.ekstep.ep.samza.task.UserCacheUpdaterSource;
import org.ekstep.ep.samza.task.UserCacheUpdaterConfig;
import org.ekstep.ep.samza.util.CassandraConnect;
import org.ekstep.ep.samza.util.RedisConnect;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.lang.reflect.Type;
import java.util.*;

public class UserCacheUpdaterService {

    private static Logger LOGGER = new Logger(UserCacheUpdaterService.class);
    private RedisConnect redisConnect;
    private CassandraConnect cassandraConnection;
    private Jedis userDataStoreConnection;
    private JobMetrics metrics;
    private UserCacheUpdaterConfig userCacheUpdaterConfig;
    private int userStoreDb;
    private Gson gson = new Gson();
    private Type mapType = new TypeToken<Map<String, Object>>() {
    }.getType();

    public UserCacheUpdaterService(UserCacheUpdaterConfig config, RedisConnect redisConnect, CassandraConnect cassandraConnect, JobMetrics metrics) {
        this.redisConnect = redisConnect;
        this.metrics = metrics;
        userCacheUpdaterConfig = config;
        this.userStoreDb = config.userStoreDb();
        this.userDataStoreConnection = redisConnect.getConnection(userStoreDb);
        List<String> cassandraHosts = Arrays.asList(config.cassandra_middleware_host().split(","));
        this.cassandraConnection = null == cassandraConnect ? new CassandraConnect(cassandraHosts, config.cassandra_middleware_port()) : cassandraConnect;
    }

    public void process(UserCacheUpdaterSource source, UserCacheUpdaterSink sink) {
        try {
            Event event = source.getEvent();
            String userId = event.objectUserId();
            if (null != userId ) {
                updateUserCache(event, userId, sink);
            }
            else { sink.markSkipped(); }
        } catch (JsonSyntaxException e) {
            LOGGER.error(null, "INVALID EVENT: " + source.getMessage());
            sink.error();
        }
    }

    public Map<String, Object> updateUserCache(Event event, String userId, UserCacheUpdaterSink sink) {
        Map<String, Object> userCacheData = new HashMap<>();
        try {
            String userState = event.getUserStateValue();
            if("Update".equals(userState)) {
                String data = userDataStoreConnection.get(userId);
                if (data != null && !data.isEmpty()) {
                    userCacheData = gson.fromJson(data, mapType);

                }
                userCacheData.putAll(getUserLogininType(event));
                ArrayList<String> userUpdatedList = event.getUserMetdataUpdatedList();
                if (userCacheData.containsKey("usersignintype") && !"Anonymous".equals(userCacheData.get("usersignintype"))
                        && null != userUpdatedList && !userUpdatedList.isEmpty()) {

                    List<Row> userDetails = fetchFallbackDetailsFromDB(userId, userCacheUpdaterConfig.cassandra_user_table());
                    Map<String, Object> userMetadataInfoMap = getUserMetaDataInfo(userDetails);

                    if (!userMetadataInfoMap.isEmpty())
                        userCacheData.putAll(userMetadataInfoMap);

                    if (userUpdatedList.contains("id") && null != userCacheData.get("locationids")) {
                        List<String> locationIds = (List<String>) userCacheData.get("locationids");

                        List<Row> locationDetails = fetchFallbackDetailsFromDB(locationIds, userCacheUpdaterConfig.cassandra_location_table());
                        Map<String, Object> userLocationMap = getLocationInfo(locationDetails);

                        if (!userLocationMap.isEmpty())
                            userCacheData.putAll(userLocationMap);
                    }
                }
            }
            else if("Create".equals(userState)) {
                userCacheData.putAll(getUserSigninType(event));
            }

            if (!userCacheData.isEmpty()) {
                redisConnect.addJsonToCache(userId, gson.toJson(userCacheData), userDataStoreConnection);
                sink.success();
            }

        } catch (JedisException ex) {
            sink.error();
            LOGGER.error("", "Exception when adding to user redis cache", ex);
            redisConnect.resetConnection();
            try (Jedis redisConn = redisConnect.getConnection(userStoreDb)) {
                this.userDataStoreConnection = redisConn;
                if (null != userCacheData)
                    redisConnect.addJsonToCache(userId, gson.toJson(userCacheData), userDataStoreConnection);
            }
        }
        return userCacheData;
    }

    private Map<String, String> getUserSigninType(Event event) {
        Map<String,String> userData = new HashMap<>();
        String signIn_type = null != event.getUserSignInType() ? event.getUserSignInType() : userCacheUpdaterConfig.getUserSignInTypeDefault();
        if (!(userCacheUpdaterConfig.getUserSignInTypeDefault().equalsIgnoreCase(signIn_type))) {
            if (userCacheUpdaterConfig.getUserSelfSignedInTypeList().contains(signIn_type)) {
                userData.put("usersignintype", userCacheUpdaterConfig.getUserSelfSignedKey());
            } else if (userCacheUpdaterConfig.getUserValidatedTypeList().contains(signIn_type)) {
                userData.put("usersignintype", userCacheUpdaterConfig.getUserValidatedKey());
            }
        }
        return userData;
    }

    private Map<String, String> getUserLogininType(Event event) {
        Map<String,String> userData = new HashMap<>();
        String loginIn_type = null != event.getUserLoginType() ? event.getUserLoginType() : userCacheUpdaterConfig.getUserLoginInTypeDefault();
        if (!(userCacheUpdaterConfig.getUserLoginInTypeDefault().equalsIgnoreCase(loginIn_type)))
            userData.put("userlogintype", loginIn_type);
        return userData;
    }

    private <T> List<Row> fetchFallbackDetailsFromDB(T id, String table){
        String metadataQuery = QueryBuilder.select().all()
                .from(userCacheUpdaterConfig.cassandra_db(), table)
                .where(QueryBuilder.eq("id", id))
                .toString();
        List<Row> rowSet= null;
        try {
            rowSet = cassandraConnection.find(metadataQuery);
        } catch (DriverException ex) {
            metrics.incUserDBErrorCount();
            LOGGER.error("", "Exception while fetching from db : " + ex);
        }
        metrics.incUserDbHitCount();
        return rowSet;
    }

    public Map<String, Object> getUserMetaDataInfo(List<Row> userDetails) {
        Map<String, Object> result = new HashMap<>();
        if( null != userDetails && userDetails.size() > 0) {
            Row row= userDetails.get(0);
            ColumnDefinitions columnDefinitions = row.getColumnDefinitions();
            int columnCount = columnDefinitions.size();
            for(int i=0; i<columnCount;i++){
                result.put(columnDefinitions.getName(i),row.getObject(i));
            }
        }
        return result;
    }

    public Map<String, Object> getLocationInfo(List<Row> locationDetails) {
        Map<String, Object> result = new HashMap<>();
        if(locationDetails.size() > 0) {
            locationDetails.forEach(record -> {
                String name = record.getString("name");
                String type = record.getString("type");
                if(type.toLowerCase().equals("state")) {
                    result.put("state", name);
                } else if(type.toLowerCase().equals("district")) {
                    result.put("district", name);
                }
            });
        }
        return result;
    }
}
