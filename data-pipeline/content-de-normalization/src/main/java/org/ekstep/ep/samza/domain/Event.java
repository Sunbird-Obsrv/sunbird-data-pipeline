package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.logger.Logger;

import java.util.HashMap;
import java.util.Map;

public class Event {
    static Logger LOGGER = new Logger(Event.class);
    public Map<String, Object> map;

    public Event(Map<String,Object> map) {
        this.map = map;
    }

    public Map<String,Object> getGData(){
        return getMap() != null &&
                getMap().containsKey("gdata")
                ? (Map<String,Object>) getMap().get("gdata")
                : null;
    }

    public Map<String,Object> getEData(){
        return getMap() != null &&
                getMap().containsKey("edata")
                ? (Map<String,Object>) getMap().get("edata")
                : null;
    }

    public Map<String,Object> getEks(){
        return getMap() != null && getEData() != null &&
                getEData().containsKey("eks")
                ? (Map<String,Object>) getEData().get("eks")
                : null;
    }

    public Map<String, Object> getMap(){
        return this.map;
    }


    public String getContentId(){
        if(getEid().equals("GE_LAUNCH_GAME")){
            Map<String, Object> eks = getEks();
            if(eks != null && eks.containsKey("gid")){
                return (String) eks.get("gid");
            }
        } else {
            Map<String, Object> gData = getGData();
            if (gData != null && gData.containsKey("id")) {
                return (String) gData.get("id");
            }
        }
        return null;
    }

    public String id() {
        return getMap() != null && getMap().containsKey("metadata") &&
                (((Map<String, Object>) getMap().get("metadata")).containsKey("checksum"))
                ? (String) ((Map<String, Object>) getMap().get("metadata")).get("checksum")
                : null;
    }

    public void updateContent(Content content) {
        updateMetadata(content);
        HashMap<String, Object> contentData = new HashMap<String, Object>();
        contentData.put("name", content.name());
        contentData.put("description", content.description());
        contentData.put("ageGroup", content.ageGroup());
        contentData.put("mediaType", content.mediaType());
        contentData.put("contentType", content.contentType());
        contentData.put("language", content.language());
        contentData.put("owner", content.owner());
        contentData.put("lastUpdatedOn", content.lastUpdatedOn());
        map.put("contentdata",contentData);
    }

    private void updateMetadata(Content content) {
        Map<String, Object> metadata = (Map<String, Object>) map.get("metadata");
        if (metadata != null) {
            metadata.put("cachehit",content.getCacheHit());
            return;
        }
        metadata = new HashMap<String, Object>();
        metadata.put("cachehit",content.getCacheHit());
        map.put("metadata", metadata);

        LOGGER.info(id(), "METADATA CACHEHIT - ADDED " + metadata);
    }

    public String getEid() {
        return map != null && map.containsKey("eid") ? (String) map.get("eid") : null;
    }
}
