package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.metrics.JobMetrics;
import org.ekstep.ep.samza.service.ObjectLifecycleManagementService;

public class ObjectLifecycleManagementTask implements StreamTask, InitableTask, WindowableTask {
    static Logger LOGGER = new Logger(ObjectLifecycleManagementTask.class);
    private ObjectLifecycleManagementConfig config;
    private JobMetrics metrics;
    private ObjectLifecycleManagementService service;

    public ObjectLifecycleManagementTask() {
    }

    public ObjectLifecycleManagementTask(Config configMock, TaskContext contextMock) throws Exception {
        init(configMock, contextMock);
    }

    @Override
    public void init(Config config, TaskContext context) throws Exception {
        this.config = new ObjectLifecycleManagementConfig(config);
        metrics = new JobMetrics(context);
        service = new ObjectLifecycleManagementService(this.config);
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector,
                        TaskCoordinator taskCoordinator) throws Exception {
        ObjectLifecycleManagementSource source = new ObjectLifecycleManagementSource(envelope);
        ObjectLifecycleManagementSink sink = new ObjectLifecycleManagementSink(collector, metrics, config);
        service.process(source, sink);
    }

    @Override
    public void window(MessageCollector messageCollector, TaskCoordinator taskCoordinator) throws Exception {
        metrics.clear();
    }
}
