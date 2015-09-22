package org.ekstep.ep.samza.system;

import com.google.gson.Gson;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Map;


public class ChecksumGenerator {
    private Map<String,Object> json;
    private Gson gson = new Gson();

    public ChecksumGenerator(Map<String,Object> json){
        this.json = json;
    }

    public String generateCheksum(){
        Map<String,Object> newJson = filter(json);
        String json = gson.toJson(newJson);
        String checksum = DigestUtils.shaHex(json);
        return checksum;
    }

    private Map<String,Object> filter(Map<String, Object> json){
        String[] keys_to_reject = {"eid","@timestamp","udata","pdata","gdata"};
        for(String key : keys_to_reject){
            json.remove(key);
        };
        return json;
    }
}
