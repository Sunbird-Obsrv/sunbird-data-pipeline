package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;

public class TelemetryRedacterConfig {

  private final String JOB_NAME = "TelemetryRedacter";

  private String redactedRouteTopic;
  private String nonRedactedRouteTopic;
  private String failedTopic;

  public TelemetryRedacterConfig(Config config) {
    redactedRouteTopic = config.get("redacted.route.topic", "telemetry.raw");
    failedTopic = config.get("failed.topic.name", "telemetry.failed");
    nonRedactedRouteTopic = config.get("nonredacted.route.topic", "telemetry.assess.raw");
  }

  public String getRedactedRouteTopic() {
    return redactedRouteTopic;
  }

  public String getNonRedactedRouteTopic() {
    return nonRedactedRouteTopic;
  }

  public String jobName() {
    return JOB_NAME;
  }

  public String failedTopic() {
    return failedTopic;
  }

}