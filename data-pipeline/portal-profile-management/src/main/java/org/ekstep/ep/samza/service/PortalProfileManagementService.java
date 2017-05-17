package org.ekstep.ep.samza.service;

import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.task.PortalProfileManagementConfig;
import org.ekstep.ep.samza.task.PortalProfileManagementSink;
import org.ekstep.ep.samza.task.PortalProfileManagementSource;

public class PortalProfileManagementService {
    static Logger LOGGER = new Logger(PortalProfileManagementService.class);
    private final PortalProfileManagementConfig config;

    public PortalProfileManagementService(PortalProfileManagementConfig config) {
        this.config = config;
    }

    public void process(PortalProfileManagementSource source, PortalProfileManagementSink sink) {
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
