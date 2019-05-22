package org.ekstep.ep.samza.service;

import static java.text.MessageFormat.format;

import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.domain.Location;
import com.google.gson.JsonSyntaxException;
import org.ekstep.ep.samza.task.TelemetryLocationUpdaterConfig;
import org.ekstep.ep.samza.task.TelemetryLocationUpdaterSink;
import org.ekstep.ep.samza.task.TelemetryLocationUpdaterSource;
import org.ekstep.ep.samza.util.DeviceLocationCache;

public class TelemetryLocationUpdaterService {
	
	private static Logger LOGGER = new Logger(TelemetryLocationUpdaterService.class);
	private DeviceLocationCache deviceLocationCache;
	private JobMetrics metrics;


	public TelemetryLocationUpdaterService(DeviceLocationCache deviceLocationCache, JobMetrics metrics) {
		this.deviceLocationCache = deviceLocationCache;
		this.metrics = metrics;
	}

	public void process(TelemetryLocationUpdaterSource source, TelemetryLocationUpdaterSink sink) {
		try {
			Event event = source.getEvent();
			sink.setMetricsOffset(source.getSystemStreamPartition(), source.getOffset());
			// Add device location details to the event
			updateEventWithIPLocation(event);
			sink.toSuccessTopic(event);
		} catch (JsonSyntaxException e) {
			LOGGER.error(null, "INVALID EVENT: " + source.getMessage());
			sink.toMalformedTopic(source.getMessage());
		}
	}

	private Event updateEventWithIPLocation(Event event) {
		String did = event.did();
		Location location = null;
		if (did != null && !did.isEmpty()) {
			location = deviceLocationCache.getLocationForDeviceId(event.did());
			updateEvent(event, location);
			metrics.incProcessedMessageCount();
		} else {
			event = updateEvent(event, location);
			metrics.incUnprocessedMessageCount();
		}
		return event;
	}

	public Event updateEvent(Event event, Location location) {
		event.removeEdataLoc();
		if (location != null && location.isLocationResolved()) {
			event.addLocation(location);
			event.setFlag(TelemetryLocationUpdaterConfig.getDeviceLocationJobFlag(), true);
		} else {
			event.setFlag(TelemetryLocationUpdaterConfig.getDeviceLocationJobFlag(), false);
		}
		return event;
	}
}
