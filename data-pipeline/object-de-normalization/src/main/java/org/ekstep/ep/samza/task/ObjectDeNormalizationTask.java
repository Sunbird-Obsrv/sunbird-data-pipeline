package org.ekstep.ep.samza.task;

import okhttp3.OkHttpClient;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.metrics.JobMetrics;
import org.ekstep.ep.samza.object.service.ObjectService;
import org.ekstep.ep.samza.object.service.ObjectServiceClient;
import org.ekstep.ep.samza.service.ObjectDeNormalizationService;

public class ObjectDeNormalizationTask implements StreamTask, InitableTask, WindowableTask {
    static Logger LOGGER = new Logger(ObjectDeNormalizationTask.class);
    private ObjectDeNormalizationConfig config;
    private JobMetrics metrics;
    private ObjectDeNormalizationService service;

    public ObjectDeNormalizationTask() {
    }

    public ObjectDeNormalizationTask(Config configMock, TaskContext contextMock) throws Exception {
        init((Config) configMock, (TaskContext) contextMock);
    }

    @Override
    public void init(Config config, TaskContext context) throws Exception {
        init(config, context, null);
    }

    private void init(Config config, TaskContext context, ObjectService objectService) {
        this.config = new ObjectDeNormalizationConfig(config);
        metrics = new JobMetrics(context);

        String objectServiceEndpoint = this.config.objectServiceEndPoint();
        objectService =
                objectService == null ?
                        new ObjectServiceClient(objectServiceEndpoint, new OkHttpClient()) :
                        objectService;


        service = new ObjectDeNormalizationService(this.config, this.config.additionalConfig(), objectService);
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector,
                        TaskCoordinator taskCoordinator) throws Exception {
        ObjectDeNormalizationSource source = new ObjectDeNormalizationSource(envelope);
        ObjectDeNormalizationSink sink = new ObjectDeNormalizationSink(collector, metrics, config);
        service.process(source, sink);
    }

    @Override
    public void window(MessageCollector messageCollector, TaskCoordinator taskCoordinator) throws Exception {
        metrics.clear();
    }
}
