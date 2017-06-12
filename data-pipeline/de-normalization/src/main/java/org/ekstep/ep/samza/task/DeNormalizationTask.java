package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.Child;
import org.ekstep.ep.samza.Event;
import org.ekstep.ep.samza.external.UserService;
import org.ekstep.ep.samza.external.UserServiceClient;
import org.ekstep.ep.samza.logger.Logger;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeNormalizationTask implements StreamTask, InitableTask, WindowableTask {
    static Logger LOGGER = new Logger(DeNormalizationTask.class);
    private String successTopic;
    private KeyValueStore<String, Child> childData;
    private String retryTopic;

    private Counter messageCount;
    private int retryBackoffBase;
    private KeyValueStore<String, Object> retryStore;
    private String userServiceEndpoint;
    private UserService userService;
    private List<String> backendEvents;
    private List<String> eventsToSkip;

    @Override
    public void init(Config config, TaskContext context) throws Exception {
        successTopic = config.get("output.success.topic.name", "events_with_de_normalization");
        retryTopic = config.get("output.retry.topic.name", "events_retry");
        childData = (KeyValueStore<String, Child>) context.getStore("de-normalization");
        userServiceEndpoint = config.get("user.service.endpoint");
        retryBackoffBase = Integer.parseInt(config.get("retry.backoff.base"));
        messageCount = context
                .getMetricsRegistry()
                .newCounter(getClass().getName(), "message-count");
        retryStore = (KeyValueStore<String, Object>) context.getStore("retry");
        userService = new UserServiceClient(userServiceEndpoint);
        backendEvents = getBackendEvents(config);
        eventsToSkip = getEventsToSkip(config);
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {
        Event event = null;
        try {
            Map<String, Object> message = (Map<String, Object>) envelope.getMessage();
            event = new Event(message, childData, backendEvents, eventsToSkip, retryBackoffBase, retryStore);
            processEvent(collector, event, userService);
            messageCount.inc();
        } catch (Exception e) {
            LOGGER.error(null, "ERROR WHILE PROCESSING EVENT", e);
            e.printStackTrace();
            if (event != null && !event.isBackendEvent()) {
                LOGGER.error(event.id(), "ADDED FAILED EVENT TO RETRY TOPIC");
                collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", retryTopic), event.getData()));
            } else {
                LOGGER.error(event.id(), "NOT ADDING EVENT TO RETRY TOPIC AS ITS EITHER NULL OR A BACKEND EVENT");
            }
        }
    }


    private List<String> getEventsToSkip(Config config) {
        String[] split = config.get("events.to.skip", "ME_APP_SESSION_SUMMARY,ME_APP_USAGE_SUMMARY,ME_CE_SESSION_SUMMARY,ME_AUTHOR_USAGE_SUMMARY,ME_TEXTBOOK_SESSION_SUMMARY").split(",");
        List<String> eventsToSkip = new ArrayList<String>();
        for (String event : split) {
            eventsToSkip.add(event.trim().toUpperCase());
        }
        return eventsToSkip;

    }

    void processEvent(MessageCollector collector, Event event, UserService userService) {
        event.initialize();
        LOGGER.info(event.id(), " EVENT: {}", event.getData());
        if (event.canBeProcessed()) {
            if (!event.shouldBackoff()) {
                LOGGER.info(event.id(), "PROCESS");
                event.process(userService, DateTime.now());
            } else {
                LOGGER.info(event.id(), "SKIP");
                event.addLastSkippedAt(DateTime.now());
            }
        }
        populateTopic(collector, event);
    }

    private void populateTopic(MessageCollector collector, Event event) {
        if (event.shouldPutInRetry())
            collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", retryTopic), event.getData()));
        else
            collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", successTopic), event.getData()));
    }

    private List<String> getBackendEvents(Config config) {
        String[] split = config.get("backend.events", "").split(",");
        List<String> backendEvents = new ArrayList<String>();
        for (String event : split) {
            backendEvents.add(event.trim().toUpperCase());
        }
        return backendEvents;
    }

    @Override
    public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        messageCount.clear();
    }
}
