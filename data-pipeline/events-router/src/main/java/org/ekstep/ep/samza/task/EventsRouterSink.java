package org.ekstep.ep.samza.task;

import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.task.MessageCollector;
import org.ekstep.ep.samza.core.BaseSink;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.domain.Event;

import java.text.SimpleDateFormat;

public class EventsRouterSink extends BaseSink {

	private JobMetrics metrics;
	private EventsRouterConfig config;
	
	public EventsRouterSink(MessageCollector collector, JobMetrics metrics, EventsRouterConfig config) {
		super(collector);
		this.metrics = metrics;
		this.config = config;
	}

	public void toTelemetryEventsTopic(Event event) {
		toTopic(config.getTelemetryEventsRouteTopic(), event.did(), event.getJson());
		metrics.incSuccessCounter();
	}

	public void toErrorTopic(Event event, String errorMessage) {
		event.markFailure(errorMessage, config);
		toTopic(config.failedTopic(), event.did(), event.getJson());
		metrics.incErrorCounter();
	}

	public void toDuplicateTopic(Event event) {
		toTopic(config.duplicateTopic(), event.did(), event.getJson());
		metrics.incFailedCounter();
	}

	public void toMalformedTopic(String message) {
		toTopic(config.malformedTopic(), null, message);
		metrics.incErrorCounter();
	}

	public void toSummaryEventsTopic(Event event) {
		toTopic(config.getSummaryEventsRouteTopic(), event.did(), event.getJson());
		metrics.incSuccessCounter();
	}

	public void incrementSkippedCount(Event event) {
		metrics.incSkippedCounter();
	}

	public void toLogEventsTopic(Event event){
		toTopic(config.getLogEventsRouteTopic(), event.did(), event.getJson());
		metrics.incSuccessCounter();
	}

	public void setMetricsOffset(SystemStreamPartition systemStreamPartition, String offset) {
		metrics.setOffset(systemStreamPartition, offset);
	}
}
