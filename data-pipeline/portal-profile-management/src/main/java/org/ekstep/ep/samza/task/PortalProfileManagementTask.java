package org.ekstep.ep.samza.task;

import okhttp3.OkHttpClient;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.metrics.JobMetrics;
import org.ekstep.ep.samza.object.service.ObjectServiceClient;
import org.ekstep.ep.samza.service.PortalProfileManagementService;

public class PortalProfileManagementTask implements StreamTask, InitableTask, WindowableTask {
    static Logger LOGGER = new Logger(PortalProfileManagementTask.class);
    private PortalProfileManagementConfig config;
    private JobMetrics metrics;
    private PortalProfileManagementService service;

    public PortalProfileManagementTask() {
    }

    public PortalProfileManagementTask(Config configMock, TaskContext contextMock) throws Exception {
        init((Config) configMock, (TaskContext) contextMock);
    }

    @Override
    public void init(Config config, TaskContext context) throws Exception {
        init(config, context, null);
    }

    private void init(Config config, TaskContext context, ObjectServiceClient objectService) {
        this.config = new PortalProfileManagementConfig(config);
        metrics = new JobMetrics(context);
        String objectServiceEndpoint = this.config.getObjectServiceEndPoint();
        objectService =
                objectService == null ?
                        new ObjectServiceClient(objectServiceEndpoint, new OkHttpClient()) :
                        objectService;
        service = new PortalProfileManagementService(this.config, objectService);
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector,
                        TaskCoordinator taskCoordinator) throws Exception {
        PortalProfileManagementSource source = new PortalProfileManagementSource(envelope);
        PortalProfileManagementSink sink = new PortalProfileManagementSink(collector, metrics, config);
        service.process(source, sink);
    }

    @Override
    public void window(MessageCollector messageCollector, TaskCoordinator taskCoordinator) throws Exception {
        metrics.clear();
    }
}
