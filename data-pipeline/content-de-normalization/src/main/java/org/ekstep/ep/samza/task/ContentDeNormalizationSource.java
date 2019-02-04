package org.ekstep.ep.samza.task;

import com.google.gson.Gson;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Event;

import java.util.Map;

public class ContentDeNormalizationSource {
	static Logger LOGGER = new Logger(ContentDeNormalizationSource.class);

	private IncomingMessageEnvelope envelope;

	public ContentDeNormalizationSource(IncomingMessageEnvelope envelope) {
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
