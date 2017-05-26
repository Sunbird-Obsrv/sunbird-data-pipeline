package org.ekstep.ep.samza.service;

import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.task.PublicExhaustDeDuplicationConfig;
import org.ekstep.ep.samza.task.PublicExhaustDeDuplicationSink;
import org.ekstep.ep.samza.task.PublicExhaustDeDuplicationSource;

public class PublicExhaustDeDuplicationService {
    static Logger LOGGER = new Logger(PublicExhaustDeDuplicationService.class);
    private final PublicExhaustDeDuplicationConfig config;

    public PublicExhaustDeDuplicationService(PublicExhaustDeDuplicationConfig config) {
        this.config = config;
    }

    public void process(PublicExhaustDeDuplicationSource source, PublicExhaustDeDuplicationSink sink) {
        Event event = source.getEvent();

        try {
            LOGGER.info(event.id(), "PASSING EVENT THROUGH");
            sink.toSuccessTopic(event);
        } catch (Exception e) {
            LOGGER.error(event.id(), "EXCEPTION. PASSING EVENT THROUGH AND ADDING IT TO FAILED TOPIC", e);
            e.printStackTrace();
            sink.toFailedTopic(event);
        }
    }
}
