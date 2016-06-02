package org.ekstep.ep.samza.task;


import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.Event;
import org.ekstep.ep.samza.cleaner.Cleaner;
import org.ekstep.ep.samza.cleaner.CleanerFactory;
import org.ekstep.ep.samza.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.text.MessageFormat.format;

public class PublicTelemetryTask implements StreamTask, InitableTask, WindowableTask {
    private static final String TAG = PublicTelemetryTask.class.getSimpleName();
    static Logger LOGGER = new Logger(PublicTelemetryTask.class);

    private String successTopic;
    private String failedTopic;
    private List<Cleaner> cleaners;

    private Counter messageCount;
    private List<String> nonPublicEvents;

    @Override
    public void init(Config config, TaskContext context) throws Exception {
        LOGGER.info(null, format("{0} INIT JOB", TAG));
        successTopic = config.get("output.success.topic.name", "telemetry.public");
        failedTopic = config.get("output.failed.topic.name", "telemetry.public.fail");
        nonPublicEvents = getNonPublicEvents(config);
        cleaners = CleanerFactory.cleaners();
        messageCount = context
                .getMetricsRegistry()
                .newCounter(getClass().getName(), "message-count");
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        Event event = null;
        try {
            event = new Event((Map<String, Object>) envelope.getMessage());
            processEvent(collector, event);
            messageCount.inc();
        } catch (Exception e) {
            LOGGER.error(event.id(), "CLEAN FAILED", e);
            LOGGER.error(event.id(), "ADDING TO EVENT FAILED TOPIC");
            collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", failedTopic), event.getMap()));
        }
    }

    private List<String> getNonPublicEvents(Config config) {
        String[] split = config.get("events.to.skip", "").split(",");
        List<String> eventsToSkip = new ArrayList<String>();
        for (String event : split) {
            eventsToSkip.add(event.trim().toUpperCase());
        }
        return eventsToSkip;
    }

    void processEvent(MessageCollector collector, Event event) {
        LOGGER.info(event.id(), "CLEAN EVENT", event.getMap());

        if (nonPublicEvents.contains(event.eid().toUpperCase())) {
            LOGGER.info(event.id(), "SKIPPING EVENT");
            return;
        }

        for (Cleaner cleaner : cleaners) {
            cleaner.clean(event.getMap());
        }
        LOGGER.info(event.id(), "CLEANED EVENT");
        collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", successTopic), event.getMap()));
    }

    @Override
    public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        messageCount.clear();
    }
}
