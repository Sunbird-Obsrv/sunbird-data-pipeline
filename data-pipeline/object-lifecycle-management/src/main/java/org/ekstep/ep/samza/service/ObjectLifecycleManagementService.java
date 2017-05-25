package org.ekstep.ep.samza.service;

import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.metrics.JobMetrics;
import org.ekstep.ep.samza.object.dto.SaveObjectResponse;
import org.ekstep.ep.samza.object.service.ObjectServiceClient;
import org.ekstep.ep.samza.task.ObjectLifecycleManagementConfig;
import org.ekstep.ep.samza.task.ObjectLifecycleManagementSink;
import org.ekstep.ep.samza.task.ObjectLifecycleManagementSource;

public class ObjectLifecycleManagementService {
    static Logger LOGGER = new Logger(ObjectLifecycleManagementService.class);
    private final ObjectLifecycleManagementConfig config;
    private final ObjectServiceClient objectService;
    private final JobMetrics metrics;

    public ObjectLifecycleManagementService(ObjectLifecycleManagementConfig config, ObjectServiceClient objectService, JobMetrics metrics) {
        this.config = config;
        this.objectService = objectService;
        this.metrics = metrics;
    }

    public void process(ObjectLifecycleManagementSource source, ObjectLifecycleManagementSink sink) {
        Event event = source.getEvent();

        try {
            if (!event.canBeProcessed()) {
                LOGGER.info(event.id(), "EVENT NOT IN PROCESS LIST, PASSING EVENT THROUGH");
                sink.toSuccessTopic(event);
                metrics.incSkippedCounter();
                return;
            }

            SaveObjectResponse saveObjectResponse = objectService.createOrUpdate(event.LifecycleObjectAttributes());

            if (saveObjectResponse.successful()) {
                event.updateFlags(true);
            } else {
                LOGGER.error(event.id(), "UNABLE TO SAVE OBJECT. RESPONSE: {}", saveObjectResponse);
                event.updateFlags(false);
                event.updateMetadata(saveObjectResponse.params());
            }
            LOGGER.info(event.id(), "PASSING EVENT THROUGH: {}", event);
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
