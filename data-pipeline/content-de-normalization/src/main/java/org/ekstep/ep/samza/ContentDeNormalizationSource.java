package org.ekstep.ep.samza;

import org.apache.samza.system.IncomingMessageEnvelope;

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
