package org.ekstep.ep.samza.domain;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;
import org.ekstep.ep.samza.task.EsIndexerConfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class Event {
    private final Telemetry telemetry;

    public Event(Map<String, Object> message) {
        this.telemetry = new Telemetry(message);
    }

    public Map<String, Object> getMap() {
        return telemetry.getMap();
    }

    public String indexName(EsIndexerConfig config, String streamName) {
    	
    	String indexName = config.indexMapping().get(streamName);
        if (StringUtils.equalsIgnoreCase("default", indexName)) {
            String eid = telemetry.<String>read("eid").value();
            if (eid == null) {
                indexName = config.failedTelemetryIndex();
            } else if (eid.startsWith("ME_")) {
                indexName = config.derivedIndex();
                if (isCumulativeEvent()) {
                    indexName = config.derivedCumulativeIndex();
                }
            } else {
                indexName = config.primaryIndex();
            }
        }
    	
        return indexName;
    }

    public String indexType() {
        return "events";
    }
    
    public boolean isCumulativeEvent() {
    	NullableValue<String> granularity = telemetry.read("context.granularity");
    	return StringUtils.equals("CUMULATIVE", granularity.value());
    }

    public String id() {
        return telemetry.<String>read("mid").value();
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

    @Override
    public String toString() {
        return "Event{" +
                "telemetry=" + telemetry +
                '}';
    }
}
