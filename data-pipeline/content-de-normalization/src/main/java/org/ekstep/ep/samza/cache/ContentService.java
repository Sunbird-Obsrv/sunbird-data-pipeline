package org.ekstep.ep.samza.cache;

import org.ekstep.ep.samza.domain.Content;
import org.ekstep.ep.samza.external.SearchServiceClient;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.service.CacheService;

import java.io.IOException;

public class ContentService {
    static Logger LOGGER = new Logger(ContentService.class);
    private final SearchServiceClient searchService;
    private final CacheService<String, Content> cacheService;
    private final long cacheTTL;

    public ContentService(SearchServiceClient searchService, CacheService<String, Content> cacheService, long cacheTTL) {
        this.searchService = searchService;
        this.cacheService = cacheService;
        this.cacheTTL = cacheTTL;
    }

    public Content getContent(String id, String contentId) throws IOException {
        Content cachedContent = cacheService.get(contentId, cacheTTL);
        if (cachedContent != null) {
            LOGGER.info(id, "CONTENT CACHED", contentId);
            cachedContent.setCacheHit(true);
            return cachedContent;
        }

        LOGGER.info(id, "CONTENT NOT CACHED", contentId);
        loadContentAndPopulateCache(id, contentId);
        return cacheService.get(contentId, cacheTTL);
    }

    private void loadContentAndPopulateCache(String id, String contentId) throws IOException {
        LOGGER.info(id, "CALLING SEARCH API", contentId);
        Content content = searchService.search(contentId);
        if (content != null) {
            LOGGER.info(id, "WRITING TO CACHE", contentId);
            content.setCacheHit(false);
            cacheService.put(contentId, content);
        }
    }
}
