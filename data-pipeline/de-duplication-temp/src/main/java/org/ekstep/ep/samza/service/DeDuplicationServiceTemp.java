package org.ekstep.ep.samza.service;

import com.google.gson.JsonSyntaxException;
import org.ekstep.ep.samza.dedup.DeDupEngine;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.task.DeDuplicationConfigTemp;
import org.ekstep.ep.samza.task.DeDuplicationSinkTemp;
import org.ekstep.ep.samza.task.DeDuplicationSourceTemp;

import static java.text.MessageFormat.format;

public class DeDuplicationServiceTemp {
    static Logger LOGGER = new Logger(DeDuplicationServiceTemp.class);
    private final DeDupEngine deDupEngine;
    private final DeDuplicationConfigTemp config;

    public DeDuplicationServiceTemp(DeDupEngine deDupEngine, DeDuplicationConfigTemp config) {
        this.deDupEngine= deDupEngine;
        this.config = config;
    }

    public void process(DeDuplicationSourceTemp source, DeDuplicationSinkTemp sink) {
        Event event = null;

        try {
            event = source.getEvent();
            String checksum = event.getChecksum();
            long startTime = System.nanoTime();

            if (checksum == null) {
                LOGGER.info(event.id(), "EVENT WITHOUT CHECKSUM & MID, PASSING THROUGH : {}", event);
                event.markSkipped();
                sink.toSuccessTopic(event);
                LOGGER.info(event.id(), String.format("{\"checksum_check_ms\": %d}", (System.nanoTime() - startTime)/1000000));
                return;
            }

            if (!deDupEngine.isUniqueEvent(checksum)) {
                LOGGER.info(event.id(), "DUPLICATE EVENT, CHECKSUM: {}", checksum);
                event.markDuplicate();
                sink.toDuplicateTopic(event);
                LOGGER.info(event.id(), String.format("{\"duplicate_check_ms\": %d}", (System.nanoTime() - startTime)/1000000));
                return;
            }

            LOGGER.info(event.id(), "ADDING EVENT CHECKSUM TO STORE");

            deDupEngine.storeChecksum(checksum);
            event.updateDefaults(config);
            event.markSuccess();
            sink.toSuccessTopic(event);
            LOGGER.info(event.id(), String.format("{\"insert_to_store_ms\": %d}", (System.nanoTime() - startTime)/1000000));

        } catch(JsonSyntaxException e){
            LOGGER.error(null, "INVALID EVENT: " + source.getMessage());
            sink.toMalformedEventsTopic(source.getMessage());
        } catch (Exception e) {
            event.markFailure(e.getMessage());
            LOGGER.error(null, format(
                    "EXCEPTION. PASSING EVENT THROUGH AND ADDING IT TO EXCEPTION TOPIC. EVENT: {0}, EXCEPTION:",
                    event),e);
            sink.toErrorTopic(event);
        }
    }
}

