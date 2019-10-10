package org.ekstep.ep.samza.task;

import java.util.Map;

import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.SystemStreamPartition;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Event;

import com.google.gson.Gson;

public class TelemetryValidatorSource {
    static Logger LOGGER = new Logger(TelemetryValidatorSource.class);
    
    private IncomingMessageEnvelope envelope;

    public TelemetryValidatorSource(IncomingMessageEnvelope envelope) {
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
    public SystemStreamPartition getSystemStreamPartition() { return envelope.getSystemStreamPartition();}
    public String getOffset() { return envelope.getOffset();}

}
