package org.ekstep.ep.samza.domain;

import com.google.gson.Gson;
import org.ekstep.ep.samza.external.ObjectResponse;
import org.ekstep.ep.samza.logger.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Event {
    static Logger LOGGER = new Logger(Event.class);
    private final Map<String, Object> map;
    private final List<String> lifeCycleEvents;

    public Event(Map<String, Object> map, List<String> lifecycleEvents) {
        this.map = map;
        this.lifeCycleEvents = lifecycleEvents;
    }

    public String id() {
        return map != null && map.containsKey("metadata") &&
                (((Map<String, Object>) map.get("metadata")).containsKey("checksum"))
                ? (String) ((Map<String, Object>) map.get("metadata")).get("checksum")
                : null;
    }

    public boolean canBeProcessed(){
        for (String event : lifeCycleEvents) {
            Pattern p = Pattern.compile(event);
            Matcher m = p.matcher(getEID());
            if (m.matches()) {
                LOGGER.info(m.toString(), "ALLOWING EVENT");
                return true;
            }
        }
        return false;
    }

    public String getEID() {
        return map != null && map.containsKey("eid") ? (String) map.get("eid") : null;
    }

    public Map<String, Object> LifecycleObjectAttributes(){
        Map<String,Object> edata = map.containsKey("edata") ? (Map<String, Object>) map.get("edata") : null;
        Map<String,Object> eks = edata != null && edata.containsKey("eks") ? (Map<String,Object>) edata.get("eks") : null;
        if(eks != null){
            Map<String, Object> lifecycleObjectMap = new HashMap<String, Object>();
            lifecycleObjectMap.putAll(eks);
            return lifecycleObjectMap;
        }
        return null;
    }

    public Map<String, Object> map() {
        return map;
    }

    public void updateFlags(boolean b) {
        Map<String,Object> flags = map.containsKey("flags") ? (Map<String, Object>) map.get("flags") : new HashMap<String,Object>();
        flags.put("lifecycle_data_processed",b);
        map.put("flags",flags);
    }

    public void updateMetadata(Map<String,Object> params) {
        Map<String,Object> metadata = map.containsKey("metadata") ? (Map<String, Object>) map.get("metadata") : new HashMap<String,Object>();
        metadata.put("lifecycle_data_process_err",params.get("err"));
        metadata.put("lifecycle_data_process_err_msg",params.get("errmsg"));
        map.put("metadata",metadata);
    }

    @Override
    public String toString() {
        return "Event{" +
                "map=" + map +
                '}';
    }
}

