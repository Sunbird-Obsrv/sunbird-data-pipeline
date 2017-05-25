package org.ekstep.ep.samza.task;

import org.apache.samza.system.IncomingMessageEnvelope;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.reader.Telemetry;

import java.util.Map;

public class ObjectDeNormalizationSource {

    private IncomingMessageEnvelope envelope;

    public ObjectDeNormalizationSource(IncomingMessageEnvelope envelope) {
        this.envelope = envelope;
    }

    public Event getEvent() {
        return new Event(new Telemetry((Map<String, Object>) envelope.getMessage()));
    }

    public Telemetry getTelemetryEvent() {
        return new Telemetry((Map<String, Object>) envelope.getMessage());
    }
}
