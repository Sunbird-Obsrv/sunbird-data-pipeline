package com.library.checksum.system;

import com.google.gson.Gson;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashMap;
import java.util.Map;


public class KeysToAccept implements Strategy {
    private Gson gson = new Gson();
    private String[] keysToAccept;

    public KeysToAccept(String[] keysToAccept){
        this.keysToAccept = keysToAccept;
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
        Map<String,Object> newEvent = new HashMap<String, Object>();
        for(String key : keysToAccept){
            newEvent.put(key,event.get(key));
        };
        return gson.toJson(newEvent);
    }
}
