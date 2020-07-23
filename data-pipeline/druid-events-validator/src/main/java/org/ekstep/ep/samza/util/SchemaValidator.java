package org.ekstep.ep.samza.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.google.common.io.ByteStreams;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.task.DruidEventsValidatorConfig;

import java.io.IOException;
import java.text.MessageFormat;

public class SchemaValidator {

    private final JsonSchema telemetryJsonSchema;
    private final JsonSchema summaryJsonSchema;
    private final JsonSchema searchEventJsonSchema;
    private final JsonSchema logEventJsonSchema;
    private JsonSchemaFactory schemaFactory;

    public SchemaValidator(DruidEventsValidatorConfig config) throws IOException, ProcessingException {
        this.schemaFactory = JsonSchemaFactory.byDefault();
        String telemetrySchemaPath = MessageFormat.format("{0}/{1}", config.telemetrySchemaPath(),
                config.defaultSchemafile());
        String summaryEventSchemapath = MessageFormat.format("{0}/{1}", config.summarySchemaPath(),
                config.defaultSchemafile());
        String searchEventSchemaPath = MessageFormat.format("{0}/{1}", config.telemetrySchemaPath(),
                config.searchSchemafile());
        String logEventSchemaPath = MessageFormat.format("{0}/{1}", config.telemetrySchemaPath(),
                config.logSchemafile());

        String telemetrySchema = new String(ByteStreams.toByteArray(this.getClass().getClassLoader().
                getResourceAsStream(telemetrySchemaPath)));
        String summarySchema = new String(ByteStreams.toByteArray(this.getClass().getClassLoader().
                getResourceAsStream(summaryEventSchemapath)));
        String searchEventSchema = new String(ByteStreams.toByteArray(this.getClass().getClassLoader().
                getResourceAsStream(searchEventSchemaPath)));
        String logEventSchema = new String(ByteStreams.toByteArray(this.getClass().getClassLoader().
                getResourceAsStream(logEventSchemaPath)));

        this.telemetryJsonSchema = schemaFactory.getJsonSchema(JsonLoader.fromString(telemetrySchema));
        this.summaryJsonSchema = schemaFactory.getJsonSchema(JsonLoader.fromString(summarySchema));
        this.searchEventJsonSchema = schemaFactory.getJsonSchema(JsonLoader.fromString(searchEventSchema));
        this.logEventJsonSchema = schemaFactory.getJsonSchema(JsonLoader.fromString(logEventSchema));
    }

    public ProcessingReport validate(Event event) throws IOException, ProcessingException {

        JsonNode eventJson = JsonLoader.fromString(event.getJson());
        ProcessingReport report;
        if (event.isSearchEvent()) {
            report = searchEventJsonSchema.validate(eventJson);
        } else if (event.isSummaryEvent()) {
            report = summaryJsonSchema.validate(eventJson);
        } else if (event.isLogEvent()) {
            report = logEventJsonSchema.validate(eventJson);
        } else {
            report = telemetryJsonSchema.validate(eventJson);
        }
        return report;
    }

}
