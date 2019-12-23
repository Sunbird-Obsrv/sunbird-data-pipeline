package org.ekstep.ep.samza.domain;

import com.google.gson.Gson;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchEvent {
    private final Telemetry telemetry;

    public BatchEvent(Map<String, Object> map) {
        this.telemetry = new Telemetry(map);

    }

    public String getJson() {
        Gson gson = new Gson();
        return gson.toJson(telemetry.getMap());
    }

    public Long assessmentEts() {
        NullableValue<Object> ets = telemetry.read("assessmentTs");
        if (ets.value().getClass().equals(Double.class)) {
            return ((Double) ets.value()).longValue();
        }
        return ((Long) ets.value());
    }


    public String courseId() {
        NullableValue<String> courseid = telemetry.read("courseId");
        return courseid.value();
    }

    public String contentId() {
        NullableValue<String> worksheetId = telemetry.read("contentId");
        return worksheetId.value();
    }

    public String batchId() {
        NullableValue<String> batchId = telemetry.read("batchId");
        return batchId.value();
    }

    public String attemptId() {
        NullableValue<String> attemptId = telemetry.read("attemptId");
        return attemptId.value();
    }

    public String userId() {
        NullableValue<String> userId = telemetry.read("userId");
        return userId.value();
    }

    public List<Map<String, Object>> assessEvents() {
        NullableValue<List<Map<String, Object>>> assessEvents = telemetry.read("events");
        return assessEvents.value();
    }

    public void markSkipped(BatchEvent batchEvent) {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.addFieldIfAbsent("metadata", new HashMap<String, Object>());
        telemetry.add("flags.batch_aggregate_assess_skipped", true);
        telemetry.add("metadata.aggregate_assess", batchEvent.attemptId() +
                " : skipping batch Event older than last batch assessment time");
    }


    @Override
    public String toString() {
        return "BatchEvent{" +
                "telemetry=" + telemetry +
                '}';
    }
}

