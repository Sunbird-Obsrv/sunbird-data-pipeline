package org.ekstep.ep.samza.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ekstep.ep.samza.events.domain.Events;
import org.ekstep.ep.samza.reader.NullableValue;

public class Event extends Events {

  public Event(Map<String, Object> map) {
    super(map);
  }

  public void clearUserInput() {
    telemetry.add("edata.resvalues", new ArrayList<>());
  }
  
  public String questionId() {
    NullableValue<String> itemId = telemetry.<String>read("edata.item.id");
    return itemId.isNull() ? null : itemId.value();
  }

  public List<Object> readResValues() {
    return telemetry.<List<Object>>read("edata.resvalues").value();
  }

}
