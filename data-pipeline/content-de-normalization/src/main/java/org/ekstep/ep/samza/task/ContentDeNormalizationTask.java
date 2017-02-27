package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.*;
import org.ekstep.ep.samza.cleaner.CleanerFactory;
import org.ekstep.ep.samza.external.SearchServiceClient;
import org.ekstep.ep.samza.logger.Logger;

import java.util.Map;

public class ContentDeNormalizationTask implements StreamTask, InitableTask, WindowableTask {
    static Logger LOGGER = new Logger(ContentDeNormalizationTask.class);
    private CleanerFactory cleaner;
    private CacheService cacheService;
    private ContentDeNormalizationConfig config;
    private ContentDeNormalizationMetrics metrics;


    public ContentDeNormalizationTask(Config config, TaskContext context, SearchServiceClient searchService,
                                      KeyValueStore<String, Object> contentStore) {
        init(config, context, contentStore, searchService);
    }

    public ContentDeNormalizationTask() {
    }

    private void init(Config config, TaskContext context,
                      KeyValueStore<String, Object> contentStore, SearchServiceClient searchService) {
        this.config = new ContentDeNormalizationConfig(config);
        metrics = new ContentDeNormalizationMetrics(context);
        cleaner = new CleanerFactory(this.config.eventsToAllow(), this.config.eventsToSkip());
        SearchServiceClient searchServiceClient =
                searchService == null
                        ? new SearchServiceClient(this.config.searchServiceEndpoint())
                        : searchService;
        this.cacheService = new CacheService(searchServiceClient, contentStore, this.config.cacheTTL());
    }

    @Override
    public void init(Config config, TaskContext context) throws Exception {
        init(config, context,
                (KeyValueStore<String, Object>) context.getStore("content-store"),
                null);
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector,
                        TaskCoordinator taskCoordinator) throws Exception {
        ContentDeNormalizationSink sink = new ContentDeNormalizationSink(collector, metrics, config);
        Event event = new Event((Map<String, Object>) envelope.getMessage());
        try {
            if (cleaner.shouldAllowEvent(event.getEid())) {
                String contentId = event.getContentId();
                if (contentId == null) {
                    sink.sendToSuccess(event);
                } else {
                    Content content = cacheService.deNormalizeContent(event.id(), contentId);
                    if (content != null) {
                        event.updateContent(content);
                    }
                    sink.sendToSuccess(event);
                }
            } else {
                LOGGER.info(event.id(), "SKIPPING EVENTS");
                sink.sendToSuccess(event);
            }
        } catch (Exception e) {
            LOGGER.error(event.id(), "CATCHING EXCEPTION: ", e);
            LOGGER.error(event.id(), "ADDING EVENT TO FAILED TOPIC");
            sink.sendToFailure(event);
        }
    }

    @Override
    public void window(MessageCollector messageCollector, TaskCoordinator taskCoordinator) throws Exception {
        metrics.clear();
    }
}
