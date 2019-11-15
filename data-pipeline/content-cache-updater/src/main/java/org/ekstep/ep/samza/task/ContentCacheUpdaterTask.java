package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.util.RedisConnect;
import org.ekstep.ep.samza.service.ContentCacheUpdaterService;

public class ContentCacheUpdaterTask implements StreamTask, InitableTask, WindowableTask {

    private JobMetrics metrics;
    private ContentCacheUpdaterService service;
    private String metricsTopic;
    private RedisConnect redisConnect;

    private final String JOB_NAME = "ContentCacheUpdater";

    public ContentCacheUpdaterTask(Config config, TaskContext context, RedisConnect redisConnect) {
        init(config, context, redisConnect);
    }

    public ContentCacheUpdaterTask() {
    }

    @Override
    public void init(Config config, TaskContext context) {
        init(config, context, null);
    }

    public void init(Config config, TaskContext context, RedisConnect redisConnect) {
        metrics = new JobMetrics(context, JOB_NAME);
        redisConnect = null != redisConnect? redisConnect: new RedisConnect(config);
        service = new ContentCacheUpdaterService(config, redisConnect, metrics);
        metricsTopic = config.get("output.metrics.topic.name");
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector,
                        TaskCoordinator taskCoordinator) throws Exception {

        ContentCacheUpdaterSource source = new ContentCacheUpdaterSource(envelope);
        ContentCacheUpdaterSink sink = new ContentCacheUpdaterSink(collector, metrics);
        service.process(source, sink);
    }

    @Override
    public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        String mEvent = metrics.collect();
        collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", metricsTopic), mEvent));
        metrics.clear();
    }
}
