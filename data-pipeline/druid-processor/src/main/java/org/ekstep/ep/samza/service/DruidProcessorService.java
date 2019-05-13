package org.ekstep.ep.samza.service;

import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.google.gson.JsonSyntaxException;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.domain.EventUpdaterFactory;
import org.ekstep.ep.samza.task.DruidProcessorConfig;
import org.ekstep.ep.samza.task.DruidProcessorSink;
import org.ekstep.ep.samza.task.DruidProcessorSource;
import org.ekstep.ep.samza.util.SchemaValidator;

import java.util.List;

import static java.text.MessageFormat.format;

public class DruidProcessorService {

    private static Logger LOGGER = new Logger(DruidProcessorService.class);
    private final DruidProcessorConfig config;
    private final EventUpdaterFactory eventUpdaterFactory;
    private final SchemaValidator schemaValidator;

    public DruidProcessorService(DruidProcessorConfig config, EventUpdaterFactory eventUpdaterFactory,
                                 SchemaValidator schemaValidator) {
        this.config = config;
        this.eventUpdaterFactory = eventUpdaterFactory;
        this.schemaValidator = schemaValidator;
    }

    public void process(DruidProcessorSource source, DruidProcessorSink sink) {
        Event event = null;
        try {
            event = source.getEvent();
            sink.setMetricsOffset(source.getSystemStreamPartition(),source.getOffset());
            String eid = event.eid();
            List<String> summaryRouteEventPrefix = this.config.getSummaryFilterEvents();

            if("ERROR".equalsIgnoreCase(eid)
                    || ("LOG".equalsIgnoreCase(eid) && !"api_access".equalsIgnoreCase(event.edataType()))) {
                LOGGER.debug(null,"Skipping error and non api_access log events");
                sink.incrementSkippedCount(event);
                return;
            }

            boolean isValidationRequired = false;

            if (summaryRouteEventPrefix.contains(eid)) {
                event = getDenormalizedEvent(event);
                isValidationRequired = true;
                // sink.toSuccessTopic(event);
            } else if (eid.startsWith("ME_")) {
                LOGGER.debug(null, "Ignoring as eid is other than WFS");
                sink.incrementSkippedCount(event);
            } else {
                // ignore past data (older than last X months)
                if (event.isOlder(config.ignorePeriodInMonths())) {
                    LOGGER.debug(null, "Ignoring as ets is older than N months");
                    event.markDenormalizationFailure("older than " + config.ignorePeriodInMonths() + " months", config);
                    sink.toFailedTopic(event);
                } else {
                    event = getDenormalizedEvent(event);
                    isValidationRequired = true;
                    // sink.toSuccessTopic(event);
                }
            }

            if (isValidationRequired) {
                ProcessingReport report = schemaValidator.validate(event);
                if (report.isSuccess()) {
                    LOGGER.info("VALIDATION SUCCESS", event.did());
                    event.markSuccess();
                    sink.toSuccessTopic(event);
                } else {
                    String fieldName = schemaValidator.getInvalidFieldName(report.toString());
                    LOGGER.error(null, "VALIDATION FAILED: " + report.toString());
                    event.markValidationFailure(String.format("Invalid field:%s", fieldName), config);
                    sink.toFailedTopic(event);
                }
            }

        } catch (JsonSyntaxException e) {
            LOGGER.error(null, "INVALID EVENT: " + source.getMessage());
            sink.toMalformedTopic(source.getMessage());
        } catch (Exception e) {
            LOGGER.error(null,
                    format("EXCEPTION. PASSING EVENT THROUGH AND ADDING IT TO EXCEPTION TOPIC. EVENT: {0}, EXCEPTION:",
                            event), e);
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