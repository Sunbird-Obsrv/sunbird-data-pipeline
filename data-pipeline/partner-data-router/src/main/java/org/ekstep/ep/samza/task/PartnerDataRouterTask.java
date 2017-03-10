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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class PartnerDataRouterTask implements StreamTask, InitableTask, WindowableTask {
    private static List<String> validPartners;
    private String successTopicSuffix;
    private Counter messageCount;
    private CleanerFactory cleaner;
    private List<String> eventsToAllow;
    private List<String> eventsToSkip;

    static Logger LOGGER = new Logger(Event.class);

    @Override
    public void init(Config config, TaskContext context) throws Exception {
        successTopicSuffix = config.get("output.success.topic.prefix", "partner");
        String validPartnersString = config.get("valid.partners", "");
        validPartners = asList(StringUtils.split(validPartnersString, ", "));

        LOGGER.info(null, "Valid partners: {}", validPartners);

        messageCount = context
                .getMetricsRegistry()
                .newCounter(getClass().getName(), "message-count");
        eventsToSkip = getEventsToSkip(config);
        eventsToAllow = getEventsToAllow(config);
        cleaner = new CleanerFactory(eventsToAllow, eventsToSkip);
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        Event event = null;
        try {
            Map<String, Object> message = (Map<String, Object>) envelope.getMessage();
            event = getEvent(message);
            processEvent(collector, event);
            messageCount.inc();
        } catch (Exception e) {
            LOGGER.error(event.id(), "PARTNER PROCESSING FAILED", e);
            LOGGER.error(event.id(), "TODO: need a failed topic for partner job");
        }
    }

    public void processEvent(MessageCollector collector, Event event) {
        LOGGER.info(event.id(), "TS: {}", event.ts());
        LOGGER.info(event.id(), "SID: {}", event.sid());
        if (!event.belongsToAPartner()) {
            return;
        }
        event.updateType();
        String topic = String.format("%s.%s", successTopicSuffix, event.routeTo());
        LOGGER.info(event.id(), "TOPIC: {}", topic);

        if (event.getData().containsKey("ver") && event.getData().get("ver").equals("1.0")) {
            return;
        }

        if (cleaner.shouldAllowEvent(event.eid())) {
            if (cleaner.shouldSkipEvent(event.eid())) {
                return;
            }

            cleaner.clean(event.getData());
            LOGGER.info(event.id(), "CLEANED EVENT");

            SystemStream stream = new SystemStream("kafka", topic);
            collector.send(new OutgoingMessageEnvelope(stream, event.getData()));
        }
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
        return new Event(message, validPartners);
    }

    @Override
    public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        messageCount.clear();

    }
}
