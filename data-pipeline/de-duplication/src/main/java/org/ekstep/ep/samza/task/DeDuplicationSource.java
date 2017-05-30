package org.ekstep.ep.samza.task;

import com.google.gson.JsonSyntaxException;
import org.apache.directory.shared.kerberos.components.Checksum;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.ekstep.ep.samza.domain.Event;
import com.google.gson.Gson;
import org.ekstep.ep.samza.logger.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DeDuplicationSource {
    static Logger LOGGER = new Logger(DeDuplicationSource.class);

    private IncomingMessageEnvelope envelope;

    public DeDuplicationSource(IncomingMessageEnvelope envelope) {
        this.envelope = envelope;
    }

    public Event getEvent(){
        String message = (String) envelope.getMessage();
        HashMap<String, Object> jsonObject = new HashMap<String,Object>();
        Map<String, Object> jsonMap = (Map<String, Object>) new Gson().fromJson(message, jsonObject.getClass());
        return new Event(jsonMap);
    }

    public String getMessage() {
        return envelope.toString();
    }

    public Map<String, Object> getMap() {
        String message = (String) envelope.getMessage();
        return (Map<String, Object>) new Gson().fromJson(message, Map.class);
    }
}
