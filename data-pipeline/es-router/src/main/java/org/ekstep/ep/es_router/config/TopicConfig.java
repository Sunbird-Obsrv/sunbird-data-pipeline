package org.ekstep.ep.es_router.config;

import org.ekstep.ep.es_router.domain.Event;
import org.ekstep.ep.samza.logger.Logger;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by aks on 27/07/17.
 */
public class TopicConfig {
  private List<String> names;
  private List<EventConfig> eventConfigs;
  static Logger LOGGER = new Logger(TopicConfig.class);

  public TopicConfig(List<String> names, List<EventConfig> eventConfigs) {
    this.names = names;
    this.eventConfigs = eventConfigs;
  }

  public void updateEsIndex(Event event) throws ParseException {
    ArrayList<EventConfig> filteredConfigs = new ArrayList<EventConfig>();
    for(EventConfig config:eventConfigs){
      if(config.isApplicable(event))
        filteredConfigs.add(config);
    }
    if(filteredConfigs.size() == 0){
      LOGGER.error(event.id(),String.format("No event configuration found for the event. Event: %s",event.toString()));
      return ;
    }
    Collections.sort(filteredConfigs);
    filteredConfigs.get(0).update(event);
  }

  public boolean isApplicable(Event event){
    return event.originatedFrom(names);
  }
}
