package org.ekstep.ep.samza.service;

import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.Row;
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
            if (null != userId) {
                updateUserCache(event, userId, sink);
            }
            else { sink.error(); }
        } catch (JsonSyntaxException e) {
            LOGGER.error(null, "INVALID EVENT: " + source.getMessage());
            sink.error();
        }
    }

    public void updateUserCache(Event event, String userId, UserCacheUpdaterSink sink) {
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

                    Map<String, Object> userMetadataInfoMap = getUserMetaDataInfoFromUserDB(userId);

                    if (!userMetadataInfoMap.isEmpty())
                        userCacheData.putAll(userMetadataInfoMap);

                    if (userUpdatedList.contains("id") && null != userCacheData.get("locationids")) {
                        List<String> locationIds = (List<String>) userCacheData.get("locationids");

                        Map<String, Object> userLocationMap = getLocationDetailsFromlocationDB(userId, locationIds);

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
    }

    private Map<String, String> getUserSigninType(Event event) {
        Map<String,String> userData = new HashMap<>();
        String signIn_type = null != event.getUserSignInType() ? event.getUserSignInType() : userCacheUpdaterConfig.getUserSignInTypeDefault();
        if (!(userCacheUpdaterConfig.getUserSignInTypeDefault().equalsIgnoreCase(signIn_type))) {
            if (userCacheUpdaterConfig.getUserSelfSignedInTypeList().contains(signIn_type)) {
                userData.put("usersignintype", userCacheUpdaterConfig.getUserSelfSignedKey());
            } else if (userCacheUpdaterConfig.getUserValidatedTypeList().contains(signIn_type)) {
                userData.put("usersignintype", userCacheUpdaterConfig.getUserValidatedKey());
            } else {
                userData.put("usersignintype", signIn_type);
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

    private Map<String, Object> getUserMetaDataInfoFromUserDB(String userId) {

        Map<String, Object> userMetadataInfoMap = null;
        try {
            userMetadataInfoMap = fetchFallbackUserMetadataFromDB(userId);
        } catch (Exception ex) {
            metrics.incUserDBErrorCount();
            cassandraConnection.reconnectCluster();
            userMetadataInfoMap = fetchFallbackUserMetadataFromDB(userId);
        }

        return userMetadataInfoMap;
    }

    private Map<String, Object> getLocationDetailsFromlocationDB(String userId, List<String> locationIds) {
        Map<String, Object> userLocationMap = null;
        try {
            userLocationMap = fetchFallbackUserLocationFromDB(locationIds);
        } catch (Exception ex) {

            metrics.incUserDBErrorCount();
            cassandraConnection.reconnectCluster();
            userLocationMap = fetchFallbackUserLocationFromDB(locationIds);
        }
        return userLocationMap;
    }

    private Map<String, Object> fetchFallbackUserMetadataFromDB(String userId) {

        String MetadataQuery = QueryBuilder.select().all()
                .from(userCacheUpdaterConfig.cassandra_db(), userCacheUpdaterConfig.cassandra_user_table())
                .where(QueryBuilder.eq("id", userId))
                .toString();

        List<Row> rowSet = cassandraConnection.find(MetadataQuery);
        Map<String, Object> result = new HashMap<>();
        if(rowSet.size() > 0) {
            Row row= rowSet.get(0);
            ColumnDefinitions columnDefinitions = row.getColumnDefinitions();
            int columnCount = columnDefinitions.size();
            for(int i=0; i<columnCount;i++){
                result.put(columnDefinitions.getName(i),row.getObject(i));
            }
        }

        metrics.incUserDbHitCount();
        return result;
    }


    private Map<String, Object> fetchFallbackUserLocationFromDB(List<String> locationIds) {
        String userLocationQuery = QueryBuilder.select().all()
                .from(userCacheUpdaterConfig.cassandra_db(), userCacheUpdaterConfig.cassandra_location_table())
                .where(QueryBuilder.in("id", locationIds))
                .toString();
        List<Row> row = cassandraConnection.find(userLocationQuery);

        Map<String, Object> result = new HashMap<>();
        if(row.size() > 0) {
            row.forEach(record -> {
                String name = record.getString("name");
                String type = record.getString("type");
                if(type.toLowerCase().equals("state")) {
                    result.put("state", name);
                } else if(type.toLowerCase().equals("district")) {
                    result.put("district", name);
                }
            });
        }
        metrics.incUserDbHitCount();
        return result;
    }
}
