package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.metrics.JobMetrics;
import org.ekstep.ep.samza.service.PublicExhaustDeDuplicationService;

public class PublicExhaustDeDuplicationTask implements StreamTask, InitableTask, WindowableTask {
    static Logger LOGGER = new Logger(PublicExhaustDeDuplicationTask.class);
    private PublicExhaustDeDuplicationConfig config;
    private JobMetrics metrics;
    private PublicExhaustDeDuplicationService service;

    public PublicExhaustDeDuplicationTask(Config config, TaskContext context,
                                          KeyValueStore<Object, Object> publicExhaustStore) {
        init(config, context, publicExhaustStore);
    }

    public PublicExhaustDeDuplicationTask() {

    }

    @Override
    public void init(Config config, TaskContext context) throws Exception {
        init(config, context,
                (KeyValueStore<Object, Object>) context.getStore("public-exhaust-store"));
    }

    private void init(Config config, TaskContext context,
                      KeyValueStore<Object, Object> publicExhaustStore) {
        this.config = new PublicExhaustDeDuplicationConfig(config);
        metrics = new JobMetrics(context);
        service = new PublicExhaustDeDuplicationService(this.config);
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector,
                        TaskCoordinator taskCoordinator) throws Exception {
        PublicExhaustDeDuplicationSource source = new PublicExhaustDeDuplicationSource(envelope);
        PublicExhaustDeDuplicationSink sink = new PublicExhaustDeDuplicationSink(collector, metrics, config);

        service.process(source, sink);
    }

    @Override
    public void window(MessageCollector messageCollector, TaskCoordinator taskCoordinator) throws Exception {
        metrics.clear();
    }
}
