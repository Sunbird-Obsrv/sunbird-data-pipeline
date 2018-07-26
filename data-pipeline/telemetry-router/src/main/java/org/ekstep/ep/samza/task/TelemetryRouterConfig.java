package org.ekstep.ep.samza.task;


import java.util.Arrays;
import java.util.List;

import org.apache.samza.config.Config;

public class TelemetryRouterConfig {

    private final String JOB_NAME = "TelemetryValidator";
    private String schemaPath;
    private String successTopic;
    private String failedTopic;
    private String malformedTopic;
    private String defaultChannel;
    private String metricsTopic;
    private String primaryRouteTopic;
    private String secondaryRouteTopic;
    private String secondaryRouteEvents;

    public TelemetryRouterConfig(Config config) {
        successTopic = config.get("output.success.topic.name", "telemetry.router");
        failedTopic = config.get("output.failed.topic.name", "telemetry.router.fail");
        malformedTopic = config.get("output.malformed.topic.name", "telemetry.malformed");
        metricsTopic = config.get("output.metrics.topic.name", "telemetry.pipeline_metrics");
        primaryRouteTopic = config.get("router.events.primary.route.topic", "telemetry.sink");
        secondaryRouteEvents = config.get("router.events.secondary.route.events", "LOG,ERROR");
        secondaryRouteTopic = config.get("router.events.secondary.route.topic", "lane2");
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
		String[] events = secondaryRouteEvents.split(",");
		return Arrays.asList(events);
	}

	public void setSecondaryRouteEvents(String secondaryRouteEvents) {
		this.secondaryRouteEvents = secondaryRouteEvents;
	}

	public String successTopic() {
        return successTopic;
    }

    public String failedTopic() {
        return failedTopic;
    }

    public String malformedTopic() {
        return malformedTopic;
    }

    public String metricsTopic() {
        return metricsTopic;
    }

    public String defaultChannel() {
        return defaultChannel;
    }

    public String schemaPath() { return schemaPath; }

    public String jobName() {
        return JOB_NAME;
    }
}