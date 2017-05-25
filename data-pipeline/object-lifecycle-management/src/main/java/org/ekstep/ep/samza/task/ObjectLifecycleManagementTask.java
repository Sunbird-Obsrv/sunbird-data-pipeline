package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.external.ObjectServiceClient;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.metrics.JobMetrics;
import org.ekstep.ep.samza.service.ObjectLifecycleManagementService;

import java.util.List;

public class ObjectLifecycleManagementTask implements StreamTask, InitableTask, WindowableTask {
    static Logger LOGGER = new Logger(ObjectLifecycleManagementTask.class);
    private ObjectLifecycleManagementConfig config;
    private JobMetrics metrics;
    private ObjectLifecycleManagementService service;
    private List<String> lifeCycleEvents;
    private ObjectServiceClient objectService;
    private String objectServiceEndpoint;

    public ObjectLifecycleManagementTask() {
    }

    public ObjectLifecycleManagementTask(Config configMock, TaskContext contextMock, ObjectServiceClient objectService) throws Exception {
        init(configMock, contextMock, objectService);
    }

    @Override
    public void init(Config config, TaskContext context) throws Exception {
        init(config,context,null);
    }

    private void init(Config config, TaskContext context, ObjectServiceClient objectService){
        this.config = new ObjectLifecycleManagementConfig(config);
        metrics = new JobMetrics(context);
        lifeCycleEvents = this.config.getLifeCycleEvents();
        objectServiceEndpoint = this.config.getObjectServiceEndPoint();
        objectService =
                objectService == null ?
                        new ObjectServiceClient(objectServiceEndpoint) :
                        objectService;
        service = new ObjectLifecycleManagementService(this.config, objectService, metrics);
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector,
                        TaskCoordinator taskCoordinator) throws Exception {
        ObjectLifecycleManagementSource source = new ObjectLifecycleManagementSource(envelope,lifeCycleEvents);
        ObjectLifecycleManagementSink sink = new ObjectLifecycleManagementSink(collector, metrics, config);
        service.process(source, sink);
    }

    @Override
    public void window(MessageCollector messageCollector, TaskCoordinator taskCoordinator) throws Exception {
        metrics.clear();
    }
}
