package org.ekstep.ep.samza.task;


import org.apache.samza.config.Config;

public class PortalProfileManagementConfig {

    private String successTopic;
    private String failedTopic;

    public PortalProfileManagementConfig(Config config) {
        successTopic = config.get("output.success.topic.name", "telemetry.portal_profiles");
        failedTopic = config.get("output.failed.topic.name", "telemetry.portal_profiles.fail");
    }

    public String successTopic() {
        return successTopic;
    }

    public String failedTopic() {
        return failedTopic;
    }
}