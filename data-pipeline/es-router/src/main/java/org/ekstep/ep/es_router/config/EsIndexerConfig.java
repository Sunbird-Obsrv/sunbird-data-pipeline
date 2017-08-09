package org.ekstep.ep.es_router.config;

import org.ekstep.ep.es_router.domain.Event;
import org.ekstep.ep.samza.logger.Logger;

import java.text.ParseException;
import java.util.List;

/**
 * Created by aks on 27/07/17.
 */
public class EsIndexerConfig {
  private List<TopicConfig> topicConfigs;
  static Logger LOGGER = new Logger(EsIndexerConfig.class);
  public EsIndexerConfig(List<TopicConfig> topicConfigs) {
    this.topicConfigs = topicConfigs;
  }

  public void updateEsIndex(Event event){
    try {
      boolean configApplied = false;
      for(TopicConfig config:topicConfigs)
        if(config.isApplicable(event)){
          config.updateEsIndex(event);
          configApplied = true;
        }
        if(!configApplied){
          LOGGER.error(event.id(),String.format("No topic configuration found for the event. Event: %s",event.toString()));
        }
    } catch (ParseException e) {
      LOGGER.error(event.id(),String.format("Parsing Exception. Event: %s",event.toString()),e);
    }
  }
}
