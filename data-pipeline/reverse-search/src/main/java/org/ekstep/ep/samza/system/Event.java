package org.ekstep.ep.samza.system;


import com.library.checksum.system.Mappable;
import org.ekstep.ep.samza.reader.Telemetry;
import org.ekstep.ep.samza.reader.NullableValue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Event implements Mappable {
  private final Telemetry telemetry;
//  private Logger logger;

  public Event(Map<String, Object> map) {
    telemetry = new Telemetry(map);
  }

  public String getGPSCoordinates() {
    NullableValue location = telemetry.read("edata.eks.loc");
    return location.isNull() ? "" : (String) location.value();
  }

  public void AddLocation(Location location) {
    Map<String, String> ldata = new HashMap<String, String>();
    ldata.put("locality", location.getCity());
    ldata.put("district", location.getDistrict());
    ldata.put("state", location.getState());
    ldata.put("country", location.getCountry());
    telemetry.add("ldata",ldata);
  }

  public String getDid() {
    NullableValue did = telemetry.read("dimensions.did");
    return (String) (did.isNull()
        ?  telemetry.read("did").value()
        : did.value());
  }

  public Map<String, Object> getMap() {
    return (Map<String, Object>)telemetry.getMap();
  }

  @Override
  public void setMetadata(Map<String, Object> metadata) {
    NullableValue metadataValue = telemetry.read("metadata");
    if(metadataValue.isNull()){
      telemetry.add("metadata",metadata);
    }
    else {
      NullableValue checksum = telemetry.read("metadata.checksum");
      if(checksum.isNull())
        telemetry.add("metadata.checksum",metadata.get("checksum"));
    }
  }


  public void setFlag(String key, Object value) {
    NullableValue telemetryFlag = telemetry.read("flags");
    Map<String, Object> flags = telemetryFlag.isNull()
        ? new HashMap<String,Object>()
        : (Map<String, Object>) telemetryFlag.value();
    flags.put(key, value);
    telemetry.add("flags", flags);
  }

  public void setTimestamp() {
    NullableValue ts = telemetry.read("ts");
    NullableValue ets = telemetry.read("ets");
    if (ts.isNull() && !ets.isNull()){
      SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
      String updatedTs = simple.format(new Date((Long) ets.value()));
      telemetry.add("ts", updatedTs);
    }
  }

  public String getMid() {
    return (String) telemetry.read("mid").value();
  }

  public String id() {
    return (String)telemetry.read("metadata.checksum").value();

  }

  public boolean isLocationEmpty() {
    NullableValue location = telemetry.read("edata.eks.loc");
    return !location.isNull() && ((String) location.value()).isEmpty();
  }

  public boolean isLocationPresent() {
    NullableValue location = telemetry.read("edata.eks.loc");
    return !(location.isNull() || ((String) location.value()).isEmpty());
  }

  public boolean isLocationAbsent() {
    NullableValue location = telemetry.read("edata.eks.loc");
    return location.isNull();
  }

  @Override
  public String toString() {
    return "event: " + telemetry.toString();
  }
}

