package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.logger.Logger;

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

        Event event = null;
        ItemDeNormalizationSource source = new ItemDeNormalizationSource(envelope);
        ItemDeNormalizationSink sink = new ItemDeNormalizationSink(collector, metrics, config);
        try {
            event = source.getEvent();
            LOGGER.debug(event.id(), "PASSING EVENT THROUGH: {}", event.getMap());
            sink.toSuccessTopic(event);
        } catch (Exception e) {
            LOGGER.error(null, "ERROR WHILE PROCESSING EVENT", e);
            if (event != null && event.getMap() != null) {
                LOGGER.error(event.id(), "ADDED FAILED EVENT TO FAILED TOPIC. EVENT: {}", event.getMap());
                sink.toFailedTopic(event);
            }
        }


    }

    @Override
    public void window(MessageCollector messageCollector, TaskCoordinator taskCoordinator) throws Exception {
        metrics.clear();
    }
}
