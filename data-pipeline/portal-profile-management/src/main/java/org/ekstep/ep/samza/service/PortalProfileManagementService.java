package org.ekstep.ep.samza.service;

import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.object.service.ObjectService;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.task.PortalProfileManagementConfig;
import org.ekstep.ep.samza.task.PortalProfileManagementSink;
import org.ekstep.ep.samza.task.PortalProfileManagementSource;

public class PortalProfileManagementService {
    static Logger LOGGER = new Logger(PortalProfileManagementService.class);
    private final PortalProfileManagementConfig config;
    private ObjectService objectService;

    public PortalProfileManagementService(PortalProfileManagementConfig config, ObjectService objectService) {
        this.config = config;
        this.objectService = objectService;
    }

    public void process(PortalProfileManagementSource source, PortalProfileManagementSink sink) {
        Event event = source.getEvent();

        try {
            if (!config.cpUpdateProfileEvent().equalsIgnoreCase(event.eid())) {
                LOGGER.info(event.id(), "SKIPPING EVENT");
                event.markSkipped();
                sink.toSuccessTopic(event);
                return;
            }

            NullableValue<String> uid = event.uid();
            String details = event.userDetails();
            if (uid.isNull() || details == null) {
                //TODO:# implement
            }
            objectService.saveDetails(uid.value(), details);
            event.markProcessed();
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
