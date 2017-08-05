package org.ekstep.ep.samza.task;

import org.apache.samza.system.IncomingMessageEnvelope;
import org.ekstep.ep.samza.domain.Event;

import java.util.Map;

public class EsIndexerPrimarySource {

    private EsIndexerPrimaryConfig config;
    private IncomingMessageEnvelope envelope;

    public EsIndexerPrimarySource(IncomingMessageEnvelope envelope, EsIndexerPrimaryConfig config) {
        this.envelope = envelope;
        this.config = config;
    }

    public Event getEvent() {
        return new Event((Map<String, Object>) envelope.getMessage(), config.getDefaultIndexName(), config.getDefaultIndexType());
    }
}
