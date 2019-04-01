package org.ekstep.ep.samza.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonSyntaxException;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.task.DruidEventsValidatorConfig;
import org.ekstep.ep.samza.task.DruidEventsValidatorSink;
import org.ekstep.ep.samza.task.DruidEventsValidatorSource;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

import static java.text.MessageFormat.format;

public class DruidEventsValidatorService {
    static Logger LOGGER = new Logger(DruidEventsValidatorService.class);
    private final DruidEventsValidatorConfig config;

    public DruidEventsValidatorService(DruidEventsValidatorConfig config) {
        this.config = config;
    }

    public void process(DruidEventsValidatorSource source, DruidEventsValidatorSink sink, JsonSchemaFactory jsonSchemaFactory) {
        Event event = null;
        try {
            event = source.getEvent();
            String schema = getSchema(event);
            if (schema == null) {
                LOGGER.info("SCHEMA FILE DOESN'T EXIST HENCE SKIPPING THE VALIDATION PROCESS AND SENDING TO SUCCESS TOPIC", event.mid());
                event.markSkipped();
                sink.toSuccessTopic(event);
                return;
            }
            JsonNode schemaJson = JsonLoader.fromString(schema);
            JsonNode eventJson = JsonLoader.fromString(event.getJson());
            JsonSchema jsonSchema = jsonSchemaFactory.getJsonSchema(schemaJson);
            ProcessingReport report = jsonSchema.validate(eventJson);
            if (report.isSuccess()) {
                LOGGER.info("VALIDATION SUCCESS", event.mid());
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
        sink.setMetricsOffset(source.getSystemStreamPartition(),source.getOffset());
    }

    private String getInvalidFieldName(String errorInfo) {
        String[] message = errorInfo.split("reports:");
        String[] fields = message[1].split(",");
        String[] pointer = fields[3].split("\"pointer\":");
        return pointer[1].substring(0, pointer[1].length() - 1);
    }

    private String getSchema(Event event) {
        String telemetrySchemaFilePath;
        String summaryEventSchemaFilepath;
        String schema = "";
        InputStream is = null;
        try {
            telemetrySchemaFilePath = MessageFormat.format("{0}/{1}", config.telemetrySchemaPath(), event.schemaName());
            summaryEventSchemaFilepath = MessageFormat.format("{0}/{1}", config.summarySchemaPath(), event.schemaName());
            is = this.getClass().getClassLoader().getResourceAsStream(event.isSummaryEvent() ? summaryEventSchemaFilepath : telemetrySchemaFilePath);
            if (is == null) {
                telemetrySchemaFilePath = MessageFormat.format("{0}/{1}", config.telemetrySchemaPath(), config.defaultSchemafile());
                summaryEventSchemaFilepath = MessageFormat.format("{0}/{1}", config.summarySchemaPath(), config.defaultSchemafile());
                is = this.getClass().getClassLoader().getResourceAsStream(event.isSummaryEvent() ? summaryEventSchemaFilepath : telemetrySchemaFilePath);
            }
            schema = new String(ByteStreams.toByteArray(is));
        } catch (IOException e) {
            LOGGER.error(null, e.getMessage());
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException error) {
                LOGGER.error(null, error.getMessage());
            }
        }
        return schema;
    }
}
