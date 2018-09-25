package org.ekstep.ep.samza.task;


import java.util.Arrays;
import java.util.List;

import org.apache.samza.config.Config;

public class TelemetryRouterConfig {

    private final String JOB_NAME = "TelemetryValidator";
    
    private String failedTopic;
    private String metricsTopic;
    private String primaryRouteTopic;
    private String secondaryRouteTopic;
    private String secondaryRouteEvents;
    private String malformedTopic;

    public TelemetryRouterConfig(Config config) {
        failedTopic = config.get("output.failed.topic.name", "telemetry.failed");
        metricsTopic = config.get("output.metrics.topic.name", "telemetry.pipeline_metrics");
        primaryRouteTopic = config.get("router.events.primary.route.topic", "telemetry.sink");
        secondaryRouteEvents = config.get("router.events.secondary.route.events", "LOG,ERROR");
        secondaryRouteTopic = config.get("router.events.secondary.route.topic", "telemetry.log");
        malformedTopic = config.get("output.malformed.topic.name", "telemetry.malformed");
        
    }

    public String getPrimaryRouteTopic() {
		return primaryRouteTopic;
	}

	public void setPrimaryRouteTopic(String primaryRouteTopic) {
		this.primaryRouteTopic = primaryRouteTopic;
	}

	public String getSecondaryRouteTopic() {
		return secondaryRouteTopic;
	}

	public void setSecondaryRouteTopic(String secondaryRouteTopic) {
		this.secondaryRouteTopic = secondaryRouteTopic;
	}

	public List<String> getSecondaryRouteEvents() {
		String[] events = this.secondaryRouteEvents.split(",");
		return Arrays.asList(events);
	}

	public void setSecondaryRouteEvents(String secondaryRouteEvents) {
		this.secondaryRouteEvents = secondaryRouteEvents;
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