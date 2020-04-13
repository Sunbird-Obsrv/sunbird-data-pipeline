package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;

public class TelemetryExtractorConfig {
	
	private final String JOB_NAME = "TelemetryExtractor";
	private final String errorTopic;
	private String successTopic;
	private String assessTopic;
	private String defaultChannel;
	private final int dupStore;
	private int expirySeconds;
	private int rawIndividualEventMaxSize;

	public TelemetryExtractorConfig(Config config) {
		successTopic = config.get("output.success.topic.name", "telemetry.raw");
		assessTopic = config.get("output.assess.topic.name", "telemetry.assess.redact");
		errorTopic = config.get("output.error.topic.name", "telemetry.extractor.failed");
		defaultChannel = config.get("default.channel", "01250894314817126443");
		dupStore = config.getInt("redis.database.duplicationstore.id", 1);
		expirySeconds = config.getInt("redis.database.key.expiry.seconds", 432000);
		rawIndividualEventMaxSize = config.getInt("raw.individual.event.maxsize", 996148);
	}

	public String successTopic() {
		return successTopic;
	}
	
	public String assessTopic() {
    return assessTopic;
  }

	public String errorTopic() {
		return errorTopic;
	}

	public String jobName() {
		return JOB_NAME;
	}

	public String defaultChannel() {
		return defaultChannel;
	}

	public int dupStore() {
		return dupStore;
	}

	public int expirySeconds() {
		return expirySeconds;
	}

	public int rawIndividualEventMaxSize() {
		return rawIndividualEventMaxSize;
	}
}