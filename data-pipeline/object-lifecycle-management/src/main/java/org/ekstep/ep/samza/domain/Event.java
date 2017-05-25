package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Event {
    static Logger LOGGER = new Logger(Event.class);
    private final Telemetry telemetry;
    private final List<String> lifeCycleEvents;

    public Event(Map<String, Object> map, List<String> lifecycleEvents) {
        this.telemetry = new Telemetry(map);
        this.lifeCycleEvents = lifecycleEvents;
    }

    public String id() {
        NullableValue<String> checksum = telemetry.read("metadata.checksum");
        return checksum.value();
    }

    public boolean canBeProcessed() {
        for (String event : lifeCycleEvents) {
            Pattern p = Pattern.compile(event);
            Matcher m = p.matcher(getEID());
            if (m.matches()) {
                LOGGER.info(m.toString(), "ALLOWING EVENT");
                return true;
            }
        }
        return false;
    }

    public Map<String, Object> getMap() {
        return telemetry.getMap();
    }

    public String getEID() {
        NullableValue<String> eid = telemetry.read("eid");
        return eid.value();
    }

    public Map<String, Object> LifecycleObjectAttributes() {
        NullableValue<Map<String, Object>> telemetryEks = telemetry.read("edata.eks");

        if (!telemetryEks.isNull()) {
            return telemetryEks.value();
        }
        return null;
    }

    public void updateFlags(boolean processed) {
        NullableValue<Map<String, Object>> telemetryFlag = telemetry.read("flags");
        Map<String, Object> flags = telemetryFlag.isNull()
                ? new HashMap<String, Object>()
                : telemetryFlag.value();
        flags.put("lifecycle_data_processed", processed);
        telemetry.add("flags", flags);
    }

    public void updateMetadata(Map<String, Object> params) {
        NullableValue<Map<String, Object>> telemetryMetadata = telemetry.read("metadata");
        Map<String, Object> metadata = telemetryMetadata.isNull()
                ? new HashMap<String, Object>()
                : telemetryMetadata.value();
        metadata.put("lifecycle_data_process_err", params.get("err"));
        metadata.put("lifecycle_data_process_err_msg", params.get("errmsg"));
        telemetry.add("metadata", metadata);
    }

    @Override
    public String toString() {
        return "Event{" +
                "telemetry=" + telemetry +
                '}';
    }
}

