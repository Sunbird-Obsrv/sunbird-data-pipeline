package org.ekstep.ep.samza.service;

import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.task.ObjectLifecycleManagementConfig;
import org.ekstep.ep.samza.task.ObjectLifecycleManagementSink;
import org.ekstep.ep.samza.task.ObjectLifecycleManagementSource;

public class ObjectLifecycleManagementService {
    static Logger LOGGER = new Logger(ObjectLifecycleManagementService.class);
    private final ObjectLifecycleManagementConfig config;

    public ObjectLifecycleManagementService(ObjectLifecycleManagementConfig config) {
        this.config = config;
    }

    public void process(ObjectLifecycleManagementSource source, ObjectLifecycleManagementSink sink) {
        Event event = source.getEvent();

        try {
            LOGGER.info(event.id(), "PASSING EVENT THROUGH");
            sink.toSuccessTopic(event);
        } catch (Exception e) {
            LOGGER.error(event.id(), "EXCEPTION. PASSING EVENT THROUGH AND ADDING IT TO FAILED TOPIC", e);
            sink.toSuccessTopic(event);
            sink.toFailedTopic(event);
            e.printStackTrace();
        }

    }
}
