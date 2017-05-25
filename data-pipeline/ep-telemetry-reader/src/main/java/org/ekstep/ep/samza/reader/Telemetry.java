package org.ekstep.ep.samza.reader;


import org.ekstep.ep.samza.logger.Logger;

import java.util.Map;

import static java.text.MessageFormat.format;

public class Telemetry {
  private Map<String, Object> map;
  private Logger logger = new Logger(this.getClass());

  public Telemetry(Map<String, Object> map){
    this.map = map;
  }

  public boolean add(String nestedKeys, Object value){
    try {
      NestedMap nestedMap = getNestedMap(map, nestedKeys);
      nestedMap.add(value);
      return true;
    } catch (Exception e) {
      logger.error("",format("Couldn't add value:{0} at  key: {0} for event:{1}",value,nestedKeys,map),e);
    }
    return false;
  }

  public Map<String, Object> getMap(){
    return map;
  }

  public <T> NullableValue<T> read(String nestedKey) {
    try {
      NestedMap nestedMap = getNestedMap(map, nestedKey);
      return new NullableValue<T>(nestedMap.<T>getValue());
    } catch (Exception e) {
      logger.error("",format("Couldn't get key: {0} from event:{1}",nestedKey,map),e);
      return new NullableValue<T>(null);
    }
  }

  private NestedMap getNestedMap(Map<String, Object> map, String nestedKeys){
    Map<String, Object> parentMap = map;
    String[] keys = nestedKeys.split("\\.");
    int lastIndex = keys.length - 1;
    if (keys.length > 1){
      for(int i = 0; i < lastIndex && parentMap != null; i++)
        parentMap = new NestedMap(parentMap,keys[i]).getValue();
    }
    String lastKey = keys[lastIndex];
    return new NestedMap(parentMap,lastKey);
  }

  @Override
  public String toString() {
    return map.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Telemetry telemetry = (Telemetry) o;

    return map != null ? map.equals(telemetry.map) : telemetry.map == null;
  }

  @Override
  public int hashCode() {
    return map != null ? map.hashCode() : 0;
  }

  class NestedMap {
    Map<String, Object> nestedMap;
    String nestedKey;

    NestedMap(Map<String, Object> nestedMap, String nestedKey) {
      this.nestedMap = nestedMap;
      this.nestedKey = nestedKey;
    }

    <T> T getValue() {
      if(nestedMap!=null && nestedMap.containsKey(nestedKey) && nestedMap.get(nestedKey) != null)
        return (T)nestedMap.get(nestedKey);
      return null;
    }

    void add(Object value){
      if (nestedMap !=null)
        nestedMap.put(nestedKey,value);
    }
  }
}
