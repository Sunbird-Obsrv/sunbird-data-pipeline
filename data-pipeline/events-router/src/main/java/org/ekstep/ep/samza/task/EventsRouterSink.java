package org.ekstep.ep.samza.task;

import org.apache.samza.task.MessageCollector;
import org.ekstep.ep.samza.core.BaseSink;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.domain.Event;

public class EventsRouterSink extends BaseSink {

	// private JobMetrics metrics;
	private EventsRouterConfig config;
	
	public EventsRouterSink(MessageCollector collector, JobMetrics metrics, EventsRouterConfig config) {
		super(collector, metrics);
		// this.metrics = metrics;
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
		metrics.incDuplicateCounter();
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

}
