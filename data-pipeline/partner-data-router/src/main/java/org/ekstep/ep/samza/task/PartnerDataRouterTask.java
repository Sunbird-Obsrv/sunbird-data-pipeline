package org.ekstep.ep.samza.task;

import kafka.admin.AdminUtils;
import org.I0Itec.zkclient.ZkClient;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.Event;
import org.ekstep.ep.samza.exception.PartnerTopicNotPresentException;

import java.util.Map;

public class PartnerDataRouterTask implements StreamTask, InitableTask, WindowableTask {
    private String successTopicSuffix;
    private String zkServer;
    private Counter messageCount;

    @Override
    public void init(Config config, TaskContext context) throws Exception {
        successTopicSuffix = config.get("output.success.topic.prefix", "partner");
        zkServer = config.get("systems.kafka.consumer.zookeeper.connect", "localhost:2181");
        messageCount = context
                .getMetricsRegistry()
                .newCounter(getClass().getName(), "message-count");
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        Map<String, Object> message = (Map<String, Object>) envelope.getMessage();
        System.out.println("ts: " + (String) message.get("ts"));
        System.out.println("sid: " + (String) message.get("sid"));
        Event event = getEvent(message);
        if(!event.belongsToAPartner()){
            return;
        }
        event.updateType();
        String topic = String.format("%s.%s", successTopicSuffix, event.routeTo());
        System.out.println("TOPIC:" + topic);
//        if(!topicExists(topic))
//            throw new PartnerTopicNotPresentException(topic+" does not exists");
        SystemStream stream = new SystemStream("kafka", topic);
        collector.send(new OutgoingMessageEnvelope(stream, event.getData()));
        messageCount.inc();

    }

    protected Event getEvent(Map<String, Object> message) {
        return new Event(message);
    }

    protected boolean topicExists(String topic) {
        return AdminUtils.topicExists(new ZkClient(zkServer), topic);
    }

    @Override
    public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        messageCount.clear();

    }
}
