package org.ekstep.ep.samza.task;

import java.util.HashMap;
import java.util.Map;

import org.apache.samza.task.MessageCollector;
import org.ekstep.ep.samza.converter.domain.TelemetryV3;
import org.ekstep.ep.samza.core.BaseSink;
import org.ekstep.ep.samza.core.JobMetrics;

import com.google.gson.Gson;

public class TelemetryConverterSink extends BaseSink {

	private JobMetrics metrics;
	private TelemetryConverterConfig config;

	public TelemetryConverterSink(MessageCollector collector, JobMetrics metrics, TelemetryConverterConfig config) {

		super(collector);
		this.metrics = metrics;
		this.config = config;
	}
	
	public void toSuccessTopic(String mid, String message) {
		toTopic(config.successTopic(), mid, message);
        metrics.incSkippedCounter();
    }

	public void toSuccessTopic(TelemetryV3 v3, Map<String, Object> v2) {
		Map<String, Object> flags = getFlags(v2);
		flags.put("v2_converted", true);

		Map<String, Object> metadata = getMetadata(v2);
		metadata.put("source_eid", v2.getOrDefault("eid", ""));
		metadata.put("source_mid", v2.getOrDefault("mid", ""));
		metadata.put("checksum", v3.getMid());

		Map<String, Object> event = v3.toMap();
		event.put("flags", flags);
		event.put("metadata", metadata);

		if (v2.containsKey("pump")) {
			event.put("pump", "true");
		}

		String json = new Gson().toJson(event);
		toTopic(config.successTopic(), v3.getMid(), json);
		metrics.incSuccessCounter();
	}

	public void toFailedTopic(Map<String, Object> event, Exception ex) {
		Map<String, Object> flags = getFlags(event);
		flags.put("v2_converted", false);
		flags.put("error", ex.getMessage());
		// TODO: Just log the stacktrace. This is not efficient to put stack trace in the message
		flags.put("stack", stacktraceToString(ex.getStackTrace()));

		Map<String, Object> payload = event;
		payload.put("flags", flags);
		payload.put("metadata", getMetadata(event));

		String json = new Gson().toJson(payload);

		toTopic(config.failedTopic(), event.get("mid") != null ? event.get("mid").toString(): null, json);
		metrics.incErrorCounter();
	}

	private String stacktraceToString(StackTraceElement[] stackTrace) {
		String stack = "";
		for (StackTraceElement trace : stackTrace) {
			stack += trace.toString() + "\n";
		}
		return stack;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getFlags(Map<String, Object> event) {
		Map<String, Object> flags;
		if (event.containsKey("flags") && (event.get("flags") instanceof Map)) {
			flags = (Map<String, Object>) event.get("flags");
		} else {
			flags = new HashMap<>();
		}

		return flags;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getMetadata(Map<String, Object> event) {
		Map<String, Object> metadata;
		if (event.containsKey("metadata") && (event.get("metadata") instanceof Map)) {
			metadata = (Map<String, Object>) event.get("metadata");
		} else {
			metadata = new HashMap<>();
		}

		return metadata;
	}
	

}
