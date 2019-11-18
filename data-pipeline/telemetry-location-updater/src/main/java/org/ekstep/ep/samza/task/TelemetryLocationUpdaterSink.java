package org.ekstep.ep.samza.task;

import org.apache.samza.task.MessageCollector;
import org.ekstep.ep.samza.core.BaseSink;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.domain.Event;


public class TelemetryLocationUpdaterSink extends BaseSink {

	private TelemetryLocationUpdaterConfig config;

	public TelemetryLocationUpdaterSink(MessageCollector collector, JobMetrics metrics, TelemetryLocationUpdaterConfig config) {
		super(collector, metrics);
		this.config = config;
	}

	public void toSuccessTopic(Event event) {
		toTopic(config.successTopic(), event.did(), event.getJson());
		metrics.incSuccessCounter();
	}

	public void toErrorTopic(Event event, String errorMessage) {
		event.markFailure(errorMessage, config);
		toTopic(config.failedTopic(), event.did(), event.getJson());
		metrics.incErrorCounter();
	}

	public void toMalformedTopic(String message) {
		toTopic(config.malformedTopic(), null, message);
		metrics.incErrorCounter();
	}

}
