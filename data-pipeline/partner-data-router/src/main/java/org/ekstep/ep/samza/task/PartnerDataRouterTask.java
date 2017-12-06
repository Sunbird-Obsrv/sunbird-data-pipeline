package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.Event;
import org.ekstep.ep.samza.cleaner.CleanerFactory;
import org.ekstep.ep.samza.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PartnerDataRouterTask implements StreamTask, InitableTask, WindowableTask {
    private Counter messageCount;
    private CleanerFactory cleaner;
    private List<String> eventsToAllow;
    private String successTopic;
    private String failedTopic;
    private String defaultChannel;

    private List<String> eventsToSkip;
    static Logger LOGGER = new Logger(Event.class);


    @Override
    public void init(Config config, TaskContext context) throws Exception {
        successTopic = config.get("output.success.topic.name", "partners");
        failedTopic = config.get("output.failed.topic.name", "partners.fail");
        defaultChannel = config.get("default.channel", "in.ekstep");
        messageCount = context
                .getMetricsRegistry()
                .newCounter(getClass().getName(), "message-count");
        eventsToSkip = getEventsToSkip(config);
        cleaner = new CleanerFactory(eventsToSkip);
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

        if (cleaner.shouldSkipEvent(event.eid())) {
            LOGGER.info(event.id(), "EVENT IN SKIPPED LIST, SKIPPING");
            return;
        }

        if(!event.isDefaultChannel(defaultChannel)){
            LOGGER.info(event.id(), "OTHER CHANNEL EVENT, SKIPPING");
            return;
        }

        if (event.isVersionOne()) {
            LOGGER.info(event.id(), "EVENT VERSION 1, SKIPPING");
            return;
        }

        if (!event.belongsToAPartner()) {
            LOGGER.info(event.id(), "EVENT DOES NOT BELONG TO A PARTNER, SKIPPING");
            return;
        }

        cleaner.clean(event.getMap());
        LOGGER.info(event.id(), "CLEANED EVENT", event.getMap());

        event.updateType();
        event.updateMetadata();
        sendToSuccess(collector, event);
    }

    public void sendToSuccess(MessageCollector collector, Event event) {
        collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", successTopic), event.getMap()));
    }

    public void sendToFailed(MessageCollector collector, Event event) {
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

    protected Event getEvent(Map<String, Object> message) {
        return new Event(message);
    }

    @Override
    public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        messageCount.clear();
    }
}
