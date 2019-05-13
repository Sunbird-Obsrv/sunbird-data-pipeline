package org.ekstep.ep.samza.service;

import static java.text.MessageFormat.format;

import java.util.List;

import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.task.TelemetryRouterConfig;
import org.ekstep.ep.samza.task.TelemetryRouterSink;
import org.ekstep.ep.samza.task.TelemetryRouterSource;

import com.google.gson.JsonSyntaxException;

public class TelemetryRouterService {
	
	static Logger LOGGER = new Logger(TelemetryRouterService.class);
	private final TelemetryRouterConfig config;

	public TelemetryRouterService(TelemetryRouterConfig config) {
		this.config = config;
	}

	public void process(TelemetryRouterSource source, TelemetryRouterSink sink) {
		Event event = null;
		try {
			event = source.getEvent();
			sink.setMetricsOffset(source.getSystemStreamPartition(),source.getOffset());
			String eid = event.eid();
			List<String> secondaryRouteEvents = this.config.getSecondaryRouteEvents();
			if (secondaryRouteEvents.contains(eid)) {
				sink.toSecondaryRoute(event);
			} else {
				sink.toPrimaryRoute(event);
			}

		} catch(JsonSyntaxException e){
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
