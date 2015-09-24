package org.ekstep.ep.samza.fixtures;

import com.google.gson.Gson;
import com.library.checksum.system.Mappable;

import java.util.Map;

/**
 * Created by sreeharikm on 9/24/15.
 */
public class TaxonomyEvent implements Mappable {

    private String json;

    public TaxonomyEvent(String json){
        this();
        this.json=json;
    }
    public TaxonomyEvent(){
        this.json="";
    }

    @Override
    public Map<String, Object> getMap() {
        return new Gson().fromJson(json,Map.class);
    }

    @Override
    public void setMetadata(Map<String,Object> metadata){
        Map<String,Object> eventMap = getMap();
        eventMap.put("metadata",metadata);
        this.json = new Gson().toJson(eventMap);
    }

}
