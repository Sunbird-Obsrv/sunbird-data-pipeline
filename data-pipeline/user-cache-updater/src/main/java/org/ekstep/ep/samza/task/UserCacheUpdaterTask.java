package org.ekstep.ep.samza.task;

import java.util.Arrays;
import java.util.List;

import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.service.UserCacheUpdaterService;
import org.ekstep.ep.samza.util.CassandraConnect;
import org.ekstep.ep.samza.util.RedisConnect;

public class UserCacheUpdaterTask extends BaseSamzaTask {

    private JobMetrics metrics;
    private UserCacheUpdaterConfig config;
    private UserCacheUpdaterService service;

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
        this.initTask(config, metrics);
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector,
                        TaskCoordinator taskCoordinator) throws Exception {

        UserCacheUpdaterSource source = new UserCacheUpdaterSource(envelope);
        UserCacheUpdaterSink sink = new UserCacheUpdaterSink(collector, metrics);
        service.process(source, sink);
    }

}
