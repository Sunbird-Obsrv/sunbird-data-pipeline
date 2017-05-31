package org.ekstep.ep.samza.service;

import org.ekstep.ep.samza.dedup.DeDupEngine;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.task.PrivateExhaustDeDuplicationConfig;
import org.ekstep.ep.samza.task.PrivateExhaustDeDuplicationSink;
import org.ekstep.ep.samza.task.PrivateExhaustDeDuplicationSource;

public class PrivateExhaustDeDuplicationService {
    static Logger LOGGER = new Logger(PrivateExhaustDeDuplicationService.class);
    private final DeDupEngine deDupEngine;


    public PrivateExhaustDeDuplicationService(DeDupEngine deDupEngine) {
        this.deDupEngine= deDupEngine;
    }

    public void process(PrivateExhaustDeDuplicationSource source, PrivateExhaustDeDuplicationSink sink) {
        Event event = source.getEvent();

        try {
            String checksum = event.getChecksum();

            if (checksum == null) {
                LOGGER.info(event.id(), "EVENT WITHOUT CHECKSUM & MID, PASSING THROUGH : {}", event);
                event.markSkipped();
                sink.toSuccessTopic(event);
                return;
            }

            if (!deDupEngine.isUniqueEvent(checksum)) {
                LOGGER.info(event.id(), "DUPLICATE EVENT, CHECKSUM: {}", checksum);
                event.markDuplicate();
                sink.toDuplicateTopic(event);
                return;
            }

            LOGGER.info(event.id(), "ADDING EVENT CHECKSUM TO STORE");
            deDupEngine.storeChecksum(checksum);
            event.markSuccess();
            sink.toSuccessTopic(event);
        } catch (Exception e) {
            LOGGER.error(event.id(), "EXCEPTION. PASSING EVENT THROUGH AND ADDING IT TO FAILED TOPIC", e);
            event.markFailure(e.getMessage());
            e.printStackTrace();
            sink.toFailedTopic(event);
        }
    }
}
