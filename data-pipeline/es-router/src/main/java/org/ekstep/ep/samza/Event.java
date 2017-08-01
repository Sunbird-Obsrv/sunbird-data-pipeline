package org.ekstep.ep.samza;

import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;

import java.text.ParseException;
import java.util.HashMap;

import static org.ekstep.ep.samza.Constants.*;

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

  public boolean originatedFrom(String source){
    if(source == null || source.isEmpty())
      return false;
    return source.equals(this.kafkaSource);
  }

  public void addEsIndex(String esIndex, boolean cumulative, EsIndexDate indexDate, String indexType) throws ParseException {
    telemetry.addFieldIfAbsent(METADATA_KEY, new HashMap<String, String>());
    String effectiveEsIndex = cumulative ? esIndex : indexDate.getIndex(esIndex,this);

    telemetry.add(INDEX_NAME_KEY,effectiveEsIndex);
    telemetry.add(INDEX_TYPE_KEY, indexType);

  }

  public Telemetry getTelemetry(){
    return telemetry;
  }
}
