package org.ekstep.ep.samza.service;

import com.google.gson.JsonSyntaxException;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.domain.EventUpdaterFactory;
import org.ekstep.ep.samza.task.DeNormalizationConfig;
import org.ekstep.ep.samza.task.DeNormalizationSink;
import org.ekstep.ep.samza.task.DeNormalizationSource;

import java.util.List;

public class DeNormalizationService {

    private static Logger LOGGER = new Logger(DeNormalizationService.class);
    private final DeNormalizationConfig config;
    private final EventUpdaterFactory eventUpdaterFactory;

    public DeNormalizationService(DeNormalizationConfig config, EventUpdaterFactory eventUpdaterFactory) {
        this.config = config;
        this.eventUpdaterFactory = eventUpdaterFactory;
    }

    public void process(DeNormalizationSource source, DeNormalizationSink sink) {
        try {
            Event event = source.getEvent();
            String eid = event.eid();
            List<String> summaryRouteEventPrefix = this.config.getSummaryFilterEvents();

            if("ERROR".equals(eid)){
                LOGGER.debug(null,"Skipping denormalization as eid is ERROR");
                sink.toSuccessTopic(event);
                return;
            }

            if (summaryRouteEventPrefix.contains(eid)) {
                denormEvent(event);
                sink.toSuccessTopic(event);
            } else if (eid.startsWith("ME_")) {
                LOGGER.debug(null, "Ignoring as eid is other than WFS");
                sink.incrementSkippedCount(event);
            } else {
                // ignore past data (older than last X months)
                if (event.isOlder(config.ignorePeriodInMonths())) {
                    sink.incExpiredEventCount();
                    LOGGER.debug(null, "Ignoring as ets is older than N months");
                    sink.toFailedTopic(event, "older than " + config.ignorePeriodInMonths() + " months");
                } else {
                    denormEvent(event);
                    sink.toSuccessTopic(event);
                }
            }
        } catch(JsonSyntaxException e){
        	e.printStackTrace();
            LOGGER.error(null, "INVALID EVENT: " + source.getMessage());
            sink.toMalformedTopic(source.getMessage());
        }
    }

    private void denormEvent(Event event) {

        // correct future dates
        event.compareAndAlterEts();

        if ("dialcode".equalsIgnoreCase(event.objectType()) || "qr".equalsIgnoreCase(event.objectType())) {
            // add dialcode details to the event where object.type = dialcode/qr
            eventUpdaterFactory.getInstance("dialcode-data-updater").update(event, event.getKey("content"));
        } else if ("user".equalsIgnoreCase(event.objectType())) {
            // skipping since it is same as user-denorm
        }
        else {
            // add content details to the event
            eventUpdaterFactory.getInstance("content-data-updater").update(event, event.getKey("content"));
            if(event.checkObjectIdNotEqualsRollUpl1Id()) {
                eventUpdaterFactory.getInstance("collection-data-updater").update(event, event.getKey("collection"));
            }
        }
        // add user details to the event
        eventUpdaterFactory.getInstance("user-data-updater").update(event);

        // add device details to the event
//        eventUpdaterFactory.getInstance("device-data-updater").update(event);
    }
}