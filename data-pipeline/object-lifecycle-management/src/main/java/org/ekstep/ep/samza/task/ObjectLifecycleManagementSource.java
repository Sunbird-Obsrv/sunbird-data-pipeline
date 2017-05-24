package org.ekstep.ep.samza.task;

import org.apache.samza.system.IncomingMessageEnvelope;
import org.ekstep.ep.samza.domain.Event;

import java.util.List;
import java.util.Map;

public class ObjectLifecycleManagementSource {

    private final List<String> lifeCycleEvents;
    private IncomingMessageEnvelope envelope;

    public ObjectLifecycleManagementSource(IncomingMessageEnvelope envelope, List<String> lifeCycleEvents) {
        this.envelope = envelope;
        this.lifeCycleEvents = lifeCycleEvents;
    }

    public Event getEvent() {
        return new Event((Map<String, Object>) envelope.getMessage(),lifeCycleEvents);
    }
}
