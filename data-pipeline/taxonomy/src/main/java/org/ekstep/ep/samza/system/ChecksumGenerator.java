package org.ekstep.ep.samza.system;

import com.google.gson.Gson;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Map;


public class ChecksumGenerator {
    private String json;
    private Gson gson = new Gson();

    public ChecksumGenerator(String json){
        this.json = json;
    }

    public String generateCheksum(){
        Map<String,Object> newJson = gson.fromJson(json,Map.class);
        Map<String,Object> filteredJson = filter(newJson);
        String json = gson.toJson(filteredJson);
        String checksum = DigestUtils.shaHex(json);
        return checksum;
    }

    private Map<String,Object> filter(Map<String, Object> json){
        String[] keys_to_reject = {"eid","@timestamp","pdata","gdata"};
        for(String key : keys_to_reject){
            json.remove(key);
        };
        return json;
    }
}
