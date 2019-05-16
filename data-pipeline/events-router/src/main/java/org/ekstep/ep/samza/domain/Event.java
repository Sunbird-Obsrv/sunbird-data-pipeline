package org.ekstep.ep.samza.domain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;
import org.ekstep.ep.samza.task.EventsRouterConfig;

import com.google.gson.Gson;
import org.ekstep.ep.samza.util.Path;

public class Event {

	private final Telemetry telemetry;
	private Path path;

	public Event(Map<String, Object> map) {
		this.telemetry = new Telemetry(map);
		path = new Path();
	}

	public String getChecksum() {
		String checksum = id();
		if (checksum != null)
			return checksum;
		return mid();
	}

	public String id() {
		NullableValue<String> checksum = telemetry.read("metadata.checksum");
		return checksum.value();
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

	public void markFailure(String error, EventsRouterConfig config) {
		telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
		telemetry.add("flags.tr_processed", false);

		telemetry.addFieldIfAbsent("metadata", new HashMap<String, Object>());
		telemetry.add("metadata.tr_error", error);
		telemetry.add("metadata.src", config.jobName());
	}

	public void updateTs(String value){
		telemetry.add("@timestamp",value);
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

	public void markSkipped() {
		telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
		telemetry.add("flags.dd_dr_processed", false);
		telemetry.add("flags.dd_dr_checksum_present", false);
	}

	public void markDuplicate() {
		telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
		telemetry.add("flags.dd_dr_processed", false);
		telemetry.add("flags.dd_dr_duplicate_event", true);
	}

}
