package org.ekstep.ep.es_router_secondary.util;

import org.apache.samza.config.Config;
import static org.ekstep.ep.es_router_secondary.util.Constants.*;

public class EsRouterConfig {
  private final String additionalConfigPath;
  private final String successTopic;
  private final String failedTopic;

  public EsRouterConfig(Config config) {
    additionalConfigPath = config.get(AdditionalConfigPathKey, AdditionalConfigDefaultPath);
    successTopic = config.get(SuccessTopic, SuccessTopicDefault);
    failedTopic = config.get(FailedTopic, FailedTopicDefault);
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
}
