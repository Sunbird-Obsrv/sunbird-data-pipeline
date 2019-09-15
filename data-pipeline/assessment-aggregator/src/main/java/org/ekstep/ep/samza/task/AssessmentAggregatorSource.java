package org.ekstep.ep.samza.task;

import com.google.gson.Gson;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.ekstep.ep.samza.domain.BatchEvent;

import java.util.Map;

public class AssessmentAggregatorSource {
    private IncomingMessageEnvelope envelope;

    public AssessmentAggregatorSource(IncomingMessageEnvelope envelope) {
        this.envelope = envelope;
    }

    public Object getMessage()
    {
        return envelope.getMessage();
    }

    @SuppressWarnings("unchecked")
    public BatchEvent getEvent() {
        String message = (String) envelope.getMessage();
        Map<String, Object> jsonMap = (Map<String, Object>) new Gson().fromJson(message, Map.class);
        return new BatchEvent(jsonMap);
    }

}

