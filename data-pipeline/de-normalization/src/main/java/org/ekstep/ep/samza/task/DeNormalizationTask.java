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
    private static final String RETRY_BACKOFF_BASE_DEFAULT = "10";
    private static final String RETRY_BACKOFF_LIMIT_DEFAULT = "4";
    private String successTopic;
    private String failedTopic;
    private KeyValueStore<String, Child> childData;
    private String retryTopic;

    private Counter messageCount;
    private int retryBackoffBase;
    private int retryBackoffLimit;
    private KeyValueStore<String, Object> retryStore;
    private String userServiceEndpoint;
    private UserService userService;
    private List<String> backendEvents;

    @Override
    public void init(Config config, TaskContext context) throws Exception {
        successTopic = config.get("output.success.topic.name", "events_with_de_normalization");
        failedTopic = config.get("output.failed.topic.name", "events_failed_de_normalization");
        retryTopic = config.get("output.retry.topic.name", "events_retry");
        childData = (KeyValueStore<String, Child>) context.getStore("de-normalization");
        userServiceEndpoint = config.get("user.service.endpoint");
        retryBackoffBase = Integer.parseInt(config.get("retry.backoff.base"));
        retryBackoffLimit = Integer.parseInt(config.get("retry.backoff.limit"));
        messageCount = context
                .getMetricsRegistry()
                .newCounter(getClass().getName(), "message-count");
        retryStore = (KeyValueStore<String, Object>) context.getStore("retry");
        userService = new UserServiceClient(userServiceEndpoint);
        backendEvents = getBackendEvents(config);
    }


    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {
        Event event = null;
        try {
            Map<String, Object> message = (Map<String, Object>) envelope.getMessage();
            event = new Event(message, childData, backendEvents);
            processEvent(collector, event, userService);
            messageCount.inc();
        } catch (Exception e) {
            LOGGER.error(event.id(), "ERROR WHILE PROCESSING EVENT", e);
            if (event != null) {
                LOGGER.error(event.id(), "ADDED FAILED EVENT TO RETRY TOPIC");
                collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", retryTopic), event.getData()));
            }
        }
    }

    void processEvent(MessageCollector collector, Event event, UserService userService) {
        event.initialize(retryBackoffBase, retryBackoffLimit, retryStore);
        LOGGER.info(event.id(), " EVENT: {}", event.getMap());
        if(event.canBeProcessed()) {
            if (!event.isSkipped()) {
                LOGGER.info(event.id(), "PROCESS");
                event.process(userService, DateTime.now());
            } else {
                LOGGER.info(event.id(), "SKIP");
                event.addLastSkippedAt(DateTime.now());
            }
        }
        if(event.isBackendEvent()){
            event.setBackendTrue();
        }
        populateTopic(collector, event);
    }

    private void populateTopic(MessageCollector collector, Event event) {

        boolean childDataNotProcessed = event.canBeProcessed() && !event.isProcessed();
        boolean hadProblemWithDb = event.hadIssueWithDb();
        if (childDataNotProcessed || hadProblemWithDb)
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
