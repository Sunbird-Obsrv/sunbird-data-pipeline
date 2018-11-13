package org.ekstep.ep.samza.service;

import static java.text.MessageFormat.format;

import org.apache.samza.storage.kv.KeyValueStore;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.domain.Location;
import com.google.gson.JsonSyntaxException;
import org.ekstep.ep.samza.engine.LocationEngine;
import org.ekstep.ep.samza.task.TelemetryLocationUpdaterConfig;
import org.ekstep.ep.samza.task.TelemetryLocationUpdaterSink;
import org.ekstep.ep.samza.task.TelemetryLocationUpdaterSource;
import org.ekstep.ep.samza.util.LocationCache;
import org.ekstep.ep.samza.util.LocationSearchServiceClient;


public class TelemetryLocationUpdaterService {
	
	private static Logger LOGGER = new Logger(TelemetryLocationUpdaterService.class);
	private final TelemetryLocationUpdaterConfig config;
	private LocationCache cache;
	private KeyValueStore<String, Location> locationStore;
	private LocationSearchServiceClient searchService;

	public TelemetryLocationUpdaterService(TelemetryLocationUpdaterConfig config, LocationCache cache, KeyValueStore<String, Location> locationStore, LocationSearchServiceClient searchService) {
		this.config = config;
		this.cache = cache;
		this.locationStore = locationStore;
		this.searchService = searchService;
	}

	public void process(TelemetryLocationUpdaterSource source, TelemetryLocationUpdaterSink sink) {
		Event event = null;
		Location location = null;
		try {
			event = source.getEvent();
			location = cache.getLocationForDeviceId(event.did());

			if (location != null) {
				event = updateEvent(event, location, true);
			} else {
				// add default location from ORG search API
				LocationEngine engine = new LocationEngine(locationStore, searchService);
				location = engine.getLocation(event.channel());
				if (location != null) {
					event = updateEvent(event, location, true);
				}
				else{
					// add empty location
					location = new Location();
					location.setState("");
					location.setDistrict("");
					event = updateEvent(event, location, false);
				}
			}
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

	public Event updateEvent(Event event, Location location, Boolean ldataFlag) {
		event.addLocation(location);
		event.removeEdataLoc();
		event.setFlag("ldata_obtained", ldataFlag);
		return event;
	}
}
