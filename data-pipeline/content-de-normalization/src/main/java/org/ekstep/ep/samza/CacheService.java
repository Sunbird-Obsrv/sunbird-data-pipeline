package org.ekstep.ep.samza;

import com.google.gson.Gson;
import org.apache.samza.storage.kv.KeyValueStore;
import org.ekstep.ep.samza.external.SearchServiceClient;
import org.ekstep.ep.samza.logger.Logger;

import java.io.IOException;
import java.util.Date;

public class CacheService {
    static Logger LOGGER = new Logger(CacheService.class);
    private final SearchServiceClient searchService;
    private final KeyValueStore<String, Object> contentStore;
    private final long cacheTTL;

    public CacheService(SearchServiceClient searchService, KeyValueStore<String, Object> contentStore, long cacheTTL) {
        this.searchService = searchService;
        this.contentStore = contentStore;
        this.cacheTTL = cacheTTL;
    }

    public Content deNormalizeContent(String id, String contentId) throws IOException {
        Content cachedContent = readContentFromCache(id, contentId);
        if(cachedContent == null){
            cachedContent =  writeContentToCache(id, contentId);
        }
        return cachedContent;
    }

    private Content readContentFromCache(String id, String contentId) throws IOException {
        String contentJson = (String) contentStore.get(contentId);
        if( contentJson != null){
            LOGGER.info(id, "CONTENT IS PRESENT IN CACHE", contentId);
            ContentCache contentCache = new Gson().fromJson(contentJson, ContentCache.class);
            if(contentCache.expired(cacheTTL)){
                LOGGER.info(id, "CACHE HAS EXPIRED", contentId);
                return writeContentToCache(id, contentId);
            }
            return contentCache.getContent();
        }
        return null;
    }

    private Content writeContentToCache(String id, String contentId) throws IOException {
        LOGGER.info(id, "CALLING SEARCH API", contentId);
        Content content = getContentFromSearchService(contentId);
        if (content != null) {
            LOGGER.info(id, "WRITING TO CACHE", contentId);
            ContentCache contentCache = new ContentCache(content, new Date().getTime());
            String contentCacheJson = new Gson().toJson(contentCache);
            contentStore.put(contentId, contentCacheJson);
        }
        return content;
    }

    private Content getContentFromSearchService(String contentId) throws IOException {
        return searchService.search(contentId);
    }
}
