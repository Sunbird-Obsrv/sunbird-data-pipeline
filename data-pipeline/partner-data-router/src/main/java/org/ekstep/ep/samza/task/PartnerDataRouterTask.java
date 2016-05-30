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

import java.util.List;
import java.util.Map;

public class PartnerDataRouterTask implements StreamTask, InitableTask, WindowableTask {
    private String successTopicSuffix;
    private Counter messageCount;
    private List<Cleaner> cleaners;

    @Override
    public void init(Config config, TaskContext context) throws Exception {
        successTopicSuffix = config.get("output.success.topic.prefix", "partner");
        messageCount = context
                .getMetricsRegistry()
                .newCounter(getClass().getName(), "message-count");
        cleaners = CleanerFactory.cleaners();
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        Map<String, Object> message = (Map<String, Object>) envelope.getMessage();
        System.out.println("ts: " + message.get("ts"));
        System.out.println("sid: " + message.get("sid"));
        Event event = getEvent(message);
        if(!event.belongsToAPartner()){
            return;
        }
        event.updateType();
        String topic = String.format("%s.%s", successTopicSuffix, event.routeTo());
        System.out.println("TOPIC:" + topic);
        for (Cleaner cleaner : cleaners) {
            cleaner.clean(event.getData());
        }
        SystemStream stream = new SystemStream("kafka", topic);
        collector.send(new OutgoingMessageEnvelope(stream, event.getData()));
        messageCount.inc();

    }

    protected Event getEvent(Map<String, Object> message) {
        return new Event(message);
    }

    @Override
    public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        messageCount.clear();

    }
}
