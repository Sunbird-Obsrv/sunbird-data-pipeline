package org.ekstep.ep.samza.task;

import com.google.gson.Gson;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.ekstep.ep.samza.domain.Event;

import java.util.Map;

public class UserCacheUpdaterSource {

    private IncomingMessageEnvelope envelope;

    public UserCacheUpdaterSource(IncomingMessageEnvelope envelope) {
        this.envelope = envelope;
    }

    public Event getEvent() {
        String message = (String) envelope.getMessage();
        @SuppressWarnings("unchecked")
        Map<String, Object> jsonMap = (Map<String, Object>) new Gson().fromJson(message, Map.class);
        return new Event(jsonMap);
    }

    public String getMessage() {
        return envelope.toString();
    }
}
