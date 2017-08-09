package org.ekstep.ep.es_router.domain;

import org.ekstep.ep.es_router.config.EsIndexDateConfig;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import static org.ekstep.ep.es_router.util.Constants.*;

/**
 * Created by aks on 27/07/17.
 */
public class Event {
  private Telemetry telemetry;
  private String kafkaSource;

  public Event(Telemetry telemetry, String kafkaSource) {
    this.telemetry = telemetry;
    this.kafkaSource = kafkaSource;
  }

  public <T> NullableValue<T> read(String path){
    return telemetry.read(path);
  }

  public boolean originatedFrom(List<String> sources){
    if(sources == null || sources.isEmpty())
      return false;
    return sources.contains(this.kafkaSource);
  }

  public void addEsIndex(String esIndex, boolean cumulative, EsIndexDateConfig indexDate, String indexType) throws ParseException {
    telemetry.addFieldIfAbsent(METADATA_KEY, new HashMap<String, String>());
    String effectiveEsIndex = cumulative ? esIndex : indexDate.getIndex(esIndex,this);

    telemetry.add(INDEX_NAME_KEY,effectiveEsIndex);
    telemetry.add(INDEX_TYPE_KEY, indexType);

  }

  public String id(){
    if(telemetry!= null)
      return telemetry.id();
    return "";
  }

  @Override
  public String toString() {
    return "Event{" +
        "telemetry=" + telemetry +
        ", kafkaSource='" + kafkaSource + '\'' +
        '}';
  }

  public Telemetry getTelemetry(){
    return telemetry;
  }
}
