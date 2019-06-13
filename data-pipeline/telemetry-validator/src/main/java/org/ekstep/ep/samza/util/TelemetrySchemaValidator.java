package org.ekstep.ep.samza.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.task.TelemetryValidatorConfig;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class TelemetrySchemaValidator {

    private Map<String, JsonSchema> schemaJsons = new HashMap<>();

    public TelemetrySchemaValidator(TelemetryValidatorConfig config) throws IOException, ProcessingException {

        String[] schemaVersions = config.schemaVersion().split(",");

        for (String schemaVersion : schemaVersions) {
            File schemaDirectory = new File(MessageFormat.format("{0}/{1}/", config.schemaPath(), schemaVersion));

            File[] schemaFiles = schemaDirectory.listFiles();

            for (File schemafile : schemaFiles) {
                String schemaKey = String.format("%s-%s", schemaVersion, schemafile.getName());
                schemaJsons.put(schemaKey, JsonSchemaFactory
                        .byDefault().getJsonSchema(JsonLoader.fromFile(schemafile)));

            }
        }

    }

    public boolean isSchemaFileExist(Event event) {
        String schemaKey = String.format("%s-%s", event.version(), event.schemaName());
        return schemaJsons.containsKey(schemaKey);

    }

    public ProcessingReport validate(Event event) throws IOException, ProcessingException {
        JsonNode eventJson = JsonLoader.fromString(event.getJson());
        String schemaKey = String.format("%s-%s", event.version(), event.schemaName());
        ProcessingReport report = schemaJsons.get(schemaKey).validate(eventJson);
        return report;
    }

}
