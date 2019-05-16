package org.ekstep.ep.samza.task;

import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.task.MessageCollector;
import org.ekstep.ep.samza.core.BaseSink;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.domain.Event;

public class DruidProcessorSink extends BaseSink {

	private JobMetrics metrics;
	private DruidProcessorConfig config;

	public DruidProcessorSink(MessageCollector collector, JobMetrics metrics, DruidProcessorConfig config) {
		super(collector);
		this.metrics = metrics;
		this.config = config;
	}

	public void toSuccessTopic(Event event) {
		String eid = event.eid();
		String routeTopic;
		if (config.getSummaryFilterEvents().contains(eid)) {
			routeTopic = config.summaryEventsRouteTopic();
		} else if ("LOG".equals(eid)) {
			routeTopic = config.logEventsRouteTopic();
		} else {
			routeTopic = config.telemetryEventsRouteTopic();
		}
		toTopic(routeTopic, event.did(), event.getJson());
		metrics.incSuccessCounter();
	}

	// public void toFailedTopic(Event event, String failedMessage) {
	public void toFailedTopic(Event event) {
		// event.markDenormalizationFailure(failedMessage, config);
		toTopic(config.failedTopic(), event.did(), event.getJson());
		metrics.incFailedCounter();
	}

	public void toMalformedTopic(String message) {
		toTopic(config.malformedTopic(), null, message);
		metrics.incErrorCounter();
	}

	public void toErrorTopic(Event event, String errorMessage) {
		event.markDenormalizationFailure(errorMessage, config);
		toTopic(config.failedTopic(), event.did(), event.getJson());
		metrics.incErrorCounter();
	}

	public void setMetricsOffset(SystemStreamPartition systemStreamPartition, String offset) {
		metrics.setOffset(systemStreamPartition, offset);
	}

	public void incrementSkippedCount(Event event) {
		metrics.incSkippedCounter();
	}

}
