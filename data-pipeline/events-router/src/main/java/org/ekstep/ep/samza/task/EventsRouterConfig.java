package org.ekstep.ep.samza.task;


import java.util.Arrays;
import java.util.List;

import org.apache.samza.config.Config;

public class EventsRouterConfig {

    private final String JOB_NAME = "EventsRouter";
    
    private String failedTopic;
    private String metricsTopic;
    private String telemetryEventsRouteTopic;
    private String summaryEventsRouteTopic;
    private String summaryRouteEvents;
    private String malformedTopic;

    public EventsRouterConfig(Config config) {
        failedTopic = config.get("output.failed.topic.name", "telemetry.failed");
        metricsTopic = config.get("output.metrics.topic.name", "telemetry.pipeline_metrics");
        telemetryEventsRouteTopic = config.get("router.events.telemetry.route.topic", "events.telemetry");
        summaryRouteEvents = config.get("router.events.summary.route.events", "ME_");
        summaryEventsRouteTopic = config.get("router.events.summary.route.topic", "events.summary");
        malformedTopic = config.get("output.malformed.topic.name", "telemetry.malformed");
        
    }

    public String getTelemetryEventsRouteTopic() {
		return telemetryEventsRouteTopic;
	}

	public void setTelemetryEventsRouteTopic(String telemetryEventsRouteTopic) {
		this.telemetryEventsRouteTopic = telemetryEventsRouteTopic;
	}

	public String getSummaryEventsRouteTopic() {
		return summaryEventsRouteTopic;
	}

	public void setSummaryEventsRouteTopic(String summaryEventsRouteTopic) {
		this.summaryEventsRouteTopic = summaryEventsRouteTopic;
	}

	public String getSummaryRouteEvents() {
		return this.summaryRouteEvents;
	}

	public void setSummaryRouteEvents(String summaryRouteEvents) {
		this.summaryRouteEvents = summaryRouteEvents;
	}

    public String failedTopic() {
        return failedTopic;
    }

    public String metricsTopic() {
        return metricsTopic;
    }
    
    public String malformedTopic() {
    	return malformedTopic;
    }

    public String jobName() {
        return JOB_NAME;
    }
}