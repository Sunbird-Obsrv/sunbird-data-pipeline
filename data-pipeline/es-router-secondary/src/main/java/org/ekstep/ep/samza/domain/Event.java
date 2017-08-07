package org.ekstep.ep.samza.domain;

import org.apache.samza.system.SystemStream;
import org.ekstep.ep.samza.EsRouter;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aks on 02/08/17.
 */
public class Event {

  private Telemetry telemetry;
  private SystemStream systemStream;

  public Event(Map<String, Object> eventMap, SystemStream systemStream) {
    telemetry = new Telemetry(eventMap);
    this.systemStream = systemStream;
  }

  public void process(EsRouter esRouter) {
    telemetry.addFieldIfAbsent("metadata",new HashMap<String,Object>());
    esRouter.updateEsIndex(telemetry,systemStream.getStream());
    telemetry.add("metadata.source",systemStream.getStream());
  }

  public NullableValue<String> id() {
    if(telemetry!= null)
      return telemetry.read("metadata.checksum");
    return new NullableValue<String>(null);
  }

  public Map<String, Object> getMap() {
    return telemetry.getMap();
  }
}
