package org.ekstep.ep.samza.service;

import static java.text.MessageFormat.format;

import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.domain.Location;
import com.google.gson.JsonSyntaxException;
import org.ekstep.ep.samza.engine.LocationEngine;
import org.ekstep.ep.samza.task.TelemetryLocationUpdaterConfig;
import org.ekstep.ep.samza.task.TelemetryLocationUpdaterSink;
import org.ekstep.ep.samza.task.TelemetryLocationUpdaterSource;

import java.io.IOException;


public class TelemetryLocationUpdaterService {
	
	private static Logger LOGGER = new Logger(TelemetryLocationUpdaterService.class);
	private final TelemetryLocationUpdaterConfig config;
	private LocationEngine locationEngine;


	public TelemetryLocationUpdaterService(TelemetryLocationUpdaterConfig config, LocationEngine locationEngine) {
		this.config = config;
		this.locationEngine = locationEngine;
	}

	public void process(TelemetryLocationUpdaterSource source, TelemetryLocationUpdaterSink sink) {
		Event event = null;
		try {
			event = source.getEvent();
			event = updateEventWithIPLocation(event);
			// add user location details to the event
			event = updateEventWithUserLocation(event);
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

	private Event updateEventWithIPLocation(Event event) throws IOException {
		Location location;
		String did = event.did();
		String channel = event.channel();

		if (did != null && !did.isEmpty()) {
			location = locationEngine.locationCache().getLocationForDeviceId(event.did(), channel);

			if (location != null) {
				event = updateEvent(event, location, true);
			} else {
				// add empty location
				location = new Location("", "", "", "", "");
				event = updateEvent(event, location, false);
			}
		} else {
			// add empty location
			location = new Location("", "", "", "", "");
			event = updateEvent(event, location, false);
		}
		return event;
	}

	private Event updateEventWithUserLocation(Event event) throws IOException {
		String actorId = event.actorid();
		String actorType = event.actortype();
		if (actorId != null && actorType.toUpperCase().equals("USER")) {
			Location location = locationEngine.getLocationByUser(actorId);
			if (location == null) {
				location = locationEngine.getLocation(event.channel());
				if(location == null) {
					event.addUserLocation(new Location(null, null, null, "", null, ""));
					event.setFlag(TelemetryLocationUpdaterConfig.getUserLocationJobFlag(), false);
				} else {
					event.addUserLocation(location);
					event.setFlag(TelemetryLocationUpdaterConfig.getUserLocationJobFlag(), true);
				}
			} else {
				event.addUserLocation(location);
				event.setFlag(TelemetryLocationUpdaterConfig.getUserLocationJobFlag(), true);
			}
		}
		return event;
	}

	private Event updateEventWithLocationFromChannel(Event event) throws IOException {
		Location location = locationEngine.getLocation(event.channel());
		if (location != null && !location.getState().isEmpty()) {
			event = updateEvent(event, location, true);
		} else {
			// add empty location
			location = new Location("", "", "", "", "");
			event = updateEvent(event, location, false);
		}
		return event;
	}

	public Event updateEvent(Event event, Location location, Boolean ldataFlag) {
		event.addLocation(location);
		event.removeEdataLoc();
		event.setFlag(TelemetryLocationUpdaterConfig.getDeviceLocationJobFlag(), ldataFlag);
		return event;
	}
}
