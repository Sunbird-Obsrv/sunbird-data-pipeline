package com.library.checksum.system;

import com.google.gson.Gson;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashMap;
import java.util.Map;


public class KeysToReject implements Strategy {
    private Gson gson = new Gson();

    @Override
    public String createChecksum(Map<String, Object> event, String[] keys_to_reject) {
        String filteredJson = filterEvent(event,keys_to_reject);
        String checksum = DigestUtils.shaHex(filteredJson);
        return checksum;
    }

    private String filterEvent(Map<String, Object> event,String[] keys_to_reject){
        Map<String, Object> dupEvent = cloneEvent(event);
        for(String key : keys_to_reject){
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
