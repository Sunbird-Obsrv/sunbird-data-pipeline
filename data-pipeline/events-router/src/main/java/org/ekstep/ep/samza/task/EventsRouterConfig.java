package org.ekstep.ep.samza.task;


import java.util.Arrays;
import java.util.List;

import org.apache.samza.config.Config;

public class EventsRouterConfig {

    private final String JOB_NAME = "EventsRouter";
    
    private String failedTopic;
    private String duplicateTopic;
    private String metricsTopic;
    private String telemetryEventsRouteTopic;
    private String summaryEventsRouteTopic;
    private String summaryRouteEvents;
    private String malformedTopic;
    private String logEventsRouteTopic;
    private Boolean dedupEnabled;
    private final int dupStore;
    private int expirySeconds;

    public EventsRouterConfig(Config config) {
        failedTopic = config.get("output.failed.topic.name", "telemetry.failed");
        duplicateTopic = config.get("output.duplicate.topic.name", "telemetry.duplicate");
        metricsTopic = config.get("output.metrics.topic.name", "telemetry.pipeline_metrics");
        telemetryEventsRouteTopic = config.get("router.events.telemetry.route.topic", "events.telemetry");
        summaryRouteEvents = config.get("router.events.summary.route.events", "ME_WORKFLOW_SUMMARY");
        summaryEventsRouteTopic = config.get("router.events.summary.route.topic", "events.summary");
        malformedTopic = config.get("output.malformed.topic.name", "telemetry.malformed");
        logEventsRouteTopic = config.get("router.events.log.route.topic", "events.log");
        dedupEnabled = config.getBoolean("dedup.enabled", true);
        dupStore = config.getInt("redis.database.duplicationstore.id", 8);
        expirySeconds = config.getInt("redis.database.key.expiry.seconds", 1296000);
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

    public String duplicateTopic() {
        return duplicateTopic;
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

    public String getLogEventsRouteTopic() { return logEventsRouteTopic; }

    public Boolean isDedupEnabled() { return dedupEnabled; }

    public int dupStore() {
        return dupStore;
    }

    public int getExpirySeconds() {
        return expirySeconds;
    }
}