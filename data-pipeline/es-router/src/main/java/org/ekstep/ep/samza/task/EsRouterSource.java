package org.ekstep.ep.samza.task;

import domain.Event;
import org.apache.samza.system.IncomingMessageEnvelope;

import java.util.Map;

public class EsRouterSource {

    private IncomingMessageEnvelope envelope;

    public EsRouterSource(IncomingMessageEnvelope envelope) {
        this.envelope = envelope;
    }

    public Event getEvent() {
        return new Event((Map<String, Object>) envelope.getMessage());
    }
}
