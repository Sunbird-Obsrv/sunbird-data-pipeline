package org.ekstep.ep.samza.service;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.apache.samza.config.Config;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.DeviceProfile;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.task.TelemetryLocationUpdaterConfig;
import org.ekstep.ep.samza.task.TelemetryLocationUpdaterSink;
import org.ekstep.ep.samza.task.TelemetryLocationUpdaterSource;
import org.ekstep.ep.samza.util.DeviceProfileCache;
import org.ekstep.ep.samza.util.RedisConnect;
import redis.clients.jedis.Jedis;
import com.google.gson.Gson;
import redis.clients.jedis.exceptions.JedisException;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class TelemetryLocationUpdaterService {

	private static Logger LOGGER = new Logger(TelemetryLocationUpdaterService.class);
	private DeviceProfileCache deviceProfileCache;
	private int userStoreDb;
	private Jedis userDataStoreConnection;
	private JobMetrics metrics;
	private Gson gson = new Gson();
	private Type mapType = new TypeToken<Map<String, Object>>() { }.getType();

	public TelemetryLocationUpdaterService(DeviceProfileCache deviceLocationCache, JobMetrics metrics, RedisConnect redisConnect, Config config) {
		this.deviceProfileCache = deviceLocationCache;
		this.metrics = metrics;
		this.userStoreDb = config.getInt("redis.database.userStore.id", 4);
		this.userDataStoreConnection = redisConnect.getConnection(userStoreDb);
	}

	public void process(TelemetryLocationUpdaterSource source, TelemetryLocationUpdaterSink sink) {
		try {
			Event event = source.getEvent();
			String did = event.did();
			DeviceProfile deviceProfile = null;
			if (did != null && !did.isEmpty()) {
				// check for user profile
				Map<String, String> derivedLocation = getLocationFromUserCache(event);
				// get device profile from cache
				deviceProfile = deviceProfileCache.getDeviceProfileForDeviceId(did);

				// check for user declared location if user profile is empty
				if (derivedLocation.isEmpty()) {
					derivedLocation = getUserDeclaredLocation(deviceProfile);
				}
				if (derivedLocation.isEmpty()) {
					derivedLocation = getIpResolvedLocation(deviceProfile);
				}
				// Add derived location to telemetry
				updateEventWithDerivedLocation(event, derivedLocation);
				// Add device profile details to the event
				updateEvent(event, deviceProfile);
				metrics.incProcessedMessageCount();
				sink.toSuccessTopic(event);
			}
			else {
				updateEvent(event, deviceProfile);
				metrics.incUnprocessedMessageCount();
				sink.toSuccessTopic(event);
			}
		} catch (JsonSyntaxException e) {
			LOGGER.error(null, "INVALID EVENT: " + source.getMessage());
			sink.toMalformedTopic(source.getMessage());
		}
	}

	private Map<String, String> getLocationFromUserCache(Event event) {
		String uid = event.actorid();
		Map<String, Object> userCacheData;
		Map<String, String> locationData = new HashMap<>();
		try {
			String data = userDataStoreConnection.get(uid);
			if (data != null && !data.isEmpty()) {
				userCacheData = gson.fromJson(data, mapType);
				if (!userCacheData.isEmpty()){
					if(userCacheData.containsKey("state")) {
						locationData.put("state", userCacheData.get("state").toString());
						locationData.put("type", "user-profile");
					}
					if(userCacheData.containsKey("district"))
						locationData.put("district", userCacheData.get("district").toString());
				}
			}
			return locationData;
		}
		catch (JedisException ex) {
			LOGGER.error("", "Exception when reading from user redis cache", ex);
			// add retry logic
			return locationData;
		}
	}

	private Map<String, String> getUserDeclaredLocation(DeviceProfile deviceProfile) {
		Map<String,String> data = deviceProfile.toMap();
		Map<String, String> locationData = new HashMap<>();
		if(!data.isEmpty() && data.containsKey("user_declared_state") && !data.get("user_declared_state").isEmpty()) {
			locationData.put("state", data.get("user_declared_state").toString());
			locationData.put("district", data.getOrDefault("user_declared_district", "").toString());
			locationData.put("type", "user-declared");
			return locationData;
		}
		else { return locationData; }
	}

	private Map<String, String> getIpResolvedLocation(DeviceProfile deviceProfile) {
		Map<String,String> data = deviceProfile.toMap();
		Map<String, String> locationData = new HashMap<>();
		if(!data.isEmpty() && data.containsKey("state")) {
			locationData.put("state", data.get("state").toString());
			locationData.put("district", data.getOrDefault("district_custom", "").toString());
			locationData.put("type", "device-profile");
			return locationData;
		}
		else { return locationData; }
	}

	private void updateEventWithDerivedLocation(Event event, Map<String, String> derivedLocationData) {
		event.addDerivedLocation(derivedLocationData);
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
			} else {
				event.setFlag(TelemetryLocationUpdaterConfig.getDeviceLocationJobFlag(), false);
			}
		} else {
			event.setFlag(TelemetryLocationUpdaterConfig.getDeviceProfileJobFlag(), false);
		}


	}
}
