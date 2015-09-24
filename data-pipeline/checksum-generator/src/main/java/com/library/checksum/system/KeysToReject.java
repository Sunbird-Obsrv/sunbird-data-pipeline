package com.library.checksum.system;

import com.google.gson.Gson;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashMap;
import java.util.Map;


public class KeysToReject implements Strategy {
    private Gson gson = new Gson();
    private String[] keysToReject;

    public KeysToReject(String[] keysToReject){
        this.keysToReject = keysToReject;
    }

    @Override
    public Mappable generateChecksum(Mappable event) {
        String filteredJson = filterEvent(event.getMap());
        String checksum = DigestUtils.shaHex(filteredJson);
        stampChecksum(event, checksum);
        return event;
    }

    private void stampChecksum(Mappable event, String checksum) {
        Map<String,Object> metadata = new HashMap<String,Object>();
        metadata.put("checksum",checksum);
        event.setMetadata(metadata);
    }

    private String filterEvent(Map<String, Object> event){
        Map<String, Object> dupEvent = cloneEvent(event);
        for(String key : keysToReject){
            dupEvent.remove(key);
        };
        return gson.toJson(dupEvent);
    }

    private Map<String,Object> cloneEvent(Map<String,Object> event){
        Map<String, Object> dupEvent = new HashMap<String, Object>();
        dupEvent.putAll(event);
        return dupEvent;
    }
}
