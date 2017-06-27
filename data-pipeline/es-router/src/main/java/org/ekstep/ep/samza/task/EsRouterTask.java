package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.metrics.JobMetrics;
import service.EsRouterService;

import java.util.List;

public class EsRouterTask implements StreamTask, InitableTask, WindowableTask {

    static Logger LOGGER = new Logger(EsRouterTask.class);
    private EsRouterConfig config;
    private JobMetrics metrics;
    private EsRouterService service;

    public EsRouterTask() {
    }

    public EsRouterTask(Config configMock, TaskContext contextMock) throws Exception {
        init(configMock, contextMock);
    }

    @Override
    public void init(Config config, TaskContext context) throws Exception {
        this.config = new EsRouterConfig(config);
        metrics = new JobMetrics(context);
        service = new EsRouterService(this.config, metrics);
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector,
                        TaskCoordinator taskCoordinator) throws Exception {
        EsRouterSource source = new EsRouterSource(envelope);
        EsRouterSink sink = new EsRouterSink(collector, metrics, config);
        service.process(source, sink);
    }

    @Override
    public void window(MessageCollector messageCollector, TaskCoordinator taskCoordinator) throws Exception {
        metrics.clear();
    }
}
