package org.ekstep.ep.samza.task;


import org.apache.samza.config.Config;

public class PortalProfileManagementConfig {

    private String successTopic;
    private String failedTopic;
    private String cpUpdateProfileEvent;
    private final String objectServiceEndpoint;

    public PortalProfileManagementConfig(Config config) {
        successTopic = config.get("output.success.topic.name", "telemetry.portal_profiles");
        failedTopic = config.get("output.failed.topic.name", "telemetry.portal_profiles.fail");
        cpUpdateProfileEvent = config.get("cp.update.profile.event", "CP_UPDATE_PROFILE");
        objectServiceEndpoint = config.get("object.service.endpoint","");
    }

    public String successTopic() {
        return successTopic;
    }

    public String failedTopic() {
        return failedTopic;
    }

    public String cpUpdateProfileEvent() {
        return cpUpdateProfileEvent;
    }

    public String getObjectServiceEndPoint() {
        return objectServiceEndpoint;
    }
}