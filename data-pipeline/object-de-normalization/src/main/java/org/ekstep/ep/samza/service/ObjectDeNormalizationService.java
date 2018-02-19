package org.ekstep.ep.samza.service;

import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.system.Strategy;
import org.ekstep.ep.samza.task.ObjectDeNormalizationConfig;
import org.ekstep.ep.samza.task.ObjectDeNormalizationSink;
import org.ekstep.ep.samza.task.ObjectDeNormalizationSource;

import java.util.HashMap;

public class ObjectDeNormalizationService {
    public static final String CUSTOM = "custom";
    static Logger LOGGER = new Logger(ObjectDeNormalizationService.class);
    private final ObjectDeNormalizationConfig config;
    private final HashMap strategies;

    public ObjectDeNormalizationService(HashMap strategies, ObjectDeNormalizationConfig config) {
        this.strategies = strategies;
        this.config = config;
    }

    public void process(ObjectDeNormalizationSource source, ObjectDeNormalizationSink sink) {
        Event event = source.getEvent();

        try {

            if (event.getObjectID() == null) {
                LOGGER.info(event.id(), "OBJECT ID IS ABSENT: SKIPPING THE EVENT THROUGH");
                sink.toSuccessTopic(event);
                return;
            }

            if (event.objectFieldsPresent()) {
                LOGGER.info(event.id(), "DENORMALIZING USING DEFINED STRATEGIES");
                LOGGER.info(event.getObjectID(), "FOUND OBJECT ID");
                Strategy strategy = (Strategy) strategies.get(event.getObjectType());
                if (strategy != null) {
                    strategy.execute(event);
                    sink.toSuccessTopic(event);
                    return;
                }
            }

            LOGGER.info(event.id(), "DENORMALIZING USING CUSTOM STRATEGY");
            Strategy cStrategy = (Strategy) strategies.get(CUSTOM);
            cStrategy.execute(event);
            sink.toSuccessTopic(event);
        } catch (Exception e) {
            LOGGER.error(event.id(), "EXCEPTION. PASSING EVENT THROUGH AND ADDING IT TO FAILED TOPIC", e);
            sink.toSuccessTopic(event);
            sink.toFailedTopic(event);
        }
    }
}
