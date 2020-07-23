package org.ekstep.ep.samza.task;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.SystemStreamPartition;

import java.lang.reflect.Type;
import java.util.Map;

public class DeviceProfileUpdaterSource {

    private IncomingMessageEnvelope envelope;

    public DeviceProfileUpdaterSource(IncomingMessageEnvelope envelope) {
        this.envelope = envelope;
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getMap() {
        String message = (String) envelope.getMessage();
        Type mapType = new TypeToken<Map<String, String>>(){}.getType();
        return new Gson().fromJson(message, mapType);
    }

    public String getMessage() {
        return envelope.toString();
    }

}
