package org.ekstep.ep.es_router_primary.util;

import org.apache.samza.config.Config;
import static org.ekstep.ep.es_router_primary.util.Constants.*;

public class EsRouterConfig {
  private final String additionalConfigPath;
  private final String successTopic;
  private final String failedTopic;
  private final String metricsTopic;
  private final String jobName;

  public EsRouterConfig(Config config) {
    additionalConfigPath = config.get(AdditionalConfigPathKey, AdditionalConfigDefaultPath);
    successTopic = config.get(SuccessTopic, SuccessTopicDefault);
    failedTopic = config.get(FailedTopic, FailedTopicDefault);
    metricsTopic = config.get("output.metrics.topic.name", "pipeline_metrics");
    jobName = config.get("output.metrics.job.name", "EsRouterPrimary");
  }

  public String additionConfigPath(){
    return additionalConfigPath;
  }

  public String successTopic(){
    return successTopic;
  }

  public String failedTopic(){
    return failedTopic;
  }

  public String metricsTopic() {
    return metricsTopic;
  }

  public String jobName() {
    return jobName;
  }
}
