package org.ekstep.ep.samza.task;

import org.apache.commons.lang.StringUtils;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.Event;
import org.ekstep.ep.samza.cleaner.CleanerFactory;
import org.ekstep.ep.samza.logger.Logger;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class PartnerDataRouterTask implements StreamTask, InitableTask, WindowableTask {
    private Counter messageCount;
    private CleanerFactory cleaner;
    private List<String> eventsToAllow;
    private String successTopic;
    private String failedTopic;

    private List<String> eventsToSkip;
    static Logger LOGGER = new Logger(Event.class);


    @Override
    public void init(Config config, TaskContext context) throws Exception {
        successTopic = config.get("output.success.topic.name", "partners");
        failedTopic = config.get("output.failed.topic.name", "partners.fail");
        messageCount = context
                .getMetricsRegistry()
                .newCounter(getClass().getName(), "message-count");
        eventsToSkip = getEventsToSkip(config);
        eventsToAllow = getEventsToAllow(config);
        cleaner = new CleanerFactory(eventsToAllow, eventsToSkip);
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {
        Event event = getEvent((Map<String, Object>) envelope.getMessage());

        try {
            processEvent(collector, event);
            messageCount.inc();
        } catch (Exception e) {
            sendToFailed(collector,event);
            LOGGER.error(event.id(), "PARTNER PROCESSING FAILED", e);
            LOGGER.error(event.id(), "TODO: need a failed topic for partner job");
        }
    }

    public void processEvent(MessageCollector collector, Event event) throws Exception {
        if (cleaner.shouldAllowEvent(event.eid())) {
            if (cleaner.shouldSkipEvent(event.eid())) {
                return;
            }
            cleaner.clean(event.getData());
            LOGGER.info(event.id(), "CLEANED EVENT");
        }

        if (event.getData().containsKey("ver") && event.getData().get("ver").equals("1.0")) {
            return;
        }

        if (!event.belongsToAPartner()) {
            return;
        }

        event.updateType();
        event.updateMetadata();
        sendToSuccess(collector, event);
    }

    public void sendToSuccess(MessageCollector collector, Event event) {
        collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", successTopic), event.getData()));
    }

    public void sendToFailed(MessageCollector collector, Event event) {
        collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", failedTopic), event.getData()));
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

    protected Event getEvent(Map<String, Object> message) {
        return new Event(message);
    }

    @Override
    public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        messageCount.clear();
    }
}
