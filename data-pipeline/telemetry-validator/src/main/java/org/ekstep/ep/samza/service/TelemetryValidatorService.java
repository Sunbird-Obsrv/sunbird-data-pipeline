package org.ekstep.ep.samza.service;

import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.google.gson.JsonSyntaxException;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.task.TelemetryValidatorConfig;
import org.ekstep.ep.samza.task.TelemetryValidatorSink;
import org.ekstep.ep.samza.task.TelemetryValidatorSource;
import org.ekstep.ep.samza.util.TelemetrySchemaValidator;

import static java.text.MessageFormat.format;

public class TelemetryValidatorService {
    private static Logger LOGGER = new Logger(TelemetryValidatorService.class);
    private final TelemetryValidatorConfig config;
    private TelemetrySchemaValidator telemetrySchemaValidator;

    public TelemetryValidatorService(TelemetryValidatorConfig config, TelemetrySchemaValidator telemetrySchemaValidator) {
        this.config = config;
        this.telemetrySchemaValidator = telemetrySchemaValidator;
    }

    public void process(TelemetryValidatorSource source, TelemetryValidatorSink sink) {
        Event event = null;
        try {
            event = source.getEvent();
            if (!telemetrySchemaValidator.schemaFileExists(event)) {
                LOGGER.info("SCHEMA NOT FOUND FOR EID: ", event.eid());
                LOGGER.debug("SKIP PROCESSING: SENDING TO SUCCESS", event.mid());
                event.markSkipped();
                sink.toSuccessTopic(event);
                return;
            }

            ProcessingReport report = telemetrySchemaValidator.validate(event);

            if (report.isSuccess()) {
                LOGGER.debug("VALIDATION SUCCESS", event.mid());
                event.markSuccess();
                event.updateDefaults(config);
                sink.toSuccessTopic(dataCorrection(event));
            } else {
                LOGGER.error(null, "VALIDATION FAILED: " + report.toString());
                String fieldName = getInvalidFieldName(report.toString());
                sink.toFailedTopic(event, String.format("validation failed. fieldname: %s", fieldName));
            }
        } catch (JsonSyntaxException e) {
            LOGGER.error(null, "INVALID EVENT: " + source.getMessage());
            sink.toMalformedEventsTopic(source.getMessage());
        } catch (Exception e) {
            LOGGER.error(null, format(
                    "EXCEPTION. PASSING EVENT THROUGH AND ADDING IT TO EXCEPTION TOPIC. EVENT: {0}, EXCEPTION:",
                    event), e);
            sink.toErrorTopic(event, e.getMessage());
        }
    }

    public String getInvalidFieldName(String errorInfo) {
        String[] message = errorInfo.split("reports:");
        String noFieldNameMsg = "unable to get the field name";
        if (message.length > 1) {
            String[] fields = message[1].split(",");
            if (fields.length > 2) {
                String[] pointer = fields[3].split("\"pointer\":");
                return pointer[1].substring(0, pointer[1].length() - 1);
            }
        }
        return noFieldNameMsg;
    }

    public Event dataCorrection(Event event) {
        // Remove prefix from federated userIds
        String eventActorId = event.actorId();
        if (eventActorId != null && !eventActorId.isEmpty() && eventActorId.startsWith("f:")) {
            event.updateActorId(eventActorId.substring(eventActorId.lastIndexOf(":") + 1));
        }

        if (event.eid() != null && event.eid().equalsIgnoreCase("SEARCH")) {
            event.correctDialCodeKey();
        }

        if (event.objectFieldsPresent() && (event.objectType().equalsIgnoreCase("DialCode") || event.objectType().equalsIgnoreCase("qr"))) {
            event.correctDialCodeValue();
        }
        return event;
    }

}
