package org.ekstep.ep.samza;

import java.text.ParseException;
import java.util.List;

/**
 * Created by aks on 27/07/17.
 */
public class EsIndexerConfig {
  private List<TopicConfig> topicConfigs;

  public EsIndexerConfig(List<TopicConfig> topicConfigs) {
    this.topicConfigs = topicConfigs;
  }

  public void updateEsIndex(Event event){

    try {
      for(TopicConfig config:topicConfigs)
        if(config.isApplicable(event))
          config.updateEsIndex(event);
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }
}
