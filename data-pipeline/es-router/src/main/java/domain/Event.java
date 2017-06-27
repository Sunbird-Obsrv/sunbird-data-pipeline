package domain;

import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;

import java.util.HashMap;
import java.util.Map;

public class Event {
    static Logger LOGGER = new Logger(Event.class);
    private final Telemetry telemetry;

    public Event(Map<String, Object> map) {
        this.telemetry = new Telemetry(map);
    }

    public String id() {
        NullableValue<String> checksum = telemetry.read("metadata.checksum");
        return checksum.value();
    }

    public Map<String, Object> getMap() {
        return telemetry.getMap();
    }

    public String getEID() {
        NullableValue<String> eid = telemetry.read("eid");
        return eid.value();
    }

    @Override
    public String toString() {
        return "Event{" +
                "telemetry=" + telemetry +
                '}';
    }
}

