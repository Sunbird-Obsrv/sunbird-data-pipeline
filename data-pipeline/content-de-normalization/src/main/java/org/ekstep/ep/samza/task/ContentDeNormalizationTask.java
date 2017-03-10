package org.ekstep.ep.samza.task;

import com.google.gson.reflect.TypeToken;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.cache.CacheEntry;
import org.ekstep.ep.samza.cache.ContentService;
import org.ekstep.ep.samza.cleaner.CleanerFactory;
import org.ekstep.ep.samza.domain.Content;
import org.ekstep.ep.samza.external.SearchServiceClient;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.service.CacheService;
import org.ekstep.ep.samza.service.ContentDeNormalizationService;

import java.util.HashMap;

public class ContentDeNormalizationTask implements StreamTask, InitableTask, WindowableTask {
    static Logger LOGGER = new Logger(ContentDeNormalizationTask.class);
    private CleanerFactory cleaner;
    private ContentService contentService;
    private ContentDeNormalizationConfig config;
    private ContentDeNormalizationMetrics metrics;
    private ContentDeNormalizationService service;
    private HashMap<String, Object> contentTaxonomy;


    public ContentDeNormalizationTask(Config config, TaskContext context, SearchServiceClient searchService,
                                      KeyValueStore<Object, Object> contentStore) {
        init(config, context, contentStore, searchService);
    }

    public ContentDeNormalizationTask() {
    }

    @Override
    public void init(Config config, TaskContext context) throws Exception {
        init(config, context,
                (KeyValueStore<Object, Object>) context.getStore("content-store"),
                null);
    }

    private void init(Config config, TaskContext context,
                      KeyValueStore<Object, Object> contentStore, SearchServiceClient searchService) {
        this.config = new ContentDeNormalizationConfig(config);
        metrics = new ContentDeNormalizationMetrics(context);
        cleaner = new CleanerFactory(this.config.eventsToAllow(), this.config.eventsToSkip());
        CacheService<String, Content> cacheService = contentStore != null
                ? new CacheService<String, Content>(contentStore, new TypeToken<CacheEntry<Content>>() {
        }.getType(), metrics)
                : new CacheService<String, Content>(context, "content-store", CacheEntry.class, metrics);
        SearchServiceClient searchServiceClient =
                searchService == null
                        ? new SearchServiceClient(this.config.searchServiceEndpoint())
                        : searchService;
        this.contentService = new ContentService(searchServiceClient, cacheService, this.config.cacheTTL());
        service = new ContentDeNormalizationService(cleaner, contentService, this.config);
        contentTaxonomy = this.config.contentTaxonomy();
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector,
                        TaskCoordinator taskCoordinator) throws Exception {
        ContentDeNormalizationSource source = new ContentDeNormalizationSource(envelope, contentTaxonomy);
        ContentDeNormalizationSink sink = new ContentDeNormalizationSink(collector, metrics, config);

        service.process(source, sink);
    }

    @Override
    public void window(MessageCollector messageCollector, TaskCoordinator taskCoordinator) throws Exception {
        metrics.clear();
    }
}
