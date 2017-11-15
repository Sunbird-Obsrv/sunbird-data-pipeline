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
import org.ekstep.ep.samza.search.domain.Content;
import org.ekstep.ep.samza.search.domain.Item;
import org.ekstep.ep.samza.search.service.SearchService;
import org.ekstep.ep.samza.search.service.SearchServiceClient;
import org.ekstep.ep.samza.service.ContentService;
import org.ekstep.ep.samza.service.ItemService;
import org.ekstep.ep.samza.service.ObjectDeNormalizationService;
import org.ekstep.ep.samza.system.ContentDeNormStrategy;
import org.ekstep.ep.samza.system.GenericDeNormStrategy;
import org.ekstep.ep.samza.system.ItemDeNormStrategy;
import org.ekstep.ep.samza.system.Strategy;

import java.util.HashMap;

public class ObjectDeNormalizationTask implements StreamTask, InitableTask, WindowableTask {
    public static final String CONTENT = "content";
    public static final String ITEM = "item";
    public static final String GENERIC = "generic";
    static Logger LOGGER = new Logger(ObjectDeNormalizationTask.class);
    private ObjectDeNormalizationConfig config;
    private JobMetrics metrics;
    private ObjectDeNormalizationService service;
    private HashMap<String, Object> objectTaxonomy;
    private HashMap strategies = new HashMap<String,Strategy>();
    private ContentService contentService;
    private ItemService itemService;

    public ObjectDeNormalizationTask(Config config, TaskContext context, SearchService searchService,
                                     KeyValueStore<Object, Object> contentStore) {
        init(config, context, contentStore, searchService);
    }

    public ObjectDeNormalizationTask() {
    }

    @Override
    public void init(Config config, TaskContext context) throws Exception {
        init(config, context,
                (KeyValueStore<Object, Object>) context.getStore("object-store"),
                null);
    }

    private void init(Config config, TaskContext context,
                      KeyValueStore<Object, Object> contentStore, SearchService searchService) {
        this.config = new ObjectDeNormalizationConfig(config);
        metrics = new JobMetrics(context);
        objectTaxonomy = this.config.objectTaxonomy();

        CacheService<String, Content> contentCacheService = contentStore != null
                ? new CacheService<String, Content>(contentStore, new TypeToken<CacheEntry<Content>>() {
        }.getType(), metrics)
                : new CacheService<String, Content>(context, "object-store", CacheEntry.class, metrics);

        SearchService searchServiceClient =
                searchService == null
                        ? new SearchServiceClient(this.config.searchServiceEndpoint())
                        : searchService;

        this.contentService = new ContentService(searchServiceClient, contentCacheService, this.config.cacheTTL());

        // TODO: Add back when events with object type item starts coming in
//         CacheService<String, Item> itemCacheService = contentStore != null
//                ? new CacheService<String, Item>(contentStore, new TypeToken<CacheEntry<Content>>() {
//         }.getType(), metrics)
//                : new CacheService<String, Item>(context, "object-store", CacheEntry.class, metrics);



//         this.itemService = new ItemService(searchServiceClient, itemCacheService, this.config.cacheTTL());

        this.strategies.put(GENERIC,new GenericDeNormStrategy(this.contentService));
        this.strategies.put(CONTENT,new ContentDeNormStrategy(this.contentService));

 // TODO: Add back when events with object type item starts coming in
//        this.strategies.put(ITEM,new ItemDeNormStrategy(this.itemService));

        service = new ObjectDeNormalizationService(strategies, this.config);
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector,
                        TaskCoordinator taskCoordinator) throws Exception {
        ObjectDeNormalizationSource source = new ObjectDeNormalizationSource(envelope, objectTaxonomy);
        ObjectDeNormalizationSink sink = new ObjectDeNormalizationSink(collector, metrics, config);

        service.process(source, sink);
    }

    @Override
    public void window(MessageCollector messageCollector, TaskCoordinator taskCoordinator) throws Exception {
        metrics.clear();
    }
}
