package org.ekstep.ep.samza.service;

import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Telemetry;
import org.ekstep.ep.samza.task.TelemetryExtractorConfig;
import org.ekstep.ep.samza.task.TelemetryExtractorSink;
import org.ekstep.ep.samza.util.DeDupEngine;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import redis.clients.jedis.exceptions.JedisException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TelemetryExtractorService {

	static Logger LOGGER = new Logger(TelemetryExtractorService.class);

	private DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZoneUTC();
	private JobMetrics metrics;
	private DeDupEngine deDupEngine;
	private String defaultChannel = "";
	private int rawIndividualEventMaxSize;

	public TelemetryExtractorService(TelemetryExtractorConfig config, JobMetrics metrics, DeDupEngine deDupEngine) {
		this.metrics = metrics;
		this.deDupEngine = deDupEngine;
		this.defaultChannel = config.defaultChannel();
		this.rawIndividualEventMaxSize = config.rawIndividualEventMaxSize();
	}

	@SuppressWarnings("unchecked")
	public void process(String message, TelemetryExtractorSink sink) {

		try {
			Map<String, Object> batchEvent = (Map<String, Object>) new Gson().fromJson(message, Map.class);
			long syncts = getSyncTS(batchEvent);
			String syncTimestamp = df.print(syncts);
			if (batchEvent.containsKey("params")) {
				String msgid = "";
				try {
					Map<String, Object> params = (Map<String, Object>) batchEvent.get("params");
					if (params.containsKey("msgid")) {
						msgid = params.get("msgid").toString();
						if (!deDupEngine.isUniqueEvent(msgid)) {
							sink.toDuplicateTopic(addDuplicateFlag(batchEvent));
							return;
						}
						deDupEngine.storeChecksum(msgid);
					}
				} catch (JedisException ex) {
					metrics.incSkippedCounter();
					LOGGER.error(msgid, "Failed to connect to redis for batch  : " + ex);
				}
			}


			List<Map<String, Object>> events = (List<Map<String, Object>>) batchEvent.get("events");
			for (Map<String, Object> event : events) {
				String json = "";
				try {
					event.put("syncts", syncts);
					event.put("@timestamp", syncTimestamp);
					Map<String, Object> context = (Map<String, Object>) event.get("context");
					String channel = (String) context.get("channel");
					if (StringUtils.isEmpty(channel)) {
						event.put("context", context);
					}
					json = new Gson().toJson(event);
					int eventSizeInBytes = json.getBytes("UTF-8").length;
					if (eventSizeInBytes > rawIndividualEventMaxSize) {
						LOGGER.info("", String.format("Event with mid %s of size %d bytes is greater than %d. " +
								"Sending to error topic", event.get("mid"), eventSizeInBytes, rawIndividualEventMaxSize));
						sink.toErrorTopic(json);
					} else {
						sink.toSuccessTopic(json);
					}
				} catch (Throwable t) {
					LOGGER.info("", "Failed to send extracted event to success topic: " + t.getMessage());
					sink.toErrorTopic(json);
				}
			}
			metrics.incSuccessCounter();
			generateAuditEvent(batchEvent, syncts, syncTimestamp, sink, defaultChannel);
		} catch (Exception ex) {
			LOGGER.error("", "Failed to process events: " + ex);
			sink.toErrorTopic(message);
		}

	}

	private long getSyncTS(Map<String, Object> batchEvent) {

		if (batchEvent.containsKey("syncts")) {
			Object obj = batchEvent.get("syncts");
			if (obj instanceof Number) {
				return ((Number) obj).longValue();
			}
		}

		return System.currentTimeMillis();
	}

	/**
	 * Create LOG event to audit telemetry sync
	 *
	 * @param eventSpec
	 * @param syncts
	 * @param syncTimestamp
	 * @param sink
	 */
	private void generateAuditEvent(Map<String, Object> eventSpec, long syncts, String syncTimestamp,
									TelemetryExtractorSink sink, String defaultChannel) {
		try {
			Telemetry v3spec = new Telemetry(eventSpec, syncts, syncTimestamp, defaultChannel);
			String auditEvent = v3spec.toJson();
			sink.toSuccessTopic(auditEvent);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("", "Failed to generate LOG event: " + e.getMessage());
		}
	}

	public String addDuplicateFlag(Map<String, Object> batchEvent) {
		Map<String, String> flags = new HashMap<>();
		flags.put("extractor_duplicate", "true");
		batchEvent.put("flags", flags);
		return new Gson().toJson(batchEvent);
	}

}
