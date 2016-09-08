package org.ekstep.ep.samza.system;


import com.library.checksum.system.Mappable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Event implements Mappable {
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
        if(map.containsKey("dimensions")){
            Map<String, Object> dimensions = (Map<String, Object>)map.get("dimensions");
            if(dimensions.containsKey("did"))
                return (String)dimensions.get("did");
        }
        return (String)map.get("did");
    }

    public Map<String, Object> getMap() {
        return map;
    }

    @Override
    public void setMetadata(Map<String, Object> metadata) {
        if(map.get("metadata") == null){
            map.put("metadata",metadata);
        }
        else{
            Map<String, Object> mData = (Map<String, Object>) map.get("metadata");
            if(mData.get("checksum") == null){
                mData.put("checksum",metadata.get("checksum"));
            }
        }
    }


    public void setFlag(String key, Object value) {
        Map<String, Object> flags=null;
        try {
            flags = (Map<String, Object>) map.get("flags");
        }catch(Exception e){
        }
        if (flags==null){
            flags = new HashMap<String, Object>();
        }
        flags.put(key,value);
        map.put("flags",flags);
    }

    public void setTimestamp() {
        if(map.get("ts") == null){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            if(map.get("ets") != null) {
                String ts = simpleDateFormat.format(new Date((Long) map.get("ets")));
                map.put("ts", ts);
            }
        }
    }

    public String getMid() {
        return (String) map.get("mid");
    }

    public String id() {
        return map != null && map.containsKey("metadata") &&
                (((Map<String, Object>) map.get("metadata")).containsKey("checksum"))
                ? (String) ((Map<String, Object>) map.get("metadata")).get("checksum")
                : null;
    }

    public boolean shouldRemoveDeviceStoreEntry(){
        if(map.containsKey("eid") && map.get("eid").equals("GE_SESSION_START")){
            if(map.containsKey("edata") && ((Map<String,Object>) map.get("edata")).containsKey("eks")){
                return ((Map<String,Object>) ((Map<String,Object>) map.get("edata")).get("eks")).containsKey("loc") &&
                        ((String) ((Map<String,Object>) ((Map<String,Object>) map.get("edata")).get("eks")).get("loc")).isEmpty()
                        ? true
                        : false;
            }
        }
        return false;
    }
}

