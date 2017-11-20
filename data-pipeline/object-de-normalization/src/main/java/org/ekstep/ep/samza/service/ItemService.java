package org.ekstep.ep.samza.service;

import org.ekstep.ep.samza.cache.CacheService;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.search.domain.Item;
import org.ekstep.ep.samza.search.service.SearchService;

import java.io.IOException;

public class ItemService {
    static Logger LOGGER = new Logger(ItemService.class);
    private final SearchService searchService;
    private final CacheService<String, Item> cacheService;
    private final long cacheTTL;

    public ItemService(SearchService searchService, CacheService<String, Item> cacheService, long cacheTTL) {
        this.searchService = searchService;
        this.cacheService = cacheService;
        this.cacheTTL = cacheTTL;
    }

    public Item getItem(String id, String itemId) throws IOException {
        Item cachedItem = cacheService.get(itemId, cacheTTL);
        if (cachedItem != null) {
            LOGGER.info(id, "ITEM CACHED", itemId);
            cachedItem.setCacheHit(true);
            return cachedItem;
        }

        LOGGER.info(id, "ITEM NOT CACHED", itemId);
        loadItemAndPopulateCache(id, itemId);
        return cacheService.get(itemId, cacheTTL);
    }

    private void loadItemAndPopulateCache(String id, String itemId) throws IOException {
        LOGGER.info(id, "CALLING SEARCH API", itemId);
        Item item = searchService.searchItem(itemId);
        if (item != null) {
            LOGGER.info(id, "WRITING TO CACHE", itemId);
            item.setCacheHit(false);
            cacheService.put(itemId, item);
        }
    }
}
