package org.ekstep.ep.samza;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by aks on 27/07/17.
 */
public class TopicConfig {
  private String name;
  private List<EventConfig> eventConfigs;

  public TopicConfig(String name, List<EventConfig> eventConfigs) {
    this.name = name;
    this.eventConfigs = eventConfigs;
  }

  public void updateEsIndex(Event event) throws ParseException {
    ArrayList<EventConfig> filteredConfigs = new ArrayList<EventConfig>();
    for(EventConfig config:eventConfigs){
      if(config.isApplicable(event))
        filteredConfigs.add(config);
    }
    if(filteredConfigs.size() == 0){
      return ;
    }
    Collections.sort(filteredConfigs);
    filteredConfigs.get(0).update(event);
  }

  public boolean isApplicable(Event event){
    return event.originatedFrom(name);
  }
}
