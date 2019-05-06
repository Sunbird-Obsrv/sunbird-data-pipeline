package org.ekstep.ep.samza.task;

import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.task.MessageCollector;
import org.ekstep.ep.samza.core.BaseSink;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.domain.Event;

import java.text.SimpleDateFormat;


public class TelemetryLocationUpdaterSink extends BaseSink {

	private JobMetrics metrics;
	private TelemetryLocationUpdaterConfig config;
	private SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	
	public TelemetryLocationUpdaterSink(MessageCollector collector, JobMetrics metrics, TelemetryLocationUpdaterConfig config) {
		super(collector);
		this.metrics = metrics;
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

	public void setMetricsOffset(SystemStreamPartition systemStreamPartition, String offset) {
		metrics.setOffset(systemStreamPartition, offset);
	}

}
