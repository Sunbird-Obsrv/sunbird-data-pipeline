package org.ekstep.ep.samza.service;

import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.Clause;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.task.UserCacheUpdaterSink;
import org.ekstep.ep.samza.task.UserCacheUpdaterSource;
import org.ekstep.ep.samza.task.UserCacheUpdaterConfig;
import org.ekstep.ep.samza.util.CassandraConnect;
import org.ekstep.ep.samza.util.RedisConnect;
import org.ekstep.ep.samza.core.BaseCacheUpdaterService;

import java.lang.reflect.Type;
import java.util.*;

public class UserCacheUpdaterService extends BaseCacheUpdaterService {

    private static Logger LOGGER = new Logger(UserCacheUpdaterService.class);
    private JobMetrics metrics;
    private UserCacheUpdaterConfig userCacheUpdaterConfig;
    private int userStoreDb;
    private Gson gson = new Gson();
    private Type mapType = new TypeToken<Map<String, Object>>() {
    }.getType();

    public UserCacheUpdaterService(UserCacheUpdaterConfig config, RedisConnect redisConnect, CassandraConnect cassandraConnect, JobMetrics metrics) {
        super(redisConnect, cassandraConnect);
        this.metrics = metrics;
        userCacheUpdaterConfig = config;
        this.userStoreDb = config.userStoreDb();
    }

    public void process(UserCacheUpdaterSource source, UserCacheUpdaterSink sink) {
        try {
            Event event = source.getEvent();
            String userId = event.objectUserId();
            if (null != userId ) {
                updateUserCache(event, userId, sink);
            } else { 
            	sink.markSkipped(); 
            }
        } catch (JsonSyntaxException e) {
        	e.printStackTrace();
            LOGGER.error(null, "INVALID EVENT: " + source.getMessage());
            sink.error();
        }
    }

    public Map<String, Object> updateUserCache(Event event, String userId, UserCacheUpdaterSink sink) {
        Map<String, Object> userCacheData = new HashMap<>();
        String userState = event.getUserStateValue();
        if("Update".equals(userState)) {
            String data = readFromCache(userId, userStoreDb);
            if (data != null && !data.isEmpty()) {
                userCacheData = gson.fromJson(data, mapType);
            }
            userCacheData.putAll(getUserLoginType(event));
            ArrayList<String> userUpdatedList = event.getUserMetdataUpdatedList();
            if (userCacheData.containsKey("usersignintype") && !"Anonymous".equals(userCacheData.get("usersignintype"))
                    && null != userUpdatedList && !userUpdatedList.isEmpty()) {
                Clause userDataClause = QueryBuilder.eq("id",userId);
                List<Row> userDetails = readFromCassandra(userCacheUpdaterConfig.cassandra_db(), userCacheUpdaterConfig.cassandra_user_table(), userDataClause);
                Map<String, Object> userMetadataInfoMap = getUserMetaDataInfo(userDetails);

                if (!userMetadataInfoMap.isEmpty())
                    userCacheData.putAll(userMetadataInfoMap);

                if (userUpdatedList.contains("id") && null != userCacheData.get("locationids")) {
                    List<String> locationIds = (List<String>) userCacheData.get("locationids");
                    Clause locationDataClause = QueryBuilder.in("id",locationIds);

                    List<Row> locationDetails = readFromCassandra(userCacheUpdaterConfig.cassandra_db(), userCacheUpdaterConfig.cassandra_location_table(), locationDataClause);
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
            addToCache(userId, gson.toJson(userCacheData), userStoreDb);
            LOGGER.info(userId, "Updated in cache");
            sink.success();
        } else {
        	sink.markSkipped();
        }
        return userCacheData;
    }

    private Map<String, String> getUserSigninType(Event event) {
        Map<String,String> userData = new HashMap<>();
        String signInType = null != event.getUserSignInType() ? event.getUserSignInType() : userCacheUpdaterConfig.getUserSignInTypeDefault();
        if (!(userCacheUpdaterConfig.getUserSignInTypeDefault().equalsIgnoreCase(signInType))) {
            if (userCacheUpdaterConfig.getUserSelfSignedInTypeList().contains(signInType)) {
                userData.put("usersignintype", userCacheUpdaterConfig.getUserSelfSignedKey());
            } else if (userCacheUpdaterConfig.getUserValidatedTypeList().contains(signInType)) {
                userData.put("usersignintype", userCacheUpdaterConfig.getUserValidatedKey());
            }
        }
        return userData;
    }

    private Map<String, String> getUserLoginType(Event event) {
        Map<String,String> userData = new HashMap<>();
        String loginInType = null != event.getUserLoginType() ? event.getUserLoginType() : userCacheUpdaterConfig.getUserLoginInTypeDefault();
        if (!(userCacheUpdaterConfig.getUserLoginInTypeDefault().equalsIgnoreCase(loginInType)))
            userData.put("userlogintype", loginInType);
        return userData;
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
        if(null != locationDetails && !locationDetails.isEmpty()) {
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
