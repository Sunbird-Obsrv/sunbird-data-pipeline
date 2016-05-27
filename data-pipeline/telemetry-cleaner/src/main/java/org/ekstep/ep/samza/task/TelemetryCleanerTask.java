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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static java.text.MessageFormat.format;

public class TelemetryCleanerTask implements StreamTask, InitableTask, WindowableTask{
    private static final String TAG = TelemetryCleanerTask.class.getSimpleName();
    static Logger LOGGER = LoggerFactory.getLogger(TelemetryCleanerTask.class);

    private String successTopic;
    private String failedTopic;
    private List<Cleaner> cleaners;

    private Counter messageCount;

    @Override
    public void init(Config config, TaskContext context) throws Exception {
        LOGGER.info(format("{0} INIT JOB", TAG));
        successTopic = config.get("output.success.topic.name", "telemetry.public");
        failedTopic = config.get("output.failed.topic.name", "telemetry.public.fail");
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
            LOGGER.error(format("{0} CLEAN FAILED", TAG), e);
            if (event != null) {
                LOGGER.error(format("{0} ADDING TO EVENT FAILED TOPIC. {1}", TAG, event.getMap()), e);
                collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", failedTopic), event.getMap()));
            }
        }
    }

    private void processEvent(MessageCollector collector, Event event) {
        LOGGER.info(format("{0} CLEAN EVENT {1}", TAG, event.getMap()));
        for (Cleaner cleaner : cleaners) {
            cleaner.clean(event.getMap());
        }
        LOGGER.info(format("{0} CLEANED EVENT {1}", TAG, event.getMap()));
        collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", successTopic), event.getMap()));
    }

    @Override
    public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        messageCount.clear();
    }
}
