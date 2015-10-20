package org.ekstep.ep.samza.task;

import com.google.gson.Gson;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.Child;
import org.ekstep.ep.samza.ChildDto;
import org.ekstep.ep.samza.Event;

import java.util.Map;

public class DeNormalizationTask implements StreamTask, InitableTask{
    private String successTopic;
    private String failedTopic;
    private KeyValueStore<String, Child> childData;
    private String dbHost;
    private String dbPort;
    private String dbUserName;
    private String dbPassword;
    private String dbSchema;
    private String retryTopic;

    private Counter messageCount;

    @Override
    public void init(Config config, TaskContext context) throws Exception {
        successTopic = config.get("output.success.topic.name", "events_with_de_normalization");
        failedTopic = config.get("output.failed.topic.name", "events_failed_de_normalization");
        retryTopic = config.get("output.retry.topic.name", "events_retry");
        childData = (KeyValueStore<String, Child>) context.getStore("de-normalization");
        dbHost = config.get("db.host");
        dbPort = config.get("db.port");
        dbUserName = config.get("db.userName");
        dbPassword = config.get("db.password");
        dbSchema = config.get("db.schema");
        messageCount = context
                .getMetricsRegistry()
                .newCounter(getClass().getName(), "message-count");
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        System.out.println("current stream"+envelope.getSystemStreamPartition().getSystemStream().toString());
        Map<String, Object> message = (Map<String, Object>) envelope.getMessage();
        System.out.println("Event"+ new Gson().toJson(message));
        ChildDto dataSource = new ChildDto(dbHost, dbPort, dbSchema, dbUserName, dbPassword);
        Event event = new Event(message, childData);
        processEvent(collector, event, dataSource);
        messageCount.inc();
    }

    public void processEvent(MessageCollector collector, Event event, ChildDto dataSource) {
        event.initialize();
        event.process(dataSource);
        populateTopic(collector,event);
    }

    private void populateTopic(MessageCollector collector, Event event) {

        if (event.canBeProcessed() && !event.isChildDataProcessed()) {
            collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", retryTopic), event.getData()));
        } else if(event.hadIssueWithDb())  {
            collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", failedTopic), event.getData()));
        } else {
            collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", successTopic), event.getData()));
        }
    }
}
