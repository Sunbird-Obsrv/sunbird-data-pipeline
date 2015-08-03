package org.ekstep.ep.samza.system;


import java.util.HashMap;
import java.util.Map;

public class Event {
    private final Map<String, Object> map;

    public Event(Map<String,Object> map) {
        this.map = map;
    }

    public String getGPSCoordinates() {
        try {
            Map<String, Object> edata = (Map<String, Object>) map.get("edata");
            Map<String, Object> eks = (Map<String, Object>) edata.get("eks");
            return (String) eks.get("loc");
        }catch (Exception e){
            return "";
        }
    }

    public void AddLocation(Location location){
        Map<String, String> ldata = new HashMap<String, String>();
        ldata.put("locality", location.getCity());
        ldata.put("district", location.getDistrict());
        ldata.put("state", location.getState());
        ldata.put("country", location.getCountry());
        map.put("ldata", ldata);
    }
    public String getDid() {
        return (String)map.get("did");
    }

    public Map<String, Object> getMap() {
        return map;
    }


    public void setFlag(String key, Object value) {
        Map<String, Object> flags = (Map<String, Object>) map.get("flags");
        if (flags==null){
            flags = new HashMap<String, Object>();
        }
        flags.put(key,value);
        map.put("flags",flags);
    }
}

