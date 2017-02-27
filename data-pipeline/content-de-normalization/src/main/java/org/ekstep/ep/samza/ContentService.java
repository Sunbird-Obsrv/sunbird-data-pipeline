package org.ekstep.ep.samza;

import org.ekstep.ep.samza.external.SearchServiceClient;
import org.ekstep.ep.samza.logger.Logger;

import java.io.IOException;
import java.util.Date;

public class ContentService {
    static Logger LOGGER = new Logger(ContentService.class);
    private final SearchServiceClient searchService;
    private final CacheService<String, ContentCache> cacheService;
    private final long cacheTTL;

    public ContentService(SearchServiceClient searchService, CacheService<String, ContentCache> cacheService, long cacheTTL) {
        this.searchService = searchService;
        this.cacheService = cacheService;
        this.cacheTTL = cacheTTL;
    }

    public Content getContent(String id, String contentId) throws IOException {
        Content cachedContent = readContentFromCache(id, contentId);
        if (cachedContent == null) {
            cachedContent = writeContentToCache(id, contentId);
        }
        return cachedContent;
    }

    private Content readContentFromCache(String id, String contentId) throws IOException {
        ContentCache contentCache = cacheService.get(contentId);
        if (contentCache == null) {
            return null;
        }
        if (contentCache.expired(cacheTTL)) {
            return writeContentToCache(id, contentId);
        }
        return contentCache.getContent();
    }

    private Content writeContentToCache(String id, String contentId) throws IOException {
        LOGGER.info(id, "CALLING SEARCH API", contentId);
        Content content = getContentFromSearchService(contentId);
        if (content != null) {
            LOGGER.info(id, "WRITING TO CACHE", contentId);
            ContentCache contentCache = new ContentCache(content, new Date().getTime());
            cacheService.put(contentId, contentCache);
        }
        return cacheService.get(contentId).getContent();
    }

    private Content getContentFromSearchService(String contentId) throws IOException {
        return searchService.search(contentId);
    }
}
