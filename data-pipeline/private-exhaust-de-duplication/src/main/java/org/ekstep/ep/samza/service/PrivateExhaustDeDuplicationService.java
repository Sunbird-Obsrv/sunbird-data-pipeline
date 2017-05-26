package org.ekstep.ep.samza.service;

import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.task.PrivateExhaustDeDuplicationConfig;
import org.ekstep.ep.samza.task.PrivateExhaustDeDuplicationSink;
import org.ekstep.ep.samza.task.PrivateExhaustDeDuplicationSource;

public class PrivateExhaustDeDuplicationService {
    static Logger LOGGER = new Logger(PrivateExhaustDeDuplicationService.class);
    private final PrivateExhaustDeDuplicationConfig config;

    public PrivateExhaustDeDuplicationService(PrivateExhaustDeDuplicationConfig config) {
        this.config = config;
    }

    public void process(PrivateExhaustDeDuplicationSource source, PrivateExhaustDeDuplicationSink sink) {
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
