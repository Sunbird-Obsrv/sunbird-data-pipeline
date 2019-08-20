package org.ekstep.ep.samza.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.samza.config.Config;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.task.RedisUpdaterSink;
import org.ekstep.ep.samza.task.RedisUpdaterSource;
import org.ekstep.ep.samza.util.RedisConnect;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.lang.reflect.Type;
import java.util.*;

public class RedisUpdaterService {

    private static Logger LOGGER = new Logger(RedisUpdaterService.class);
    private RedisConnect redisConnect;
    private Jedis dialCodeStoreConnection;
    private Jedis contentStoreConnection;
    private Jedis userDataStoreConnection;
    private int dialCodeStoreDb;
    private int contentStoreDb;
    private int userStoreDb;
    private String auditTopic;
    private String userSignInTypeDefault;
    private String userLoginInTypeDefault;
    private List<String> userSelfSignedInTypeList;
    private List<String> userValidatedTypeList;
    private String  userSelfSignedKey;
    private String  userValidatedKey;
    private Gson gson = new Gson();
    private List<String> contentModelListTypeFields;
    private Type mapType = new TypeToken<Map<String, Object>>() {
    }.getType();

    public RedisUpdaterService(Config config, RedisConnect redisConnect) {
        this.redisConnect = redisConnect;
        this.userStoreDb = config.getInt("redis.database.userStore.id", 4);
        this.contentStoreDb = config.getInt("redis.database.contentStore.id", 5);
        this.dialCodeStoreDb = config.getInt("redis.database.dialCodeStore.id", 6);
        this.contentStoreConnection = redisConnect.getConnection(contentStoreDb);
        this.dialCodeStoreConnection = redisConnect.getConnection(dialCodeStoreDb);
        this.userDataStoreConnection = redisConnect.getConnection(userStoreDb);
        this.contentModelListTypeFields = config.getList("contentModel.fields.listType", new ArrayList<>());
        this.auditTopic = config.get("input.audit.topic.name", "telemetry.audit");
        this.userSignInTypeDefault = config.get("user.signin.type.default", "Anonymous");
        this.userLoginInTypeDefault = config.get("user.login.type.default", "NA");
        this.userSelfSignedInTypeList = config.getList("user.selfsignedin.typeList", Arrays.asList("google", "self"));
        this.userValidatedTypeList = config.getList("user.validated.typeList", Arrays.asList("sso"));
        this.userSelfSignedKey = config.get("user.self-siginin.key", "Self-Signed-In");
        this.userValidatedKey = config.get("user.valid.key", "Validated");
    }

    public void process(RedisUpdaterSource source, RedisUpdaterSink sink) {

        if (auditTopic.equalsIgnoreCase(source.getSystemStreamPartition().getStream())) {
            Event event = source.getEvent();
            String userId = event.objectUserId();
            if(null != userId)
            {
                updateUserCache(event,userId);
            }
            return;
        } else {
            Map<String, Object> message = source.getMap();
            String nodeUniqueId = (String) message.get("nodeUniqueId");
            String objectType = (String) message.get("objectType");
            if (nodeUniqueId == null || objectType == null) return;
            LOGGER.info("", "processing event for nodeUniqueId: " + nodeUniqueId);
            if (!nodeUniqueId.isEmpty() && objectType.equalsIgnoreCase("DialCode")) {
                updateDialCodeCache(message, sink);
            } else if (!nodeUniqueId.isEmpty()) {
                updateContentCache(message, sink);
            }
        }
    }

