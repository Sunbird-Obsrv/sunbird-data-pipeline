package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.events.domain.Events;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.task.TelemetryRouterConfig;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Event extends Events {


    public Event(Map<String, Object> map) {
        super(map);
    }

    public void markFailure(String error, TelemetryRouterConfig config) {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.tr_processed", false);

        telemetry.addFieldIfAbsent("metadata", new HashMap<String, Object>());
        telemetry.add("metadata.tr_error", error);
        telemetry.add("metadata.src", config.jobName());
    }

    public void setTimestamp() {
        Double ets = safelyParse(path.ets());
        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        if (ets != null) {
            String updatedTs = simple.format(new Date(ets.longValue()));
            telemetry.add(path.ts(), updatedTs);
        } else {
            telemetry.add(path.ts(), simple.format(new Date()));
        }
    }

    private Double safelyParse(String etsField) {
        try {
            NullableValue<Double> time = telemetry.read(etsField);
            return time.value();
        } catch (ClassCastException e) {
            NullableValue<Long> timeInLong = telemetry.read(etsField);
            return Double.valueOf(timeInLong.value());
        }
    }

}
