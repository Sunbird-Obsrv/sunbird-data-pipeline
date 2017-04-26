package org.ekstep.ep.samza.task;

import org.apache.samza.system.IncomingMessageEnvelope;
import org.ekstep.ep.samza.domain.Event;

import java.util.HashMap;
import java.util.Map;

public class ItemDeNormalizationSource {


    private final HashMap<String, Object> itemTaxonomy;
    private IncomingMessageEnvelope envelope;

    public ItemDeNormalizationSource(IncomingMessageEnvelope envelope, HashMap<String, Object> itemTaxonomy) {
        this.envelope = envelope;
        this.itemTaxonomy= itemTaxonomy;

    }

    public Event getEvent() {
        return new Event((Map<String, Object>) envelope.getMessage(), itemTaxonomy);
    }
}
