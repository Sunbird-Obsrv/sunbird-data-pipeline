package service;

import domain.Event;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.metrics.JobMetrics;
import org.ekstep.ep.samza.task.EsRouterConfig;
import org.ekstep.ep.samza.task.EsRouterSink;
import org.ekstep.ep.samza.task.EsRouterSource;

public class EsRouterService {
    static Logger LOGGER = new Logger(EsRouterService.class);
    private final EsRouterConfig config;
    private final JobMetrics metrics;

    public EsRouterService(EsRouterConfig config, JobMetrics metrics) {
        this.config = config;
        this.metrics = metrics;
    }

    public void process(EsRouterSource source, EsRouterSink sink) {
        Event event = source.getEvent();
        try {
            LOGGER.info(event.id(), "PASSING EVENT THROUGH: {}", event);
            sink.toSuccessTopic(event);
        } catch (Exception e) {
            LOGGER.error(event.id(), "EXCEPTION. ADDING EVENT TO FAILED TOPIC", e);
            sink.toFailedTopic(event);
        }
    }
}
