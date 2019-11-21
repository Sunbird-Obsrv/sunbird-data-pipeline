package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.util.CassandraConnect;
import org.ekstep.ep.samza.util.RedisConnect;
import org.ekstep.ep.samza.service.UserCacheUpdaterService;

import java.util.Arrays;
import java.util.List;

public class UserCacheUpdaterTask implements StreamTask, InitableTask, WindowableTask {

    private JobMetrics metrics;
    private UserCacheUpdaterConfig config;
    private UserCacheUpdaterService service;
    private String metricsTopic;
    private RedisConnect redisConnect;
    private CassandraConnect cassandraConnect;

    public UserCacheUpdaterTask(Config config, TaskContext context, CassandraConnect cassandraConnect, RedisConnect redisConnect) {
        init(config, context, cassandraConnect, redisConnect);
    }

    public UserCacheUpdaterTask() {
    }

    @Override
    public void init(Config config, TaskContext context) {
        init(config, context, null, null);
    }

    public void init(Config config, TaskContext context, CassandraConnect cassandraConnect, RedisConnect redisConnect) {
        this.config = new UserCacheUpdaterConfig(config);
        metrics = new JobMetrics(context, this.config.jobName());
        List<String> cassandraHosts = Arrays.asList(config.get("middleware.cassandra.host", "127.0.0.1").split(","));
        cassandraConnect = null != cassandraConnect ? cassandraConnect : new CassandraConnect(cassandraHosts, config.getInt("middleware..cassandra.port", 9042));
        redisConnect = null != redisConnect? redisConnect: new RedisConnect(config);
        service = new UserCacheUpdaterService(this.config, redisConnect, cassandraConnect, metrics);
        metricsTopic = config.get("output.metrics.topic.name");
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector,
                        TaskCoordinator taskCoordinator) throws Exception {

        UserCacheUpdaterSource source = new UserCacheUpdaterSource(envelope);
        UserCacheUpdaterSink sink = new UserCacheUpdaterSink(collector, metrics);
        service.process(source, sink);
    }

    @Override
    public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        String mEvent = metrics.collect();
        collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", metricsTopic), mEvent));
        metrics.clear();
    }
}
