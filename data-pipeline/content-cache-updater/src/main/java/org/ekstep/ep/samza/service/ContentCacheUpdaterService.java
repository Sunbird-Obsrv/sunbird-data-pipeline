package org.ekstep.ep.samza.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.ekstep.ep.samza.core.BaseCacheUpdaterService;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.task.ContentCacheConfig;
import org.ekstep.ep.samza.task.ContentCacheUpdaterSink;
import org.ekstep.ep.samza.task.ContentCacheUpdaterSource;
import org.ekstep.ep.samza.util.ContentData;
import org.ekstep.ep.samza.util.RedisConnect;
import org.ekstep.ep.samza.util.RestUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentCacheUpdaterService extends BaseCacheUpdaterService {

    private static Logger LOGGER = new Logger(ContentCacheUpdaterService.class);
    private JobMetrics metrics;
    private ContentData contentData;
    private ContentCacheConfig contentCacheConfig;
    private RestUtil restUtil;
    int storeId;
    private Gson gson = new Gson();
    Type mapType = new TypeToken<Map<String, Object>>() {
    }.getType();

    public ContentCacheUpdaterService(ContentCacheConfig config, RedisConnect redisConnect, JobMetrics metrics, RestUtil restUtil) {
        super(redisConnect);
        this.metrics = metrics;
        this.contentCacheConfig = config;
        this.restUtil = restUtil;
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
            parsedData = getCacheData(message);
        }
        if (null != parsedData) {
            addToCache(nodeUniqueId, gson.toJson(parsedData), storeId);
            LOGGER.info(nodeUniqueId, "Updated in cache for type " + objectType);
            sink.success();
        }
    }

    public Map<String, Object> getCacheData(Map<String, Object> message) {
        String nodeUniqueId = (String) message.get("nodeUniqueId");
        Map<String, Object> parsedData = null;
        Map<String, Object> newProperties = contentData.extractProperties(message);
        /**
         * Responsibility:
         * 1. Take dialCode property form the transaction event.
         * 2. Check whether dialCode metadata is present in the cache or not.
         * 3. If not present in the redis
         *      1. Get the dialCode metadata using dialCode api and update to redis cache
         */
        updateDialCodeToCache((List<String>) newProperties.get("dialcodes"), contentCacheConfig.getdialCodeStoreDb(), contentCacheConfig.getDialCodeAPIUrl());
        updateDialCodeToCache(getDialCodesAsList(gson.fromJson((String) newProperties.get("reservedDialcodes"), Map.class)), contentCacheConfig.getdialCodeStoreDb(), contentCacheConfig.getDialCodeAPIUrl());
        String contentNode = null;
        storeId = contentCacheConfig.getContentStoreDb();
        contentNode = readFromCache(nodeUniqueId, storeId);
        Map<String, Object> listTypeFields = contentData.convertType(newProperties, contentCacheConfig.getContentModelListTypeFields(), contentCacheConfig.getDateFields());
        if (!listTypeFields.isEmpty()) {
            newProperties.putAll(listTypeFields);
        }
        if (contentNode != null) {
            parsedData = gson.fromJson(contentNode, mapType);
        } else {
            parsedData = new HashMap<>();
        }
        return contentData.getParsedData(parsedData, newProperties);
    }

    public void updateDialCodeToCache(List<String> dialCodeList, int storeId, String apiUrl) {
        if (null != dialCodeList) {
            dialCodeList.forEach(dialCode -> {
                if (!dialCode.isEmpty()) {
                    metrics.incDialCodesCount();
                    String metaData = readFromCache(dialCode, storeId);
                    if (null == metaData || metaData.isEmpty()) {
                        LOGGER.info("", String.format("Invoking DialCode API to fetch the metadata for this dialCode( %s )", dialCode));
                        Object dialCodeMetadata = contentData.getMetadata(apiUrl.concat(dialCode), this.restUtil, contentCacheConfig.getAuthorizationKey(), "dialcode");
                        if (null != dialCodeMetadata) {
                            metrics.incDialCodesFromApiCount();
                            LOGGER.info("", String.format("Inserting dialCode( %s ) metadata to cache system", dialCode));
                            addToCache(dialCode, gson.toJson(dialCodeMetadata), storeId);
                        } else {
                            LOGGER.info("", String.format("API did not fetched any dialCode metadata!!! %s", dialCode));
                        }
                    } else {
                        metrics.incDialCodesFromCacheCount();
                    }
                }
            });
        }
    }

    public List<String> getDialCodesAsList(Map<String, Object> dialCodesMap) {
        if (null != dialCodesMap) {
            return new ArrayList<>(dialCodesMap.keySet());
        } else {
            return new ArrayList<>();
        }
    }
}