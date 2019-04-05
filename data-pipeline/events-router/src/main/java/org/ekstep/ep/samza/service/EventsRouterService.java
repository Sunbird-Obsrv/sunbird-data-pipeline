package org.ekstep.ep.samza.service;

import static java.text.MessageFormat.format;

import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.task.EventsRouterConfig;
import org.ekstep.ep.samza.task.EventsRouterSink;
import org.ekstep.ep.samza.task.EventsRouterSource;

import com.google.gson.JsonSyntaxException;

public class EventsRouterService {
	
	static Logger LOGGER = new Logger(EventsRouterService.class);
	private final EventsRouterConfig config;

	public EventsRouterService(EventsRouterConfig config) {
		this.config = config;
	}

	public void process(EventsRouterSource source, EventsRouterSink sink) {
		Event event = null;
		try {
			event = source.getEvent();
			String eid = event.eid();
			String summaryRouteEventPrefix = this.config.getSummaryRouteEvents();
			if (eid.startsWith(summaryRouteEventPrefix)) {
				sink.toSummaryEventsTopic(event);
			} else if (eid.startsWith("ME_")) {
				sink.incrementSkippedCount(event);
			}else if(eid.equals("LOG")){
				sink.toLogEventsTopic(event);
			}
			else {
				sink.toTelemetryEventsTopic(event);
			}
		} catch (JsonSyntaxException e) {
			LOGGER.error(null, "INVALID EVENT: " + source.getMessage());
			sink.toMalformedTopic(source.getMessage());
		} catch (Exception e) {
			LOGGER.error(null,
					format("EXCEPTION. PASSING EVENT THROUGH AND ADDING IT TO EXCEPTION TOPIC. EVENT: {0}, EXCEPTION:",
							event),
					e);
			sink.toErrorTopic(event, e.getMessage());
		}
	}
}
