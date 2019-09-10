package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;

import java.util.List;
import java.util.Map;

public class BatchEvent {
    private final Telemetry telemetry;

    public BatchEvent(Map<String, Object> map) {
        this.telemetry = new Telemetry(map);

    }

    public Long assessmentets() {
        NullableValue<Object> ets = telemetry.read("assessmentTs");
        if(ets.value().getClass().equals(Double.class)) {
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
        return  batchId.value();
    }

    public String attemptId() {
        NullableValue<String> attemptId = telemetry.read("attemptId");
        return  attemptId.value();
    }

    public String userId() {
        NullableValue<String> userId = telemetry.read("userId");
        return  userId.value();
    }

    public List<Map<String,Object>> assessEvents() {
        NullableValue<List<Map<String,Object>>> assessEvents = telemetry.read("events");
        return  assessEvents.value();
    }



    @Override
    public String toString() {
        return "BatchEvent{" +
                "telemetry=" + telemetry +
                '}';
    }
}

