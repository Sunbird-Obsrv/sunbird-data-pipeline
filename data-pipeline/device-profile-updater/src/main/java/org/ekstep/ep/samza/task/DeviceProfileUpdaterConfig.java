package org.ekstep.ep.samza.task;
import org.apache.samza.config.Config;

public class DeviceProfileUpdaterConfig {

    private final String JOB_NAME = "DeviceProfileUpdater";
    private String malformedTopic;

    public DeviceProfileUpdaterConfig(Config config) {
        malformedTopic = config.get("output.malformed.topic.name", "telemetry.malformed");
    }

    public String malformedTopic() {
        return malformedTopic;
    }

    public String jobName() {
        return JOB_NAME;
    }
}
