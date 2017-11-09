package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.reader.Telemetry;
import org.ekstep.ep.samza.reader.TelemetryReaderException;

import java.util.Map;

public class Actor {
    private String id;
    private String type;
    private final String defaultType = "User";

    public Actor() { }

    public Actor(Map<String, Object> source) throws TelemetryReaderException {
        Telemetry reader = new Telemetry(source);
        this.id = reader.mustReadValue("uid");
        this.type = defaultType;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}
