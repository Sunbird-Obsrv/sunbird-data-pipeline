package org.ekstep.ep.samza.domain;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;
import org.ekstep.ep.samza.task.TelemetryValidatorConfig;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.gson.Gson;

public class Event {

	private DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZoneUTC();
	private final Telemetry telemetry;

	public Event(Map<String, Object> map) {
		this.telemetry = new Telemetry(map);
	}

	public Map<String, Object> getMap() {
		return telemetry.getMap();
	}

	public String getJson() {
		Gson gson = new Gson();
		return gson.toJson(getMap());
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

	public String mid() {
		NullableValue<String> checksum = telemetry.read("mid");
		return checksum.value();
	}

	public String actorId() {
		NullableValue<String> checksum = telemetry.read("actor.id");
		return checksum.value();
	}

	public String eid() {
		NullableValue<String> eid = telemetry.read("eid");
		return eid.value();
	}

	public String pid() {
		NullableValue<String> pid = telemetry.read("context.pdata.pid");
		return pid.value();
	}

	public String schemaName() {
		String eid = eid();
		if (eid != null) {
			return MessageFormat.format("{0}.json", eid.toLowerCase());
		} else {
			return "envelope.json";
		}
	}

	public String version() {
		return (String) telemetry.read("ver").value();
	}

	@Override
	public String toString() {
		return "Event{" + "telemetry=" + telemetry + '}';
	}

	public void markSuccess() {
		telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
		telemetry.add("flags.tv_processed", true);
		telemetry.add("type", "events");
	}

	public void markFailure(String error, TelemetryValidatorConfig config) {
		telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
		telemetry.add("flags.tv_processed", false);

		telemetry.addFieldIfAbsent("metadata", new HashMap<String, Object>());
		if (null != error) {
			telemetry.add("metadata.tv_error", error);
			telemetry.add("metadata.src", config.jobName());
		}

	}

	public void markSkipped() {
		telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
		telemetry.add("flags.tv_skipped", true);
	}

	public void updateActorId(String actorId) {
		telemetry.add("actor.id", actorId);
	}

	public void correctDialCodeKey() {
		NullableValue<Object> dialcodes = telemetry.read("edata.filters.dialCodes");
		if (dialcodes != null && dialcodes.value() != null) {
			telemetry.add("edata.filters.dialcodes", dialcodes.value());
			telemetry.add("edata.filters.dialCodes", null);
		}
	}

	public void updateDefaults(TelemetryValidatorConfig config) {
		String channelString = telemetry.<String>read("context.channel").value();
		String channel = StringUtils.deleteWhitespace(channelString);
		if (channel == null || channel.isEmpty()) {
			telemetry.addFieldIfAbsent("context", new HashMap<String, Object>());
			telemetry.add("context.channel", config.defaultChannel());
		}
		String atTimestamp = telemetry.getAtTimestamp();
		String strSyncts = telemetry.getSyncts();
		if (null == atTimestamp && null == strSyncts) {
			long syncts = System.currentTimeMillis();
			telemetry.addFieldIfAbsent("syncts", syncts);
			telemetry.addFieldIfAbsent("@timestamp", df.print(syncts));
		} else {
			if(atTimestamp != null) {
				telemetry.addFieldIfAbsent("syncts", df.parseMillis(atTimestamp));
			} else if(strSyncts != null) {
				telemetry.addFieldIfAbsent("@timestamp", strSyncts);
			}
		}

	}
}
