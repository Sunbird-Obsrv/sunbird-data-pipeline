package org.ekstep.ep.samza.task;

import com.google.gson.reflect.TypeToken;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.cache.CacheEntry;
import org.ekstep.ep.samza.cache.CacheService;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.metrics.JobMetrics;
import org.ekstep.ep.samza.search.domain.Item;
import org.ekstep.ep.samza.search.service.SearchService;
import org.ekstep.ep.samza.search.service.SearchServiceClient;
import org.ekstep.ep.samza.service.ItemDeNormalizationService;
import org.ekstep.ep.samza.service.ItemService;

import java.util.HashMap;

public class ItemDeNormalizationTask implements StreamTask, InitableTask, WindowableTask {
    static Logger LOGGER = new Logger(ItemDeNormalizationTask.class);
    private org.ekstep.ep.samza.task.ItemDeNormalizationConfig config;
    private JobMetrics metrics;
    private ItemDeNormalizationService service;
    private ItemService itemService;
    private HashMap<String, Object> itemTaxonomy;

    public ItemDeNormalizationTask(Config config, TaskContext context,
                                    SearchService searchService, KeyValueStore<Object, Object> itemStore) {
        init(config, context, itemStore, searchService);
    }

    public ItemDeNormalizationTask() {
    }

    @Override
    public void init(Config config, TaskContext context) throws Exception {
        init(config, context, (KeyValueStore<Object, Object>) context.getStore("item-store"), null);
    }

    private void init(Config config, TaskContext context,
                      KeyValueStore<Object, Object> itemStore, SearchService searchService) {
        this.config = new ItemDeNormalizationConfig(config);
        metrics = new JobMetrics(context);
        CacheService<String, Item> cacheService = itemStore != null
                ? new CacheService<String, Item>(itemStore, new TypeToken<CacheEntry<Item>>() {
        }.getType(), metrics)
                : new CacheService<String, Item>(context, "item-store", CacheEntry.class, metrics);
        SearchService searchServiceClient =
                searchService == null
                        ? new SearchServiceClient(this.config.searchServiceEndpoint())
                        : searchService;
        this.itemService = new ItemService(searchServiceClient, cacheService, this.config.cacheTTL());
        service = new ItemDeNormalizationService(this.itemService, this.config);
        itemTaxonomy = this.config.itemTaxonomy();
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector,
                        TaskCoordinator taskCoordinator) throws Exception {
        ItemDeNormalizationSource source = new ItemDeNormalizationSource(envelope, itemTaxonomy);
        ItemDeNormalizationSink sink = new ItemDeNormalizationSink(collector, metrics, config);
        service.process(source,sink);
    }

    @Override
    public void window(MessageCollector messageCollector, TaskCoordinator taskCoordinator) throws Exception {
        metrics.clear();
    }
}
