package org.ekstep.ep.samza.service;

import static java.text.MessageFormat.format;

import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.task.TelemetryRedacterSink;
import org.ekstep.ep.samza.task.TelemetryRedacterSource;

public class TelemetryRedacterService {

	private static Logger LOGGER = new Logger(TelemetryRedacterService.class);

	public TelemetryRedacterService() {
	}

	public void process(TelemetryRedacterSource source, TelemetryRedacterSink sink) {
		Event event = null;
		try {
			event = source.getEvent();
			event.clearUserInput();
			sink.toRedactedRoute(event);
		} catch (Exception e) {
			LOGGER.error(null,
					format("EXCEPTION. PASSING EVENT THROUGH AND ADDING IT TO EXCEPTION TOPIC. EVENT: {0}, EXCEPTION:",
							event),
					e);
			sink.toErrorTopic(source.getMessage());
		}
	}
}
