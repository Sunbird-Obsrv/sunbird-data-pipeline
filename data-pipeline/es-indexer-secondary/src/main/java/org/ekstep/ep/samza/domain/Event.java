package org.ekstep.ep.samza.domain;

import com.google.gson.*;
import org.ekstep.ep.samza.reader.Telemetry;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class Event {
    private final Telemetry telemetry;
    private final String defaultIndexName;
    private final String defaultIndexType;

    public Event(Map<String, Object> message, String defaultIndexName, String defaultIndexType) {
        this.telemetry = new Telemetry(message);
        this.defaultIndexName = defaultIndexName;
        this.defaultIndexType = defaultIndexType;
    }

    public Map<String, Object> getMap() {
        return telemetry.getMap();
    }

    public String indexName() {
        return telemetry.<String>read("metadata.index_name").value();
    }

    public String failedIndexName() {
        return indexName() != null && !indexName().isEmpty() ? indexName() : getFailedIndexName();
    }

    public String failedIndexType() {
        return indexType() != null ? indexType() : getFailedIndexType();
    }

    public String indexType() {
        return telemetry.<String>read("metadata.index_type").value();
    }

    public String id() {
        return telemetry.<String>read("metadata.checksum").value();
    }

    public String getJson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Double.class,  new JsonSerializer<Double>() {
            @Override
            public JsonElement serialize(final Double src, final Type typeOfSrc, final JsonSerializationContext context) {
                BigDecimal value = BigDecimal.valueOf(src.longValue());
                return new JsonPrimitive(value);
            }
        });
        Gson gson = gsonBuilder.create();
        return gson.toJson(telemetry.getMap());
    }

    public void markFailed(String status, String errorMsg) {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.es_indexer_processed", false);

        telemetry.addFieldIfAbsent("metadata", new HashMap<String, Object>());
        telemetry.add("metadata.es_indexer_status", status);
        telemetry.add("metadata.es_indexer_error", errorMsg);
    }

    public void markSkipped() {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.es_indexer_skipped", true);
    }

    public boolean can_be_indexed() {
        return ( (indexName() != null && !indexName().isEmpty()) && (indexType() != null && !indexType().isEmpty()));
    }

    public String getFailedIndexName() {
        DateTime time = new DateTime();
        String year = String.valueOf(time.getYear());
        String month = String.valueOf(time.getMonthOfYear());
        return MessageFormat.format("{0}-{1}.{2}",defaultIndexName,year,month);
    }

    public String getFailedIndexType() {
        return defaultIndexType;
    }

    @Override
    public String toString() {
        return "Event{" +
                "telemetry=" + telemetry +
                '}';
    }
}
