package org.ekstep.ep.samza.service;

import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.external.ObjectResponse;
import org.ekstep.ep.samza.external.ObjectServiceClient;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.task.ObjectLifecycleManagementConfig;
import org.ekstep.ep.samza.task.ObjectLifecycleManagementSink;
import org.ekstep.ep.samza.task.ObjectLifecycleManagementSource;

public class ObjectLifecycleManagementService {
    static Logger LOGGER = new Logger(ObjectLifecycleManagementService.class);
    private final ObjectLifecycleManagementConfig config;
    private final ObjectServiceClient objectService;

    public ObjectLifecycleManagementService(ObjectLifecycleManagementConfig config, ObjectServiceClient objectService) {
        this.config = config;
        this.objectService = objectService;
    }

    public void process(ObjectLifecycleManagementSource source, ObjectLifecycleManagementSink sink) {
        Event event = source.getEvent();

        try {
            if (!event.canBeProcessed()){
                LOGGER.info(event.id(), "EVENT IN SKIP LIST, PASSING EVENT THROUGH");
                sink.toSuccessTopic(event);
                return;
            }

            LOGGER.info(event.id(), "CREATE OR SAVE OBJECT");

            ObjectResponse objectResponse = objectService.createOrUpdate(event.LifecycleObjectAttributes());

            if (objectResponse.successful()) {
                event.updateFlags(true);
            } else {
                event.updateFlags(false);
                event.updateMetadata(objectResponse.params());
            }

            LOGGER.info(event.id(), "PASSING EVENT THROUGH");
            sink.toSuccessTopic(event);
        } catch (Exception e) {
            LOGGER.error(event.id(), "EXCEPTION. PASSING EVENT THROUGH AND ADDING IT TO FAILED TOPIC", e);
            event.updateFlags(false);
            sink.toSuccessTopic(event);
            sink.toFailedTopic(event);
            e.printStackTrace();
        }
    }
}
