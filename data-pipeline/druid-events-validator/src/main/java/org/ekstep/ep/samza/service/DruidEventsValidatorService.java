package org.ekstep.ep.samza.service;

import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.google.gson.JsonSyntaxException;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.task.DruidEventsValidatorConfig;
import org.ekstep.ep.samza.task.DruidEventsValidatorSink;
import org.ekstep.ep.samza.task.DruidEventsValidatorSource;
import org.ekstep.ep.samza.util.SchemaValidator;

import static java.text.MessageFormat.format;

public class DruidEventsValidatorService {
    private static Logger LOGGER = new Logger(DruidEventsValidatorService.class);
    private final DruidEventsValidatorConfig config;
    private final SchemaValidator schemaValidator;

    public DruidEventsValidatorService(DruidEventsValidatorConfig config, SchemaValidator schemaValidator) {
        this.config = config;
        this.schemaValidator = schemaValidator;
    }

    public void process(DruidEventsValidatorSource source, DruidEventsValidatorSink sink, JsonSchemaFactory jsonSchemaFactory) {
        Event event = null;
        try {
            event = source.getEvent();
            ProcessingReport report = schemaValidator.validate(event);
            if (report.isSuccess()) {
                event.markSuccess();
                sink.toSuccessTopic(event);
            } else {
                String fieldName = this.getInvalidFieldName(report.toString());
                LOGGER.error(null, "VALIDATION FAILED: " + report.toString());
                sink.toFailedTopic(event, "Invalid field:" + fieldName);
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

    private String getInvalidFieldName(String errorInfo) {
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

}