    private void updateDialCodeCache(Map<String, Object> message, RedisUpdaterSink sink) {
        String nodeUniqueId = (String) message.get("nodeUniqueId");
        Map<String, Object> parsedData = null;
        try {
            String contentNode = dialCodeStoreConnection.get(nodeUniqueId);
            if (contentNode != null) {
                Type type = new TypeToken<Map<String, Object>>() {
                }.getType();
                parsedData = gson.fromJson(contentNode, type);
            } else {
                parsedData = new HashMap<>();
            }
            Map<String, Object> newProperties = extractProperties(message);
            parsedData.putAll(newProperties);
            if (parsedData.size() > 0) {
                parsedData.values().removeAll(Collections.singleton(null));
                parsedData.values().removeAll(Collections.singleton(""));
                addToCache(nodeUniqueId, gson.toJson(parsedData), dialCodeStoreConnection);
                sink.success();
            }
        } catch (JedisException ex) {
            sink.error();
            LOGGER.error("", "Exception when adding to dialcode redis cache", ex);
            redisConnect.resetConnection();
            try (Jedis redisConn = redisConnect.getConnection(dialCodeStoreDb)) {
                this.dialCodeStoreConnection = redisConn;
                if (null != parsedData)
                    addToCache(nodeUniqueId, gson.toJson(parsedData), dialCodeStoreConnection);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void updateContentCache(Map<String, Object> message, RedisUpdaterSink sink) {
        String nodeUniqueId = (String) message.get("nodeUniqueId");
        Map<String, Object> parsedData = null;
        Map<String, List<String>> listTypeFields = new HashMap();
        try {
            String contentNode = contentStoreConnection.get(nodeUniqueId);
            if (contentNode == null) {
                parsedData = new HashMap<>();
            } else {
                Type type = new TypeToken<Map<String, Object>>() {
                }.getType();
                parsedData = gson.fromJson(contentNode, type);
            }
            Map<String, Object> newProperties = extractProperties(message);
            for (Map.Entry<String, Object> entry : newProperties.entrySet()) {
                if (contentModelListTypeFields.contains(entry.getKey())
                        && entry.getValue() instanceof String
                        && !((String) entry.getValue()).isEmpty()
                ) {
                    String str = (String) entry.getValue();
                    List<String> value = toList(str);
                    listTypeFields.put(entry.getKey(), value);
                    LOGGER.info("", "type cast to List for field: " + entry.getKey() + " ,nodeUniqueId: " + nodeUniqueId);
                }
            }
            if (!listTypeFields.isEmpty()) {
                newProperties.putAll(listTypeFields);
            }
            parsedData.putAll(newProperties);
            if (parsedData.size() > 0) {
                parsedData.values().removeAll(Collections.singleton(null));
                parsedData.values().removeAll(Collections.singleton(""));
                addToCache(nodeUniqueId, gson.toJson(parsedData), contentStoreConnection);
                sink.success();
            }
        } catch (JedisException ex) {
            sink.error();
            LOGGER.error("", "Exception when adding to content store redis cache", ex);
            redisConnect.resetConnection();
            try (Jedis redisConn = redisConnect.getConnection(contentStoreDb)) {
                this.contentStoreConnection = redisConn;
                if (null != parsedData)
                    addToCache(nodeUniqueId, gson.toJson(parsedData), contentStoreConnection);
            }
        }
    }

    public void updateUserCache(Event event, String userId) {
        Map<String, Object> cacheData = new HashMap<>();
        try {
            String data = userDataStoreConnection.get(userId);
            if (data != null && !data.isEmpty()) {
                cacheData = gson.fromJson(data, mapType);
            }
                cacheData.putAll(getUserSignandLoginType(event));
            if(!cacheData.isEmpty())
                addToCache(userId, gson.toJson(cacheData), userDataStoreConnection);

        } catch (JedisException ex) {
            LOGGER.error("", "Exception when adding to user redis cache", ex);
            redisConnect.resetConnection();
            try (Jedis redisConn = redisConnect.getConnection(userStoreDb)) {
                this.dialCodeStoreConnection = redisConn;
                if (null != cacheData)
                    addToCache(userId, gson.toJson(cacheData), userDataStoreConnection);
            }
        }
    }

    private Map<String,String> getUserSignandLoginType(Event event) {

        Map<String,String> userData = new HashMap<>();
        String signIn_type = null != event.getUserSignInType() ? event.getUserSignInType() : userSignInTypeDefault;
        String loginIn_type = null != event.getUserLoginType() ? event.getUserLoginType() : userLoginInTypeDefault;
        if (!(userSignInTypeDefault.equalsIgnoreCase(signIn_type) && userLoginInTypeDefault.equalsIgnoreCase(loginIn_type))) {
            if (userSelfSignedInTypeList.contains(signIn_type)) {
                userData.put("usersignintype", userSelfSignedKey);
            } else if (userValidatedTypeList.contains(signIn_type)) {
                userData.put("usersignintype", userValidatedKey);
            } else {
                userData.put("usersignintype", signIn_type);
            }
            userData.put("userlogintype", loginIn_type);
        }
        return  userData;
    }


    private List<String> toList(String value) {
        if (value != null) {
            List<String> val = new ArrayList<>();
            val.add(value);
            return val;
        } else {
            return new ArrayList<>();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> extractProperties(Map<String, Object> message) {
        Map<String, Object> properties = new HashMap<>();
        Map transactionData = (Map) message.get("transactionData");
        if (transactionData != null) {
            Map<String, Object> addedProperties = (Map<String, Object>) transactionData.get("properties");
            if (addedProperties != null && !addedProperties.isEmpty()) {
                for (Map.Entry<String, Object> propertyMap : addedProperties.entrySet()) {
                    if (propertyMap != null && propertyMap.getKey() != null) {
                        String propertyName = propertyMap.getKey();
                        Object propertyNewValue = ((Map<String, Object>) propertyMap.getValue()).get("nv");
                        properties.put(propertyName, propertyNewValue);
                    }
                }
            }
        }
        return properties;
    }

    private void addToCache(String key, String value, Jedis redisConnection) {
        if (key != null && !key.isEmpty() && null != value && !value.isEmpty()) {
            redisConnection.set(key, value);
        }
    }
}
