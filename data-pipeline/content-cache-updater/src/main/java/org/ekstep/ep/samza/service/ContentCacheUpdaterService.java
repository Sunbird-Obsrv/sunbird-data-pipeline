package org.ekstep.ep.samza.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.task.*;
import org.ekstep.ep.samza.util.ContentCache;
import org.ekstep.ep.samza.util.RedisConnect;
import redis.clients.jedis.Jedis;
import org.apache.samza.config.Config;
import redis.clients.jedis.exceptions.JedisException;

import java.lang.reflect.Type;
import java.util.*;

public class ContentCacheUpdaterService {

    private static Logger LOGGER = new Logger(ContentCacheUpdaterService.class);
    private RedisConnect redisConnect;
    private Jedis dialCodeStoreConnection;
    private Jedis contentStoreConnection;
    private JobMetrics metrics;
    private ContentCache contentCache;
    private int dialCodeStoreDb;
    private int contentStoreDb;
    Jedis redisConnection;
    int storeId;
    private Gson gson = new Gson();
    Type mapType = new TypeToken<Map<String, Object>>() {
    }.getType();
    private List<String> contentModelListTypeFields;

    public ContentCacheUpdaterService(Config config, RedisConnect redisConnect, JobMetrics metrics) {
        this.redisConnect = redisConnect;
        this.metrics = metrics;
        this.contentStoreDb = config.getInt("redis.database.contentStore.id", 5);
        this.dialCodeStoreDb = config.getInt("redis.database.dialCodeStore.id", 6);
        this.contentStoreConnection = redisConnect.getConnection(contentStoreDb);
        this.dialCodeStoreConnection = redisConnect.getConnection(dialCodeStoreDb);
        this.contentModelListTypeFields = config.getList("contentModel.fields.listType", new ArrayList<>());
    }

    public void process(ContentCacheUpdaterSource source, ContentCacheUpdaterSink sink) {
        Map<String, Object> message = source.getMap();
        String nodeUniqueId = (String) message.get("nodeUniqueId");
        String objectType = (String) message.get("objectType");
        Map<String, Object> parsedData = null;
        if (nodeUniqueId == null || objectType == null || nodeUniqueId.isEmpty() || objectType.isEmpty()) {
            sink.markSkipped();
            return;
        }
        LOGGER.info("", "processing event for nodeUniqueId: " + nodeUniqueId);
        contentCache = new ContentCache();
        if (!nodeUniqueId.isEmpty()) {
            parsedData = getCacheData(message, objectType);
        }
        if(null != parsedData) {
            redisConnect.addToCache(nodeUniqueId, gson.toJson(parsedData), redisConnection, storeId);
            sink.success();
        }
    }

    public Map<String, Object> getCacheData(Map<String, Object> message, String objectType){
        String nodeUniqueId = (String) message.get("nodeUniqueId");
        Map<String, Object> parsedData = null;
        String contentNode = contentStoreConnection.get(nodeUniqueId);
        if (contentNode != null) {
            parsedData = gson.fromJson(contentNode, mapType);
        } else {
            parsedData = new HashMap<>();
        }
        Map<String, Object> newProperties = contentCache.extractProperties(message);
        if(objectType.equalsIgnoreCase("DialCode")) {
            redisConnection=dialCodeStoreConnection;
            storeId=dialCodeStoreDb;
        }
        else {
            redisConnection=contentStoreConnection;
            storeId=contentStoreDb;
            Map<String, List<String>> listTypeFields = contentCache.convertType(newProperties, contentModelListTypeFields);
            if (!listTypeFields.isEmpty()) {
                newProperties.putAll(listTypeFields);
            }
        }
        return contentCache.getParsedData(parsedData, newProperties);
    }
}
