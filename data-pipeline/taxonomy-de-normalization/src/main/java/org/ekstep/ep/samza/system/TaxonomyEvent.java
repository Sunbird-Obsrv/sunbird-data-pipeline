package org.ekstep.ep.samza.system;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.ekstep.ep.samza.service.Fetchable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shashankteotia on 9/19/15.
 */
public class TaxonomyEvent {
    private String json;
    private TaxonomyCache cache;
    public TaxonomyEvent(String json){
        this();
        this.json=json;
    }
    public TaxonomyEvent(){
    }
    public void setCache(TaxonomyCache cache){
        this.cache = cache;
    }
    public String getCID(){
        return String.valueOf(getMap().get("cid"));
    }
    public String getType(){
        return String.valueOf(getMap().get("ctype"));
    }
    public void denormalize() throws java.io.IOException{
        Map<String,Object> taxonomy = new HashMap<String, Object>();
        Map<String,Object> eventMap = getMap();
        Object cid = getCID();
        Object cval = cache.get((String) cid);
        System.out.println("LT: "+cval);
        Map<String,Object> cvalMap = null;
        String type;
        // TODO Put in a RetryStrategy
        if(cval==null){
            cache.warm();
            cval = cache.get((String)cid);
        }
        if(cval!=null){
            type = getType();
            cvalMap = new Gson().fromJson(String.valueOf(cval),Map.class);
            taxonomy.put(type,cvalMap);
            cid = cvalMap.get("parent");
            if(cid!=null){
                cval = cache.get((String) cid);
                cvalMap = new Gson().fromJson(String.valueOf(cval), Map.class);
                type = (String)cvalMap.get("type");
                taxonomy.put(type, cvalMap);
                cid = cvalMap.get("parent");
                if(cid!=null){
                    cval = cache.get((String) cid);
                    cvalMap = new Gson().fromJson(String.valueOf(cval), Map.class);
                    type = (String)cvalMap.get("type");
                    taxonomy.put(type,cvalMap);                }
            }
        }
        eventMap.put("taxonomy",taxonomy);
        this.json = new Gson().toJson(eventMap);
    }
    public Map<String, Object> getMap(){
        return new Gson().fromJson(json,Map.class);
    }
}
