package org.ekstep.ep.samza.service;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.apache.samza.config.Config;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.DeviceProfile;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.events.domain.Path;
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
	private RedisConnect redisConnect;
	private Jedis userDataStoreConnection;
	private JobMetrics metrics;
	private Gson gson = new Gson();
	private Path path = new Path();
	private Type mapType = new TypeToken<Map<String, Object>>() { }.getType();

	public TelemetryLocationUpdaterService(DeviceProfileCache deviceLocationCache, JobMetrics metrics, RedisConnect redisConnect, Config config) {
		this.deviceProfileCache = deviceLocationCache;
		this.metrics = metrics;
		this.userStoreDb = config.getInt("redis.database.userStore.id", 4);
		this.redisConnect = redisConnect;
		this.userDataStoreConnection = this.redisConnect.getConnection(userStoreDb);
	}

	public void process(TelemetryLocationUpdaterSource source, TelemetryLocationUpdaterSink sink) {
		try {
			Event event = source.getEvent();
			// Temporary fix for skipping pipeline metrics
			if (null != event.eid()) {
				String did = event.did();
				DeviceProfile deviceProfile = null;
				if (did != null && !did.isEmpty()) {
					// get device profile from cache
					deviceProfile = deviceProfileCache.getDeviceProfileForDeviceId(did);

					// check for user profile location
					Map<String, String> derivedLocation = getLocationFromUserCache(event);

					// get user declared location if user profile location is empty
					if (derivedLocation.isEmpty()) derivedLocation = getUserDeclaredLocation(deviceProfile);

					// get ip resolved location if user declared location is empty
					if (derivedLocation.isEmpty()) derivedLocation = getIpResolvedLocation(deviceProfile);

					// Add derived location to telemetry
					if (derivedLocation.isEmpty()) {
						metrics.incCacheMissCounter();
						event.setFlag(TelemetryLocationUpdaterConfig.getDerivedLocationJobFlag(), false);
					} else {
						// update derived location metrics
						updateDerivedLocationMetrics(derivedLocation);
						event.addDerivedLocation(derivedLocation);
						event.setFlag(TelemetryLocationUpdaterConfig.getDerivedLocationJobFlag(), true);
					}

					// Add device profile details to the event
					updateEvent(event, deviceProfile);
					metrics.incProcessedMessageCount();
					sink.toSuccessTopic(event);
				} else {
					updateEvent(event, deviceProfile);
					metrics.incUnprocessedMessageCount();
					sink.toSuccessTopic(event);
				}
			}
		} catch (JsonSyntaxException ex) {
			LOGGER.error(null, "INVALID EVENT: " + source.getMessage(), ex);
			sink.toMalformedTopic(source.getMessage());
		}
	}

	private Map<String, String> getLocationFromUserCache(Event event) {
		String uid = event.actorId();
		Map<String, String> locationData;
		try {
			locationData = getLocationForUser(uid);
			return locationData;
		}
		catch (JedisException ex) {
			LOGGER.error(null, "Reconnecting with Redis store due to exception: ", ex);
			this.userDataStoreConnection.close();
			this.userDataStoreConnection = redisConnect.getConnection(userStoreDb);
			return getLocationForUser(uid);
		}
	}

    private Map<String, String> getLocationForUser(String uid) {
        Map<String, Object> userCacheData;
        Map<String, String> locationData = new HashMap<>();
        if (uid != null) {
            String data = userDataStoreConnection.get(uid);
            if (data != null && !data.isEmpty()) {
                userCacheData = gson.fromJson(data, mapType);
                if (!userCacheData.isEmpty() && userCacheData.containsKey("state") && !userCacheData.get("state").toString().isEmpty()) {
                    locationData.put(path.stateKey(), userCacheData.get("state").toString());
                    locationData.put(path.districtKey(), userCacheData.getOrDefault("district", "").toString());
                    locationData.put(path.locDerivedFromKey(), "user-profile");
                }
            }
        }
        return locationData;
    }

	private Map<String, String> getUserDeclaredLocation(DeviceProfile deviceProfile) {
		Map<String, String> locationData = new HashMap<>();
		if (null != deviceProfile) {
			Map<String, String> data = deviceProfile.toMap();
			if (!data.isEmpty() && data.containsKey("user_declared_state") && !data.get("user_declared_state").isEmpty()) {
				locationData.put(path.stateKey(), data.get("user_declared_state").toString());
				locationData.put(path.districtKey(), data.getOrDefault("user_declared_district", "").toString());
				locationData.put(path.locDerivedFromKey(), "user-declared");
			}
		}
		return locationData;
	}

	private Map<String, String> getIpResolvedLocation(DeviceProfile deviceProfile) {
		Map<String, String> locationData = new HashMap<>();
		if (null != deviceProfile) {
			Map<String,String> data = deviceProfile.toMap();
			if(!data.isEmpty() && data.containsKey("state")) {
				locationData.put(path.stateKey(), data.get("state").toString());
				locationData.put(path.districtKey(), data.getOrDefault("district_custom", "").toString());
				locationData.put(path.locDerivedFromKey(), "ip-resolved");
			}
		}
		return locationData;
	}

	private void updateDerivedLocationMetrics(Map<String, String> derivedLocation) {
		String derivedFrom = derivedLocation.get("from");
		if("user-profile".equalsIgnoreCase(derivedFrom)) metrics.incUserCacheHitCount();
		if("user-declared".equalsIgnoreCase(derivedFrom)) metrics.incUserDeclaredHitCount();
		if("ip-resolved".equalsIgnoreCase(derivedFrom)) metrics.incIpLocationHitCount();
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
