package org.ekstep.ep.samza;

import java.text.ParseException;
import java.util.List;

import static org.ekstep.ep.samza.Constants.DEFAULT_INDEX_TYPE;

/**
 * Created by aks on 27/07/17.
 */
public class EventConfig implements Comparable<EventConfig>{
  private String esIndexValue;
  private String esIndexType;
  private Double weight;
  private EsIndexDate esIndexDate;
  private boolean cumulative;
  private List<Rule> rules;

  public EventConfig(List<Rule> rules, String esIndexValue, Double weight, EsIndexDate esIndexDate, boolean cumulative, String esIndexType){
    this.rules = rules;
    this.esIndexValue = esIndexValue;
    this.weight = weight;
    this.esIndexDate = esIndexDate;
    this.cumulative = cumulative;
    this.esIndexType = esIndexType;
  }

  public boolean isApplicable(Event event){
    for(Rule rule:rules)
      if(!rule.isApplicable(event))
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
    event.addEsIndex(esIndexValue,cumulative, esIndexDate, esIndexType);
  }
}
