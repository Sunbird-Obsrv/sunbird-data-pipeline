package org.ekstep.ep.samza.task;

import org.apache.samza.task.MessageCollector;
import org.ekstep.ep.samza.core.BaseSink;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.domain.Event;

public class TelemetryRouterSink extends BaseSink {

	private TelemetryRouterConfig config;

	public TelemetryRouterSink(MessageCollector collector, JobMetrics metrics, TelemetryRouterConfig config) {
		super(collector, metrics);
		this.config = config;
	}

	public void toPrimaryRoute(Event event) {
		event.setTimestamp();
		toTopic(config.getPrimaryRouteTopic(), event.did(), event.getJson());
		metrics.incPrimaryRouteSuccessCounter();
	}

	public void toErrorTopic(Event event, String errorMessage) {
		event.markFailure(errorMessage, config);
		toTopic(config.failedTopic(), event.mid(), event.getJson());
		metrics.incErrorCounter();
	}

	public void toMalformedTopic(String message) {
		toTopic(config.malformedTopic(), null, message);
		metrics.incErrorCounter();
	}

	public void toSecondaryRoute(Event event) {
		toTopic(config.getSecondaryRouteTopic(), event.did(), event.getJson());
		metrics.incSecondaryRouteSuccessCounter();
	}

	public void toLogRoute(Event event) {
		toTopic(config.getLogRouteTopic(), event.did(), event.getJson());
		metrics.incLogRouteSuccessCounter();
	}

	public void toErrorRoute(Event event) {
		toTopic(config.getErrorRouteTopic(), event.did(), event.getJson());
		metrics.incErrorRouteSuccessCounter();
	}

	public void toAuditRoute(Event event) {
		toTopic(config.getAuditRouteTopic(), event.mid(), event.getJson());
		metrics.incAuditRouteSuccessCounter();
	}

	public void toShareEventRouter(Event event) {
		toTopic(config.getShareEventRouterTopic(), event.mid(), event.getJson());
		metrics.incShareEventRouteSuccessCounter();
	}

}
