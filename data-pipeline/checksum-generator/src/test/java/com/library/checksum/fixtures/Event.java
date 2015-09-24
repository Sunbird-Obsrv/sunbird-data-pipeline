package com.library.checksum.fixtures;

import com.library.checksum.system.Mappable;

import java.util.Map;


public class Event implements Mappable {

    private Map<String,Object> map;

    public Event(Map<String,Object> map){
        this.map=map;
    }

    @Override
    public Map<String, Object> getMap() {
        return this.map;
    }

    @Override
    public void setMetadata(Map<String, Object> metadata) {
        Map<String,Object> eventMap = getMap();
        eventMap.put("metadata",metadata);
    }
}
