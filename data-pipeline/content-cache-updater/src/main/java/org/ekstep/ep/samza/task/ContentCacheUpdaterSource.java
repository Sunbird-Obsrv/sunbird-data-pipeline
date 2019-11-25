package org.ekstep.ep.samza.task;

import com.google.gson.Gson;
import org.apache.samza.system.IncomingMessageEnvelope;

import java.util.Map;

public class ContentCacheUpdaterSource {

    private IncomingMessageEnvelope envelope;

    public ContentCacheUpdaterSource(IncomingMessageEnvelope envelope) {
        this.envelope = envelope;
    }

    public Map<String, Object> getMap() {
        String message = (String) envelope.getMessage();
        return (Map<String, Object>) new Gson().fromJson(message, Map.class);
    }
}
