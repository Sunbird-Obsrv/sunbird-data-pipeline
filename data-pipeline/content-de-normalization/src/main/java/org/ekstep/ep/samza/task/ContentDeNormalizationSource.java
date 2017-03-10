package org.ekstep.ep.samza.task;

import org.apache.samza.system.IncomingMessageEnvelope;
import org.ekstep.ep.samza.domain.Event;

import java.util.HashMap;
import java.util.Map;

public class ContentDeNormalizationSource {


    private final HashMap<String, Object> contentTaxonomy;
    private IncomingMessageEnvelope envelope;

    public ContentDeNormalizationSource(IncomingMessageEnvelope envelope, HashMap<String, Object> contentTaxonomy) {
        this.envelope = envelope;
        this.contentTaxonomy = contentTaxonomy;
    }

    public Event getEvent() {
        return new Event((Map<String, Object>) envelope.getMessage(), contentTaxonomy);
    }
}
