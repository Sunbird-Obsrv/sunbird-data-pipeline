package com.library.checksum.system;

import com.google.gson.Gson;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashMap;
import java.util.Map;


public class KeysToAccept implements Strategy {
    private Gson gson = new Gson();

    @Override
    public String createChecksum(Map<String, Object> event, String[] keys_to_accept) {
        String filteredJson = filterEvent(event,keys_to_accept);
        String checksum = DigestUtils.shaHex(filteredJson);
        return checksum;
    }

    private String filterEvent(Map<String, Object> event,String[] keys_to_accept){
        Map<String,Object> newEvent = new HashMap<String, Object>();
        for(String key : keys_to_accept){
            newEvent.put(key,event.get(key));
        };
        return gson.toJson(newEvent);
    }


}
