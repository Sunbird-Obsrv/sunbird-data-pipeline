package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.Content;
import org.ekstep.ep.samza.CacheService;
import org.ekstep.ep.samza.Event;
import org.ekstep.ep.samza.cleaner.CleanerFactory;
import org.ekstep.ep.samza.external.SearchServiceClient;
import org.ekstep.ep.samza.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContentDeNormalizationTask implements StreamTask, InitableTask, WindowableTask {
    static Logger LOGGER = new Logger(ContentDeNormalizationTask.class);
    private String successTopic;
    private String failedTopic;
    private Counter messageCount;
    private SearchServiceClient searchService;
    private KeyValueStore<String, Object> contentStore;
    private CleanerFactory cleaner;
    private long cacheTTL;
    private CacheService cacheService;


    public ContentDeNormalizationTask(Config config, TaskContext context, SearchServiceClient searchService, KeyValueStore<String, Object> contentStore) {
        init(config, context, searchService, contentStore);
    }

    public ContentDeNormalizationTask() {
    }

    private void init(Config config, TaskContext context, SearchServiceClient searchService, KeyValueStore<String, Object> contentStore) {
        successTopic = config.get("output.success.topic.name", "telemetry.content.de_normalized");
        failedTopic = config.get("output.failed.topic.name", "telemetry.content.de_normalized.fail");
        cacheTTL = Long.parseLong(config.get("content.store.ttl", "60000"));
        this.contentStore = contentStore;
        messageCount = context
                .getMetricsRegistry()
                .newCounter(getClass().getName(), "message-count");
        this.searchService = searchService;
        List<String> eventsToSkip = getEventsToSkip(config);
        List<String> eventsToAllow = getEventsToAllow(config);
        cleaner = new CleanerFactory(eventsToAllow, eventsToSkip);
        this.cacheService = new CacheService(searchService,contentStore,cacheTTL);
    }

    @Override
    public void init(Config config, TaskContext context) throws Exception {
        String searchServiceEndpoint = config.get("search.service.endpoint");
        init(config, context, new SearchServiceClient(searchServiceEndpoint), (KeyValueStore<String, Object>) context.getStore("content-store"));
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator taskCoordinator) throws Exception {
        Event event = new Event((Map<String, Object>) envelope.getMessage());
        try {
            if (cleaner.shouldAllowEvent(event.getEid())) {
                String contentId = event.getContentId();
                if (contentId == null) {
                    sendToSuccess(collector, event);
                } else {
                    Content content = cacheService.deNormalizeContent(event.id(), contentId);
                    if(content != null){
                        event.updateContent(content);
                    }
                    sendToSuccess(collector, event);
                }
            } else {
                LOGGER.info(event.id(), "SKIPPING EVENTS");
                sendToSuccess(collector, event);
            }
        } catch (Exception e) {
            LOGGER.error(event.id(), "CATCHING EXCEPTION: ", e);
            LOGGER.error(event.id(), "ADDING EVENT TO FAILED TOPIC");
            sendToFailure(collector, event);
        }
    }

    private void sendToSuccess(MessageCollector collector, Event event) {
        collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", successTopic), event.getMap()));
        messageCount.inc();
    }

    private void sendToFailure(MessageCollector collector, Event event) {
        collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", failedTopic), event.getMap()));
    }

    private List<String> getEventsToSkip(Config config) {
        String[] split = config.get("events.to.skip", "").split(",");
        List<String> eventsToSkip = new ArrayList<String>();
        for (String event : split) {
            eventsToSkip.add(event.trim().toUpperCase());
        }
        return eventsToSkip;
    }

    private List<String> getEventsToAllow(Config config) {
        String[] split = config.get("events.to.allow", "").split(",");
        List<String> eventsToAllow = new ArrayList<String>();
        for (String event : split) {
            eventsToAllow.add(event.trim().toUpperCase());
        }
        return eventsToAllow;
    }

    @Override
    public void window(MessageCollector messageCollector, TaskCoordinator taskCoordinator) throws Exception {
        messageCount.clear();
    }
}
