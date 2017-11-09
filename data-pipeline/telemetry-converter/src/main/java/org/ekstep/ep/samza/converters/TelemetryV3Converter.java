package org.ekstep.ep.samza.converters;

import org.ekstep.ep.samza.domain.Actor;
import org.ekstep.ep.samza.domain.TelemetryV3;
import org.ekstep.ep.samza.reader.Telemetry;
import org.ekstep.ep.samza.reader.TelemetryReaderException;

import java.util.Map;

public class TelemetryV3Converter {
    private final Map<String, Object> source;
    private final Telemetry reader;

    public TelemetryV3Converter(Map<String, Object> source) {
        this.source = source;
        this.reader = new Telemetry(source);
    }

    public TelemetryV3 convert() throws TelemetryReaderException {
        TelemetryV3 v3 = convertEnvelope();
        return v3;
    }

    private TelemetryV3 convertEnvelope() throws TelemetryReaderException {
        TelemetryV3 v3 = new TelemetryV3();

        v3.setEid(reader.<String>mustReadValue("eid"));
        v3.setEts(reader.<Long>mustReadValue("ets"));
        v3.setMid(reader.<String>mustReadValue("mid"));
        v3.setActor(new Actor(source));

        return v3;
    }
}
