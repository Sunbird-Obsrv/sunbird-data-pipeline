package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.ekstep.ep.samza.logger.Logger;
import org.apache.samza.task.*;

public class ItemDeNormalizationTask implements StreamTask, InitableTask, WindowableTask {
    static Logger LOGGER = new Logger(ItemDeNormalizationTask.class);
    private org.ekstep.ep.samza.task.ItemDeNormalizationConfig config;
    private ItemDeNormalizationMetrics metrics;


    public ItemDeNormalizationTask(Config config, TaskContext context) {
        initialise(config, context);
    }

    public ItemDeNormalizationTask() {
    }

    @Override
    public void init(Config config, TaskContext context) throws Exception {
        initialise(config, context);
    }

    private void initialise(Config config, TaskContext context) {
        this.config = new ItemDeNormalizationConfig(config);
        metrics = new ItemDeNormalizationMetrics(context);
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector,
                        TaskCoordinator taskCoordinator) throws Exception {
        ItemDeNormalizationSource source = new ItemDeNormalizationSource(envelope);
        ItemDeNormalizationSink sink = new ItemDeNormalizationSink(collector, metrics, config);

        sink.toSuccessTopic(source.getEvent());
    }

    @Override
    public void window(MessageCollector messageCollector, TaskCoordinator taskCoordinator) throws Exception {
        metrics.clear();
    }
}
