package org.ekstep.ep.samza.domain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;
import org.ekstep.ep.samza.task.TelemetryLocationUpdaterConfig;

import com.google.gson.Gson;
import org.ekstep.ep.samza.util.Path;

public class Event {

	private final Telemetry telemetry;
	private Path path;

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

	public void addUserLocation(Location location) {
		Map<String, String> userLoc = new HashMap<>();
		if (location.getState() == null) location.setState("");
		if (location.getDistrict() == null) location.setDistrict("");
		userLoc.put("state", location.getState());
		userLoc.put("district", location.getDistrict());
		if (location.isStateDistrictResolved()) {
			setFlag(TelemetryLocationUpdaterConfig.getUserLocationJobFlag(), true);
		} else if(location.getDistrict().equals("") && location.getState().equals("")) {
			setFlag(TelemetryLocationUpdaterConfig.getUserLocationJobFlag(), false);
		} else {
			setFlag(TelemetryLocationUpdaterConfig.getUserLocationJobFlag(), true);
		}
		telemetry.add(path.userData(), userLoc);
	}

	public void addLocation(Location location) {
		Map<String, String> ldata = new HashMap<>();
		ldata.put("countrycode", location.getCountryCode());
		ldata.put("country", location.getCountry());
		ldata.put("statecode", location.getStateCode());
		ldata.put("state", location.getState());
		ldata.put("city", location.getCity());
		ldata.put("statecustomcode", location.getstateCodeCustom());
		ldata.put("statecustomname", location.getstateCustomName());
		ldata.put("districtcustom", location.getDistrictCustom());
		telemetry.add(path.deviceData(), ldata);
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
