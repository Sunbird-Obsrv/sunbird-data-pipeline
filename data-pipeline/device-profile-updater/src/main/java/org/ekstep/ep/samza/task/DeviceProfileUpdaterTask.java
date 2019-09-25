package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.service.DeviceProfileUpdaterService;
import org.ekstep.ep.samza.util.CassandraConnect;
import org.ekstep.ep.samza.util.RedisConnect;

public class DeviceProfileUpdaterTask implements StreamTask, InitableTask, WindowableTask {

    static Logger LOGGER = new Logger(DeviceProfileUpdaterTask.class);
    private JobMetrics metrics;
    private DeviceProfileUpdaterService service;
    private DeviceProfileUpdaterConfig config;
    private String metricsTopic;
    private RedisConnect redisConnect;
    private CassandraConnect cassandraConnect;

    public DeviceProfileUpdaterTask(Config config, TaskContext context) { init(config, context); }

    public DeviceProfileUpdaterTask() {
    }

    @Override
    public void init(Config config, TaskContext context) {
        redisConnect = new RedisConnect(config);
        this.config = new DeviceProfileUpdaterConfig(config);
        metrics = new JobMetrics(context, this.config.jobName());
        cassandraConnect = new CassandraConnect(config);
        service = new DeviceProfileUpdaterService(config, redisConnect, cassandraConnect);
        metricsTopic = config.get("output.metrics.topic.name");
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector,
                        TaskCoordinator taskCoordinator) throws Exception {
        DeviceProfileUpdaterSource source = new DeviceProfileUpdaterSource(envelope);
        DeviceProfileUpdaterSink sink = new DeviceProfileUpdaterSink(collector, metrics, config);
        service.process(source, sink);
    }

    @Override
    public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        String mEvent = metrics.collect();
        collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", metricsTopic), mEvent));
        metrics.clear();
    }
}

