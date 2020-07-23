package org.ekstep.ep.samza.task;

import java.util.Map;

import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.SystemStreamPartition;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Event;

import com.google.gson.Gson;

public class DeDuplicationSource {
	private static Logger LOGGER = new Logger(DeDuplicationSource.class);

	private IncomingMessageEnvelope envelope;

	public DeDuplicationSource(IncomingMessageEnvelope envelope) {
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
	public SystemStreamPartition getSystemStreamPartition() { return envelope.getSystemStreamPartition();}
	public String getOffset() { return envelope.getOffset();}
}
