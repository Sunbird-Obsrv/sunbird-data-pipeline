package org.ekstep.ep.samza.domain;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;
import org.ekstep.ep.samza.task.TelemetryLocationUpdaterConfig;
import org.ekstep.ep.samza.util.Path;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Event {

	private final Telemetry telemetry;
	private Path path;
	private Gson gson = new Gson();
	private Type type = new TypeToken<Map<String, Object>>() {
	}.getType();

	public Event(Map<String, Object> map) {
		this.telemetry = new Telemetry(map);
		path = new Path();
	}

	public Map<String, Object> getMap() {
		return telemetry.getMap();
	}

	public String getJson() {
		Gson gson = new Gson();
		String json = gson.toJson(getMap());
		return json;
	}

	public String mid() {
		NullableValue<String> checksum = telemetry.read("mid");
		return checksum.value();
	}

	public String did() {
		NullableValue<String> did = telemetry.read("dimensions.did");
		return did.isNull() ? telemetry.<String>read("context.did").value() : did.value();
	}

	public String channel() {
		NullableValue<String> channel = telemetry.read("dimensions.channel");
		return channel.isNull() ? telemetry.<String>read("context.channel").value() : channel.value();
	}

	public String eid() {
		NullableValue<String> eid = telemetry.read("eid");
		return eid.value();
	}

	public String actorid() {
		NullableValue<String> actorid = telemetry.read("actor.id");
		return actorid.value();
	}

	public String actortype() {
		NullableValue<String> actortype = telemetry.read("actor.type");
		return actortype.value();
	}

	@Override
	public String toString() {
		return "Event{" + "telemetry=" + telemetry + '}';
	}

	public void markFailure(String error, TelemetryLocationUpdaterConfig config) {
		telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
		telemetry.add("flags.tr_processed", false);

		telemetry.addFieldIfAbsent("metadata", new HashMap<String, Object>());
		telemetry.add("metadata.tr_error", error);
		telemetry.add("metadata.src", config.jobName());
	}

	public void setTimestamp() {
		Double ets = safelyParse(path.ets());
		SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

		if (ets != null) {
			String updatedTs = simple.format(new Date(ets.longValue()));
			telemetry.add(path.ts(), updatedTs);
		} else {
			telemetry.add(path.ts(), simple.format(new Date()));
		}
	}

	private Double safelyParse(String etsField) {
		try {
			NullableValue<Double> time = telemetry.read(etsField);
			return time.value();
		} catch (ClassCastException e) {
			NullableValue<Long> timeInLong = telemetry.read(etsField);
			return Double.valueOf(timeInLong.value());
		}
	}

	public void addDeviceProfile(DeviceProfile deviceProfile) {
		Map<String, Object> ldata = new HashMap<>();
		ldata.put("countrycode", deviceProfile.getCountryCode());
		ldata.put("country", deviceProfile.getCountry());
		ldata.put("statecode", deviceProfile.getStateCode());
		ldata.put("state", deviceProfile.getState());
		ldata.put("city", deviceProfile.getCity());
		ldata.put("statecustomcode", deviceProfile.getstateCodeCustom());
		ldata.put("statecustomname", deviceProfile.getstateCustomName());
		ldata.put("districtcustom", deviceProfile.getDistrictCustom());
		ldata.put("devicespec", deviceProfile.getDevicespec());
		ldata.put("uaspec", deviceProfile.getUaspec());
		ldata.put("firstaccess", deviceProfile.getFirstaccess());
		String iso3166statecode = addISOStateCodeToDeviceProfile(deviceProfile);
		if (!iso3166statecode.isEmpty()) {
			ldata.put("iso3166statecode", iso3166statecode);
		}
		telemetry.add(path.deviceData(), ldata);
	}

	public String addISOStateCodeToDeviceProfile(DeviceProfile deviceProfile) {
		// add new statecode field
		String statecode = deviceProfile.getStateCode();
		if (statecode != null && !statecode.isEmpty()) {
			return "IN-" + statecode;
		} else return "";
	}

	public void removeEdataLoc() {
		Gson gson = new Gson();
		JsonObject json = gson.toJsonTree(getMap()).getAsJsonObject().get("edata").getAsJsonObject();
		json.remove("loc");
		telemetry.add(path.edata(), json);
	}

	public void setFlag(String key, Object value) {
		NullableValue<Map<String, Object>> telemetryFlag = telemetry.read(path.flags());
		Map<String, Object> flags = telemetryFlag.isNull() ? new HashMap<>() : telemetryFlag.value();
		flags.put(key, value);
		telemetry.add(path.flags(), flags);
	}

}
