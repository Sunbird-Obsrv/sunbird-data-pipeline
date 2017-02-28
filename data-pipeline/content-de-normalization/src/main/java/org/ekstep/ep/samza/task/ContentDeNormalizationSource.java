package org.ekstep.ep.samza.task;

import org.apache.samza.system.IncomingMessageEnvelope;
import org.ekstep.ep.samza.domain.Event;

import java.util.Map;

public class ContentDeNormalizationSource {

    private IncomingMessageEnvelope envelope;

    public ContentDeNormalizationSource(IncomingMessageEnvelope envelope) {

        this.envelope = envelope;
    }

    public Event getEvent() {
        return new Event((Map<String, Object>) envelope.getMessage());
    }
}
