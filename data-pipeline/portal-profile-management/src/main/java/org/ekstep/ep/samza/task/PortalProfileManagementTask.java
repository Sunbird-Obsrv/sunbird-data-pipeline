package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.metrics.JobMetrics;
import org.ekstep.ep.samza.service.PortalProfileManagementService;

public class PortalProfileManagementTask implements StreamTask, InitableTask, WindowableTask {
    static Logger LOGGER = new Logger(PortalProfileManagementTask.class);
    private PortalProfileManagementConfig config;
    private JobMetrics metrics;
    private PortalProfileManagementService service;

    public PortalProfileManagementTask() {
    }

    public PortalProfileManagementTask(Config configMock, TaskContext contextMock) throws Exception {
        init(configMock, contextMock);
    }

    @Override
    public void init(Config config, TaskContext context) throws Exception {
        this.config = new PortalProfileManagementConfig(config);
        metrics = new JobMetrics(context);
        service = new PortalProfileManagementService(this.config);
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
