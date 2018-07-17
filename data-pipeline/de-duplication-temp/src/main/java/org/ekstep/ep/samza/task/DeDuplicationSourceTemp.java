package org.ekstep.ep.samza.task;

import org.apache.samza.system.IncomingMessageEnvelope;
import org.ekstep.ep.samza.domain.Event;
import com.google.gson.Gson;
import org.ekstep.ep.samza.logger.Logger;

import java.util.Map;

public class DeDuplicationSourceTemp {
    static Logger LOGGER = new Logger(DeDuplicationSourceTemp.class);

    private IncomingMessageEnvelope envelope;

    public DeDuplicationSourceTemp(IncomingMessageEnvelope envelope) {
        this.envelope = envelope;
    }

    public Event getEvent(){
        String message = (String) envelope.getMessage();
        Map<String, Object> jsonMap = (Map<String, Object>) new Gson().fromJson(message, Map.class);
        return new Event(jsonMap);
    }

    public String getMessage() {
        return envelope.toString();
    }
}
