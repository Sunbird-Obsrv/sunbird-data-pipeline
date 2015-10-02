package org.ekstep.ep.samza.model;

import com.google.gson.Gson;

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
        return (Map<String, Object>) new Gson().fromJson(json,Map.class);
    }

    public String getUID(){
        return (String) getEks().get("uid");
    }

    public String getEId(){
        return (String) getMap().get("eid");
    }
}
