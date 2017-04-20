package org.ekstep.ep.samza.task;

import com.google.gson.reflect.TypeToken;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.cache.CacheEntry;
import org.ekstep.ep.samza.cache.CacheService;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.search.domain.Item;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.metrics.JobMetrics;
import org.ekstep.ep.samza.search.service.SearchService;
import org.ekstep.ep.samza.search.service.SearchServiceClient;
import org.ekstep.ep.samza.service.ItemDeNormalizationService;
import org.ekstep.ep.samza.service.ItemService;

public class ItemDeNormalizationTask implements StreamTask, InitableTask, WindowableTask {
    static Logger LOGGER = new Logger(ItemDeNormalizationTask.class);
    private org.ekstep.ep.samza.task.ItemDeNormalizationConfig config;
    private JobMetrics metrics;
    private ItemDeNormalizationService service;
    private ItemService itemService;

    public ItemDeNormalizationTask(Config config, TaskContext context) {
        init(config, context);
    }

    public ItemDeNormalizationTask() {
    }

    @Override
    public void init(Config config, TaskContext context) throws Exception {
        init(config, context);
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
        this.service = new ItemDeNormalizationService(this.itemService, this.config);
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector,
                        TaskCoordinator taskCoordinator) throws Exception {

        Event event = null;
        ItemDeNormalizationSource source = new ItemDeNormalizationSource(envelope);
        ItemDeNormalizationSink sink = new ItemDeNormalizationSink(collector, metrics, config);
        try {
            event = source.getEvent();
            LOGGER.debug(event.id(), "PASSING EVENT THROUGH: {}", event.getMap());
            sink.toSuccessTopic(event);
        } catch (Exception e) {
            LOGGER.error(null, "ERROR WHILE PROCESSING EVENT", e);
            if (event != null && event.getMap() != null) {
                LOGGER.error(event.id(), "ADDED FAILED EVENT TO FAILED TOPIC. EVENT: {}", event.getMap());
                sink.toFailedTopic(event);
            }
        }


    }

    @Override
    public void window(MessageCollector messageCollector, TaskCoordinator taskCoordinator) throws Exception {
        metrics.clear();
    }
}
