package org.ekstep.ep.samza.model;

import com.google.gson.Gson;
import org.ekstep.ep.samza.logger.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


public class Event  {
    static Logger LOGGER = new Logger(Event.class);
    public final String json;

    public Event(String json) {
        this.json = json;
    }

    public Event() {
        this("");
    }

    public Map<String,Object> getEData(){
        return (Map<String,Object>) getMap().get("edata");
    }

    public Map<String,Object> getEks(){
        return (Map<String,Object>) getEData().get("eks");
    }

    public Map<String, Object> getMap(){
        return (Map<String, Object>) new Gson().fromJson(json, Map.class);
    }

    public String getUID(){
        return (String) getEks().get("uid");
    }

    public String getEId(){
        return (String) getMap().get("eid");
    }

    public Date getTs() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date timeOfEvent = simpleDateFormat.parse((String)getMap().get("ts"));
            return timeOfEvent;
        } catch (ParseException e) {
            LOGGER.error(id(), "EXCEPTION", e);
        }
        return null;
    }

    public String id() {
        return getMap() != null && getMap().containsKey("metadata") &&
                (((Map<String, Object>) getMap().get("metadata")).containsKey("checksum"))
                ? (String) ((Map<String, Object>) getMap().get("metadata")).get("checksum")
                : null;
    }

    @Override
    public String toString() {
        return "Event{" +
                "json='" + json + '\'' +
                '}';
    }
}
