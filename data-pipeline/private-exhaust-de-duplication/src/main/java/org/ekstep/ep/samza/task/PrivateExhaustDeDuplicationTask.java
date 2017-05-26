package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.metrics.JobMetrics;
import org.ekstep.ep.samza.service.PrivateExhaustDeDuplicationService;

public class PrivateExhaustDeDuplicationTask implements StreamTask, InitableTask, WindowableTask {
    static Logger LOGGER = new Logger(PrivateExhaustDeDuplicationTask.class);
    private PrivateExhaustDeDuplicationConfig config;
    private JobMetrics metrics;
    private PrivateExhaustDeDuplicationService service;

    public PrivateExhaustDeDuplicationTask(Config config, TaskContext context,
                                           KeyValueStore<Object, Object> publicExhaustStore) {
        init(config, context, publicExhaustStore);
    }

    public PrivateExhaustDeDuplicationTask() {

    }

    @Override
    public void init(Config config, TaskContext context) throws Exception {
        init(config, context,
                (KeyValueStore<Object, Object>) context.getStore("private-exhaust-store"));
    }

    private void init(Config config, TaskContext context,
                      KeyValueStore<Object, Object> publicExhaustStore) {
        this.config = new PrivateExhaustDeDuplicationConfig(config);
        metrics = new JobMetrics(context);
        service = new PrivateExhaustDeDuplicationService(this.config);
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector,
                        TaskCoordinator taskCoordinator) throws Exception {
        PrivateExhaustDeDuplicationSource source = new PrivateExhaustDeDuplicationSource(envelope);
        PrivateExhaustDeDuplicationSink sink = new PrivateExhaustDeDuplicationSink(collector, metrics, config);

        service.process(source, sink);
    }

    @Override
    public void window(MessageCollector messageCollector, TaskCoordinator taskCoordinator) throws Exception {
        metrics.clear();
    }
}
