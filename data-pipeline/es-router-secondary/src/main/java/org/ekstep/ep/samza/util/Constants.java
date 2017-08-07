package org.ekstep.ep.samza.util;

/**
 * Created by aks on 02/08/17.
 */
public class Constants {
  public static final String AdditionalConfigPathKey = "addition.config.path";
  public static final String AdditionalConfigDefaultPath = "/etc/samza-jobs/es-router-additional-config.json";
  public static final String SuccessTopic = "output.success.topic.name";
  public static final String SuccessTopicDefault = "telemetry.es-sink";
  public static final String FailedTopic = "output.failed.topic.name";
  public static final String FailedTopicDefault = "telemetry.es-sink.fail";
}
