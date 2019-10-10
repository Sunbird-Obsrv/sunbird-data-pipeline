package org.ekstep.ep.samza.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.google.common.io.ByteStreams;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.task.DruidProcessorConfig;

import java.io.IOException;
import java.text.MessageFormat;

public class SchemaValidator {

    private final JsonSchema telemetryJsonSchema;
    private final JsonSchema summaryJsonSchema;
    private final JsonSchema searchEventJsonSchema;
    private JsonSchemaFactory schemaFactory;

    public SchemaValidator(DruidProcessorConfig config) throws IOException, ProcessingException {
        this.schemaFactory = JsonSchemaFactory.byDefault();
        String telemetrySchemaPath = MessageFormat.format("{0}/{1}", config.telemetrySchemaPath(),
                config.defaultSchemafile());
        String summaryEventSchemapath = MessageFormat.format("{0}/{1}", config.summarySchemaPath(),
                config.defaultSchemafile());
        String searchEventSchemaPath = MessageFormat.format("{0}/{1}", config.telemetrySchemaPath(),
                "search.json");

        String telemetrySchema = new String(ByteStreams.toByteArray(this.getClass().getClassLoader().
                getResourceAsStream(telemetrySchemaPath)));
        String summarySchema = new String(ByteStreams.toByteArray(this.getClass().getClassLoader().
                getResourceAsStream(summaryEventSchemapath)));
        String searchEventSchema = new String(ByteStreams.toByteArray(this.getClass().getClassLoader().
                getResourceAsStream(searchEventSchemaPath)));

        this.telemetryJsonSchema = schemaFactory.getJsonSchema(JsonLoader.fromString(telemetrySchema));
        this.summaryJsonSchema = schemaFactory.getJsonSchema(JsonLoader.fromString(summarySchema));
        this.searchEventJsonSchema = schemaFactory.getJsonSchema(JsonLoader.fromString(searchEventSchema));
    }

    public ProcessingReport validate(Event event) throws IOException, ProcessingException {

        JsonNode eventJson = JsonLoader.fromString(event.getJson());
        ProcessingReport report;
        if ("SEARCH".equalsIgnoreCase(event.eid())) {
            report = searchEventJsonSchema.validate(eventJson);
        } else if (event.isSummaryEvent()) {
            report = summaryJsonSchema.validate(eventJson);
        } else {
            report = telemetryJsonSchema.validate(eventJson);
        }
        return report;
    }

    public String getInvalidFieldName(String errorInfo) {
        String[] message = errorInfo.split("reports:");
        String[] fields = message[1].split(",");
        String[] pointer = fields[3].split("\"pointer\":");
        return pointer[1].substring(2, pointer[1].length() - 2);
    }

}
