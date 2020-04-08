package org.ekstep.ep.samza.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ekstep.ep.samza.events.domain.Events;

public class Event extends Events {

  public Event(Map<String, Object> map) {
    super(map);
  }

  public void clearUserInput() {
    telemetry.add("edata.resvalues", new ArrayList<>());
  }

  public List<Object> readResValues() {
    return telemetry.<List<Object>>read("edata.resvalues").value();
  }

}
