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
    if ("ASSESS".equals(eid())) {
      telemetry.add("edata.resvalues", new ArrayList<>());
    } else {
      telemetry.add("edata.values", new ArrayList<>());
    }
  }

  public String questionId() {
    if ("ASSESS".equals(eid())) {
      NullableValue<String> itemId = telemetry.<String>read("edata.item.id");
      return itemId.isNull() ? null : itemId.value();
    } else {
      NullableValue<String> itemId = telemetry.<String>read("edata.target.id");
      return itemId.isNull() ? null : itemId.value();
    }
  }

  public List<Object> readResValues() {
    if ("ASSESS".equals(eid())) {
      return telemetry.<List<Object>>read("edata.resvalues").value();
    } else {
      return telemetry.<List<Object>>read("edata.values").value();
    }
  }

}
