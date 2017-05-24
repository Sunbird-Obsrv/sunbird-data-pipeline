package org.ekstep.ep.samza.task;


import org.apache.samza.config.Config;

import java.util.ArrayList;
import java.util.List;

public class ObjectLifecycleManagementConfig {

    private final String objectServiceEndpoint;
    private final String lifecycleEvents;
    private String successTopic;
    private String failedTopic;

    public ObjectLifecycleManagementConfig(Config config) {
        successTopic = config.get("output.success.topic.name", "telemetry.objects");
        failedTopic = config.get("output.failed.topic.name", "telemetry.objects.fail");
        objectServiceEndpoint = config.get("create.object.endpoint","");
        lifecycleEvents = config.get("lifecycle.events", "");
    }

    public String successTopic() {
        return successTopic;
    }

    public String failedTopic() {
        return failedTopic;
    }

    public List<String> getLifeCycleEvents() {
        String[] split = lifecycleEvents.split(",");
        List<String> lifecycleEvents = new ArrayList<String>();
        for (String event : split) {
            lifecycleEvents.add(event.trim().toUpperCase());
        }
        return lifecycleEvents;
    }

    public String getObjectServiceEndPoint() {
        return objectServiceEndpoint;
    }
}