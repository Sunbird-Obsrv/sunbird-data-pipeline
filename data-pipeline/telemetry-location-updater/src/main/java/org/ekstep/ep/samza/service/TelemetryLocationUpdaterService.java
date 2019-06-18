package org.ekstep.ep.samza.service;

import com.google.gson.JsonSyntaxException;

import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.DeviceProfile;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.task.TelemetryLocationUpdaterConfig;
import org.ekstep.ep.samza.task.TelemetryLocationUpdaterSink;
import org.ekstep.ep.samza.task.TelemetryLocationUpdaterSource;
import org.ekstep.ep.samza.util.DeviceProfileCache;

public class TelemetryLocationUpdaterService {

	private static Logger LOGGER = new Logger(TelemetryLocationUpdaterService.class);
	private DeviceProfileCache deviceProfileCache;
	private JobMetrics metrics;


	public TelemetryLocationUpdaterService(DeviceProfileCache deviceLocationCache, JobMetrics metrics) {
		this.deviceProfileCache = deviceLocationCache;
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

	private void updateEventWithIPLocation(Event event) {
		String did = event.did();
		DeviceProfile deviceProfile = null;
		if (did != null && !did.isEmpty()) {
			deviceProfile = deviceProfileCache.getDeviceProfileForDeviceId(event.did());
			updateEvent(event, deviceProfile);
			metrics.incProcessedMessageCount();
		} else {
			updateEvent(event, deviceProfile);
			metrics.incUnprocessedMessageCount();
		}
	}

	public void updateEvent(Event event, DeviceProfile deviceProfile) {
		event.removeEdataLoc();
		if (null != deviceProfile) {
			event.addDeviceProfile(deviceProfile);
			if (deviceProfile.isDeviceProfileResolved()) {
				event.setFlag(TelemetryLocationUpdaterConfig.getDeviceProfileJobFlag(), true);
			} else {
				event.setFlag(TelemetryLocationUpdaterConfig.getDeviceProfileJobFlag(), false);
			}
			if (deviceProfile.isLocationResolved()) {
				event.setFlag(TelemetryLocationUpdaterConfig.getDeviceLocationJobFlag(), true);
			}
			else
			{
				event.setFlag(TelemetryLocationUpdaterConfig.getDeviceLocationJobFlag(), false);
			}
		} else {
			event.setFlag(TelemetryLocationUpdaterConfig.getDeviceProfileJobFlag(), false);
		}


	}
}
