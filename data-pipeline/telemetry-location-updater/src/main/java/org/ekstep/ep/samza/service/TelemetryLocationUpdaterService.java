package org.ekstep.ep.samza.service;

import static java.text.MessageFormat.format;

import java.util.List;

import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Event;

import com.google.gson.JsonSyntaxException;
import org.ekstep.ep.samza.task.TelemetryLocationUpdaterConfig;
import org.ekstep.ep.samza.task.TelemetryLocationUpdaterSink;
import org.ekstep.ep.samza.task.TelemetryLocationUpdaterSource;

public class TelemetryLocationUpdaterService {
	
	private static Logger LOGGER = new Logger(TelemetryLocationUpdaterService.class);
	private final TelemetryLocationUpdaterConfig config;

	public TelemetryLocationUpdaterService(TelemetryLocationUpdaterConfig config) {
		this.config = config;
	}

	public void process(TelemetryLocationUpdaterSource source, TelemetryLocationUpdaterSink sink) {
		Event event = null;
		try {
			event = source.getEvent();
			sink.toSuccessTopic(event);
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
