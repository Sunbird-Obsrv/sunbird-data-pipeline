package org.ekstep.ep.samza.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.task.*;
import org.ekstep.ep.samza.util.ContentData;
import org.ekstep.ep.samza.util.RedisConnect;
import org.ekstep.ep.samza.util.BaseUpdater;
import org.apache.samza.config.Config;

import java.lang.reflect.Type;
import java.util.*;

public class ContentCacheUpdaterService extends BaseUpdater {

    private static Logger LOGGER = new Logger(ContentCacheUpdaterService.class);
    private JobMetrics metrics;
    private ContentData contentData;
    private ContentCacheConfig contentCacheConfig;
    int storeId;
    private Gson gson = new Gson();
    Type mapType = new TypeToken<Map<String, Object>>() {
    }.getType();

    public ContentCacheUpdaterService(ContentCacheConfig config, RedisConnect redisConnect, JobMetrics metrics) {
        super(redisConnect);
        this.metrics = metrics;
        this.contentCacheConfig = config;
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
        contentData = new ContentData();
        if (!nodeUniqueId.isEmpty()) {
            parsedData = getCacheData(message, objectType);
        }
        if(null != parsedData) {
            addToCache(nodeUniqueId, gson.toJson(parsedData), storeId);
            sink.success();
        }
    }

    public Map<String, Object> getCacheData(Map<String, Object> message, String objectType){
        String nodeUniqueId = (String) message.get("nodeUniqueId");
        Map<String, Object> parsedData = null;
        Map<String, Object> newProperties = contentData.extractProperties(message);
        String contentNode = null;

        if(objectType.equalsIgnoreCase("DialCode")) {
            storeId=contentCacheConfig.getdialCodeStoreDb();
            contentNode = readFromCache(nodeUniqueId, storeId);
        }
        else {
            storeId=contentCacheConfig.getContentStoreDb();
            contentNode = readFromCache(nodeUniqueId, storeId);
            Map<String, Object> listTypeFields = contentData.convertType(newProperties, contentCacheConfig.getContentModelListTypeFields(), contentCacheConfig.getDateFields());
            if (!listTypeFields.isEmpty()) {
                newProperties.putAll(listTypeFields);
            }
        }
        if (contentNode != null) {
            parsedData = gson.fromJson(contentNode, mapType);
        } else {
            parsedData = new HashMap<>();
        }
        return contentData.getParsedData(parsedData, newProperties);
    }
}
