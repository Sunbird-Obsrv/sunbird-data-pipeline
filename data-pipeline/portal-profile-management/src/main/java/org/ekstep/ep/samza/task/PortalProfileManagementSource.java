package org.ekstep.ep.samza.task;

import org.apache.samza.system.IncomingMessageEnvelope;
import org.ekstep.ep.samza.domain.Event;

import java.util.Map;

public class PortalProfileManagementSource {

    private IncomingMessageEnvelope envelope;

    public PortalProfileManagementSource(IncomingMessageEnvelope envelope) {
        this.envelope = envelope;
    }

    public Event getEvent() {
        return new Event((Map<String, Object>) envelope.getMessage());
    }
}
