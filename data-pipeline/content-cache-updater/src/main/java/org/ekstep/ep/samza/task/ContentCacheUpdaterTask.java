package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.service.ContentCacheUpdaterService;
import org.ekstep.ep.samza.util.RedisConnect;

public class ContentCacheUpdaterTask extends BaseSamzaTask {

    private JobMetrics metrics;
    private ContentCacheUpdaterService service;
    private ContentCacheConfig config;

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
        this.config = new ContentCacheConfig(config);
        metrics = new JobMetrics(context, this.config.JOB_NAME());
        redisConnect = null != redisConnect? redisConnect: new RedisConnect(config);
        service = new ContentCacheUpdaterService(this.config, redisConnect, metrics);
        this.initTask(config, metrics);
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector,
                        TaskCoordinator taskCoordinator) throws Exception {

        ContentCacheUpdaterSource source = new ContentCacheUpdaterSource(envelope);
        ContentCacheUpdaterSink sink = new ContentCacheUpdaterSink(collector, metrics);
        service.process(source, sink);
    }
    
}
