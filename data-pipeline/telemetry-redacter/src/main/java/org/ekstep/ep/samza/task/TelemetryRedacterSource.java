package org.ekstep.ep.samza.task;

import java.util.Map;

import org.apache.samza.system.IncomingMessageEnvelope;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Event;

import com.google.gson.Gson;

public class TelemetryRedacterSource {
  
    static Logger LOGGER = new Logger(TelemetryRedacterSource.class);

    private IncomingMessageEnvelope envelope;

    public TelemetryRedacterSource(IncomingMessageEnvelope envelope) {
        this.envelope = envelope;
    }

    public Event getEvent() {
        return new Event(getMap());
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getMap() {
        String message = (String) envelope.getMessage();
        return (Map<String, Object>) new Gson().fromJson(message, Map.class);
    }

    public String getMessage() {
        return envelope.toString();
    }


}
