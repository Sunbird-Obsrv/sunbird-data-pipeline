package org.ekstep.ep.samza.service;

import org.ekstep.ep.samza.config.ObjectDenormalizationAdditionalConfig;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.task.ObjectDeNormalizationConfig;
import org.ekstep.ep.samza.task.ObjectDeNormalizationSink;
import org.ekstep.ep.samza.task.ObjectDeNormalizationSource;

public class ObjectDeNormalizationService {
    static Logger LOGGER = new Logger(ObjectDeNormalizationService.class);
    private final ObjectDeNormalizationConfig config;
    private final ObjectDenormalizationAdditionalConfig additionalConfig;

    public ObjectDeNormalizationService(ObjectDeNormalizationConfig config, ObjectDenormalizationAdditionalConfig additionalConfig) {
        this.config = config;
        this.additionalConfig = additionalConfig;
    }

    public void process(ObjectDeNormalizationSource source, ObjectDeNormalizationSink sink) {
        Event event = source.getEvent();

        try {
            LOGGER.info(event.id(), "PASSING EVENT THROUGH");
            sink.toSuccessTopic(event);
        } catch (Exception e) {
            LOGGER.error(event.id(), "EXCEPTION. PASSING EVENT THROUGH AND ADDING IT TO FAILED TOPIC. EVENT: " + event, e);
            sink.toSuccessTopic(event);
            sink.toFailedTopic(event);
            e.printStackTrace();
        }
    }
}
