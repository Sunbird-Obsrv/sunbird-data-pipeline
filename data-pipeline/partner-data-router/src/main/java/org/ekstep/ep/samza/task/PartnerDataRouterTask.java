package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.Event;

import java.util.Map;

public class PartnerDataRouterTask implements StreamTask, InitableTask {

    private String successTopicSuffix;

    @Override
    public void init(Config config, TaskContext context) throws Exception {
        successTopicSuffix = config.get("output.success.topic.name", "events_with_de_normalization");
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        Map<String, Object> message = (Map<String, Object>) envelope.getMessage();
        Event event = new Event(message);
        if(!event.belongsToAPartner())
            return;
        String topic = String.format("%s.%s",successTopicSuffix,event.routeTo());
        SystemStream stream = new SystemStream("kafka", topic);
        collector.send(new OutgoingMessageEnvelope(stream,event.getData()));
    }
}
