package org.ekstep.ep.es_router_primary.domain;

import org.apache.samza.system.SystemStream;
import org.ekstep.ep.es_router.EsRouter;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;

import java.util.Map;

public class Event {

  private Telemetry telemetry;
  private SystemStream systemStream;

  public Event(Map<String, Object> eventMap, SystemStream systemStream) {
    telemetry = new Telemetry(eventMap);
    this.systemStream = systemStream;
  }

  public void process(EsRouter esRouter) {
    esRouter.updateEsIndex(telemetry,systemStream.getStream());
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
