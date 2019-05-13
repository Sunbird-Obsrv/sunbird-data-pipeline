package org.ekstep.ep.samza.service;

import com.google.gson.JsonSyntaxException;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.domain.EventUpdaterFactory;
import org.ekstep.ep.samza.task.DeNormalizationConfig;
import org.ekstep.ep.samza.task.DeNormalizationSink;
import org.ekstep.ep.samza.task.DeNormalizationSource;

import java.util.List;

import static java.text.MessageFormat.format;

public class DeNormalizationService {

    static Logger LOGGER = new Logger(DeNormalizationService.class);
    private final DeNormalizationConfig config;
    private final EventUpdaterFactory eventUpdaterFactory;

    public DeNormalizationService(DeNormalizationConfig config, EventUpdaterFactory eventUpdaterFactory) {
        this.config = config;
        this.eventUpdaterFactory = eventUpdaterFactory;
    }

    public void process(DeNormalizationSource source, DeNormalizationSink sink) {
        Event event = null;
        try {
            event = source.getEvent();
            sink.setMetricsOffset(source.getSystemStreamPartition(),source.getOffset());
            String eid = event.eid();
            List<String> summaryRouteEventPrefix = this.config.getSummaryFilterEvents();

            if("ERROR".equals(eid)){
                LOGGER.debug(null,"Skipping as eid is ERROR");
                sink.incrementSkippedCount(event);
                return;
            }

            if (summaryRouteEventPrefix.contains(eid)) {
                event = getDenormalizedEvent(event);
                sink.toSuccessTopic(event);
            } else if (eid.startsWith("ME_")) {
                LOGGER.debug(null, "Ignoring as eid is other than WFS");
                sink.incrementSkippedCount(event);
            } else {
                // ignore past data (older than last X months)
                if (event.isOlder(config.ignorePeriodInMonths())) {
                    LOGGER.debug(null, "Ignoring as ets is older than N months");
                    sink.toFailedTopic(event, "older than " + config.ignorePeriodInMonths() + " months");
                } else if ("LOG".equals(eid) && !"api_access".equalsIgnoreCase(event.edataType())) {
                    LOGGER.debug(null, "Ignoring as edata_type is other than 'api_access' for LOG events");
                    sink.incrementSkippedCount(event);
                } else {
                    event = getDenormalizedEvent(event);
                    sink.toSuccessTopic(event);
                }
            }
        } catch(JsonSyntaxException e){
            LOGGER.error(null, "INVALID EVENT: " + source.getMessage());
            sink.toMalformedTopic(source.getMessage());
        } catch (Exception e) {
            LOGGER.error(null,
                    format("EXCEPTION. PASSING EVENT THROUGH AND ADDING IT TO EXCEPTION TOPIC. EVENT: {0}, EXCEPTION:",
                            event),
                    e);
            sink.toErrorTopic(event, e.getMessage());
        }
    }

    public Event getDenormalizedEvent(Event event) {
        // correct future dates
        event.compareAndAlterEts();

        if ("dialcode".equals(event.objectType())) {
            // add dialcode details to the event where object.type = dialcode
            event = eventUpdaterFactory.getInstance("dialcode-data-updater")
                    .update(event, event.getKey("content").get(0), true);
        } else {
            // add content details to the event
            event = eventUpdaterFactory.getInstance("content-data-updater")
                    .update(event, event.getKey("content").get(0), true);
        }
        // add user details to the event
        event = eventUpdaterFactory.getInstance("user-data-updater")
                .update(event, event.getKey("user").get(0), false);
        // add device details to the event
        event = eventUpdaterFactory.getInstance("device-data-updater")
                .update(event);
        // add dialcode details to the event only for SEARCH event
        event = eventUpdaterFactory.getInstance("dialcode-data-updater")
                .update(event, event.getKey("dialcode"), true);

        // Update version to 3.1
        event.updateVersion();
        return event;
    }
}