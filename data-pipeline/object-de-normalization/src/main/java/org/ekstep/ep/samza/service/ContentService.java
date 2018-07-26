package org.ekstep.ep.samza.service;

import org.ekstep.ep.samza.cache.CacheService;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.search.domain.Content;
import org.ekstep.ep.samza.search.service.SearchService;

import java.io.IOException;

public class ContentService {
    static Logger LOGGER = new Logger(ContentService.class);
    private final SearchService searchService;
    private final CacheService<String, Content> cacheService;
    private final long cacheTTL;

    public ContentService(SearchService searchService, CacheService<String, Content> cacheService, long cacheTTL) {
        this.searchService = searchService;
        this.cacheService = cacheService;
        this.cacheTTL = cacheTTL;
    }

    public Content getContent(String id, String contentId) throws IOException {
        String key = contentId + "_content";
        Content cachedContent = cacheService.get(key, cacheTTL);
        if (cachedContent != null) {
            LOGGER.info(id, "CONTENT CACHED", key);
            cachedContent.setCacheHit(true);
            return cachedContent;
        }

        LOGGER.info(id, "CONTENT NOT CACHED", key);
        loadContentAndPopulateCache(id, contentId);
        return cacheService.get(key, cacheTTL);
    }

    private void loadContentAndPopulateCache(String id, String contentId) throws IOException {
        LOGGER.info(id, "CALLING SEARCH API", contentId);
        Content content = searchService.searchContent(contentId);
        if (content != null) {
            String key = contentId + "_content";
            LOGGER.info(id, "WRITING TO CACHE", key);
            content.setCacheHit(false);
            cacheService.put(key, content);
        }
    }
}
