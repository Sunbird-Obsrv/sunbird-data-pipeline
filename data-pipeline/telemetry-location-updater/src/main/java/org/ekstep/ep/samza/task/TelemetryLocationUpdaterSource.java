package org.ekstep.ep.samza.task;

import com.google.gson.Gson;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Event;

import java.util.Map;

public class TelemetryLocationUpdaterSource {
    static Logger LOGGER = new Logger(TelemetryLocationUpdaterSource.class);
    
    private IncomingMessageEnvelope envelope;

    public TelemetryLocationUpdaterSource(IncomingMessageEnvelope envelope) {
        this.envelope = envelope;
    }

    public Event getEvent(){
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
