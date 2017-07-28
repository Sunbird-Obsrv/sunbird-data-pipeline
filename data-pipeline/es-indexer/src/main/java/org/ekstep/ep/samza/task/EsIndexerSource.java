package org.ekstep.ep.samza.task;

import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.ekstep.ep.samza.domain.Event;

import java.util.HashMap;
import java.util.Map;

public class EsIndexerSource {

    private EsIndexerConfig config;
    private IncomingMessageEnvelope envelope;

    public EsIndexerSource(IncomingMessageEnvelope envelope, EsIndexerConfig config) {
        this.envelope = envelope;
        this.config = config;
    }

    public Event getEvent() {
        return new Event((Map<String, Object>) envelope.getMessage(), config.getDefaultIndexName(), config.getDefaultIndexType());
    }
}
