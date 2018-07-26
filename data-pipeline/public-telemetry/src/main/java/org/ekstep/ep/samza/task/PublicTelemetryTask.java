package org.ekstep.ep.samza.task;


import com.google.gson.Gson;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.Event;
import org.ekstep.ep.samza.cleaner.CleanerFactory;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.core.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PublicTelemetryTask implements StreamTask, InitableTask, WindowableTask {
    static Logger LOGGER = new Logger(PublicTelemetryTask.class);

    private String successTopic;
    private String failedTopic;
    private String metricTopic;

    private JobMetrics metrics;

    private CleanerFactory cleaner;
    private List<String> nonPublicEvents;
    private List<String> publicEvents;
    private List<String> defaultChannels;


    @Override
    public void init(Config config, TaskContext context) throws Exception {
        successTopic = config.get("output.success.topic.name", "telemetry.public");
        failedTopic = config.get("output.failed.topic.name", "telemetry.public.fail");
        metricTopic = config.get("output.metrics.topic.name", "pipeline_metrics");
        defaultChannels = getDefaultChannelValues(config);
        nonPublicEvents = getNonPublicEvents(config);
        metrics = new JobMetrics(context, config.get("output.metrics.job.name", "PublicTelemetry"));
        cleaner = new CleanerFactory(nonPublicEvents);
    }


    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        Event event = null;
        try {
            event = new Event((Map<String, Object>) envelope.getMessage());
            processEvent(collector, event);
        } catch (Exception e) {
            LOGGER.error(event.id(), "CLEAN FAILED", e);
            LOGGER.error(event.id(), "ADDING TO EVENT FAILED TOPIC");
            sendToFailedTopic(collector, event);
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


    private List<String> getDefaultChannelValues(Config config) {
        String[] split = config.get("default.channel", "").split(",");
        List<String> defaultChannels = new ArrayList<String>();
        for (String event : split) {
            defaultChannels.add(event.trim());
        }
        return defaultChannels;
    }

    void processEvent(MessageCollector collector, Event event) {
        if(!event.isDefaultChannel(defaultChannels)){
            LOGGER.info(event.id(), "OTHER CHANNEL EVENT, SKIPPING");
            metrics.incSkippedCounter();
            return;
        }

        if(event.isVersionOne()){
            LOGGER.info(event.id(), "EVENT VERSION 1, SKIPPING");
            metrics.incSkippedCounter();
            return;
        }

        if (cleaner.shouldSkipEvent(event.eid())) {
            metrics.incSkippedCounter();
            return;
        }

        cleaner.clean(event.telemetry());

        LOGGER.info(event.id(), "CLEANED EVENT",event.getMap());
        sendToSuccessTopic(collector,  event);
    }

    private void sendToSuccessTopic(MessageCollector collector,  Event event) {
        collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", successTopic), event.getMap()));
        metrics.incSuccessCounter();
    }

    private void sendToFailedTopic(MessageCollector collector, Event event) {
        collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", failedTopic), event.getMap()));
        metrics.incErrorCounter();
    }

    @Override
    public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        String mEvent = metrics.collect();
        Map<String,Object> mEventMap = new Gson().fromJson(mEvent,Map.class);
        collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", metricTopic), mEventMap));
        metrics.clear();
    }
}
