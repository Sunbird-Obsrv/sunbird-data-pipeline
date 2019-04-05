package org.ekstep.ep.samza.task;

import com.google.gson.Gson;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.SystemStreamPartition;
import org.ekstep.ep.samza.core.Logger;

import java.util.Map;

public class RedisUpdaterSource {
    static Logger LOGGER = new Logger(RedisUpdaterSource.class);

    private IncomingMessageEnvelope envelope;

    public RedisUpdaterSource(IncomingMessageEnvelope envelope) {
        this.envelope = envelope;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getMap() {
        String message = (String) envelope.getMessage();
        return (Map<String, Object>) new Gson().fromJson(message, Map.class);
    }

    public String getMessage() {
        return envelope.toString();
    }

    public SystemStreamPartition getSystemStreamPartition() {
        return envelope.getSystemStreamPartition();
    }

    public String getOffset() {
        return envelope.getOffset();
    }
}

