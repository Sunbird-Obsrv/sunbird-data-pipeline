package org.ekstep.ep.samza.config;

import org.ekstep.ep.samza.domain.Event;

import java.text.ParseException;
import java.util.List;

/**
 * Created by aks on 27/07/17.
 */
public class EventConfig implements Comparable<EventConfig>{
  private String esIndexValue;
  private String esIndexType;
  private Double weight;
  private EsIndexDateConfig esIndexDateConfig;
  private boolean cumulative;
  private List<RuleConfig> ruleConfigs;

  public EventConfig(List<RuleConfig> ruleConfigs, String esIndexValue, Double weight, EsIndexDateConfig esIndexDateConfig, boolean cumulative, String esIndexType){
    this.ruleConfigs = ruleConfigs;
    this.esIndexValue = esIndexValue;
    this.weight = weight;
    this.esIndexDateConfig = esIndexDateConfig;
    this.cumulative = cumulative;
    this.esIndexType = esIndexType;
  }

  public boolean isApplicable(Event event){
    for(RuleConfig ruleConfig : ruleConfigs)
      if(!ruleConfig.isApplicable(event))
        return false;
    return true;
  }

  @Override
  public int compareTo(EventConfig other) {
    final int BEFORE = -1;
    final int EQUAL = 0;
    final int AFTER = 1;

    if (this == other) return EQUAL;
    if (this.weight > other.weight) return BEFORE;
    if (this.weight < other.weight) return AFTER;
    return EQUAL;
  }

  public void update(Event event) throws ParseException {
    event.addEsIndex(esIndexValue,cumulative, esIndexDateConfig, esIndexType);
  }
}
