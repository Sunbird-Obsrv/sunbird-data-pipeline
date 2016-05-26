package org.ekstep.ep.samza.task;


import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.Event;
import org.ekstep.ep.samza.cleaner.CleanerFactory;
import org.ekstep.ep.samza.cleaner.Cleaner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;

import static java.text.MessageFormat.format;

public class TelemetryCleanerTask implements StreamTask, InitableTask {
    private static final String TAG = TelemetryCleanerTask.class.getSimpleName();
    static Logger LOGGER = LoggerFactory.getLogger(TelemetryCleanerTask.class);

    private String successTopic;
    private String failedTopic;
    private ArrayList<Cleaner> cleaners;

    @Override
    public void init(Config config, TaskContext context) throws Exception {
        LOGGER.info(format("{0} INIT JOB", TAG));
        successTopic = config.get("output.success.topic.name", "public_data");
        failedTopic = config.get("output.failed.topic.name", "public_data.fail");
        cleaners = CleanerFactory.cleaners();
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        try {
            processEvent(collector, envelope);
        } catch (Exception e) {
            LOGGER.error(format("{0} CLEAN FAILED", TAG), e);
        }
    }

    private void processEvent(MessageCollector collector, IncomingMessageEnvelope envelope) {
        Event event = new Event((Map<String, Object>) envelope.getMessage());
        LOGGER.info(format("{0} CLEAN EVENT {1}", TAG, event.getMap()));
        for (Cleaner cleaner : cleaners) {
            cleaner.process(event.getMap());
        }
        LOGGER.info(format("{0} CLEANED EVENT {1}", TAG, event.getMap()));
        collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", successTopic), event.getMap()));
    }
}
