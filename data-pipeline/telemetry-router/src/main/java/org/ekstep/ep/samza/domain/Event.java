package org.ekstep.ep.samza.domain;

import java.util.HashMap;
import java.util.Map;

import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;
import org.ekstep.ep.samza.task.TelemetryRouterConfig;

import com.google.gson.Gson;

public class Event {

	private final Telemetry telemetry;

	public Event(Map<String, Object> map) {
		this.telemetry = new Telemetry(map);
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

	public String eid() {
		NullableValue<String> eid = telemetry.read("eid");
		return eid.value();
	}

	@Override
	public String toString() {
		return "Event{" + "telemetry=" + telemetry + '}';
	}

	public void markFailure(String error, TelemetryRouterConfig config) {
		telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
		telemetry.add("flags.tr_processed", false);

		telemetry.addFieldIfAbsent("metadata", new HashMap<String, Object>());
		telemetry.add("metadata.tr_error", error);
		telemetry.add("metadata.src", config.jobName());
	}

}
