package org.ekstep.ep.samza.eventData;

import org.ekstep.ep.samza.Event;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BackendData {
    static Logger LOGGER = new Logger(Event.class);
    private Telemetry telemetry;
    private List<String> backendEvents;

    public BackendData(Telemetry telemetry, List<String> backendEvents) {
        this.telemetry = telemetry;
        this.backendEvents = backendEvents;
    }

    public void initialize() {
        if (isBackendEvent())
            setBackendTrue();
    }

    private void setBackendTrue() {
        NullableValue<String> id = telemetry.<String>read("metadata.checksum");
        LOGGER.info(id.value(), "ADDING BACKEND EVENT TYPE");
        telemetry.add("backend", "true");
    }

    public boolean isBackendEvent() {
        for (String events : backendEvents) {
            Pattern p = Pattern.compile(events);
            NullableValue<String> eid = telemetry.read("eid");
            if (eid.isNull())
                return false;
            Matcher m = p.matcher(eid.value());
            if (m.matches()) {
                LOGGER.info(m.toString(), "FOUND BACKEND EVENT");
                return true;
            }
        }
        return false;
    }

}
