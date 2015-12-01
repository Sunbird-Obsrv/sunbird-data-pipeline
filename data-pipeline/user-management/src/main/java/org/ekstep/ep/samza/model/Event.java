package org.ekstep.ep.samza.model;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


public class Event  {
    public String json;

    public Event(String json) {
        this.json = json;
    }

    public Event() {
        this.json = "";
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
            e.printStackTrace();
        }
        return null;
    }
}
