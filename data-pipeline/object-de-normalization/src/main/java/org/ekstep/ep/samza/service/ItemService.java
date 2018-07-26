package org.ekstep.ep.samza.service;

import org.ekstep.ep.samza.cache.CacheService;
import org.ekstep.ep.samza.core.Logger;
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
        String key = itemId + "_item";
        Item cachedItem = cacheService.get(key, cacheTTL);
        if (cachedItem != null) {
            LOGGER.info(id, "ITEM CACHED", key);
            cachedItem.setCacheHit(true);
            return cachedItem;
        }

        LOGGER.info(id, "ITEM NOT CACHED", itemId);
        loadItemAndPopulateCache(id, itemId);
        return cacheService.get(key, cacheTTL);
    }

    private void loadItemAndPopulateCache(String id, String itemId) throws IOException {
        LOGGER.info(id, "CALLING SEARCH API", itemId);
        Item item = searchService.searchItem(itemId);
        if (item != null) {
            String key = itemId + "_item";
            LOGGER.info(id, "WRITING TO CACHE", key);
            item.setCacheHit(false);
            cacheService.put(key, item);
        }
    }
}
