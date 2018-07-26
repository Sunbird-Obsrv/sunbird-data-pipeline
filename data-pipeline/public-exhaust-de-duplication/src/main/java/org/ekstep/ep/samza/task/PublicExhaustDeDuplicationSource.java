package org.ekstep.ep.samza.task;

import org.apache.samza.system.IncomingMessageEnvelope;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.domain.Event;

import java.util.HashMap;
import java.util.Map;

public class PublicExhaustDeDuplicationSource {

    private IncomingMessageEnvelope envelope;

    public PublicExhaustDeDuplicationSource(IncomingMessageEnvelope envelope) {
        this.envelope = envelope;
    }

    public Event getEvent() {
        return new Event((Map<String, Object>) envelope.getMessage());
    }
}
