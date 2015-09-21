package org.ekstep.ep.samza.system;

import com.google.gson.Gson;
import org.apache.samza.storage.kv.KeyValueStore;
import org.ekstep.ep.samza.service.Fetchable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TaxonomyCache {

    private KeyValueStore<String, Object> cacheStore;
    private Clockable cacheClock;
    private Long ttl;
    private HashMap<String,Long> LAMap;
    private Fetchable service;

    public TaxonomyCache(KeyValueStore kvstore){
        cacheStore = kvstore;
        ttl = 60*60*1000L;
        this.cacheClock = new Clock();
        LAMap = new HashMap<String, Long>();
    }
    public TaxonomyCache(KeyValueStore kvstore,Clockable clock){
        this(kvstore);
        cacheClock = clock;
    }
    public Object get(String key){
        System.out.println("GET: "+key);
        if(LAMap.containsKey(key)){
            System.out.println("cache HIT");
            long timeDiff = cacheClock.getDate().getTime() - LAMap.get(key);
            System.out.println("timeDiff: "+String.valueOf(timeDiff));
            if(timeDiff<ttl){
                return cacheStore.get(key);
            }
        }
        cacheStore.delete(key);
        return null;
    }
    public void put(String key,Object value){
        System.out.println("PUT " + key);
        System.out.println("VALUE " + String.valueOf(value));
        cacheStore.put(key,value);
        LAMap.put(key, cacheClock.getDate().getTime());
    }
    public void setTTL(Long ttl){
        this.ttl = ttl;
    }
    public void setService(Fetchable service){
        this.service = service;
    }
    public void warm() throws java.io.IOException{
        System.out.println("Warming Cache");
        Map<String,Object> _map;
        Map<String,Object> map = service.fetch();
        ArrayList<Map<String,Object>> childElements = (ArrayList<Map<String,Object>>) map.get("children");
        for (Map<String, Object> elementLD : childElements) {
            _map = new HashMap<String, Object>();
            _map.put("id", elementLD.get("identifier"));
            _map.put("type", elementLD.get("type"));
            put(String.valueOf(elementLD.get("identifier")), new Gson().toJson(_map));
            childElements = (ArrayList<Map<String,Object>>) elementLD.get("children");
            for (Map<String, Object> elementLO : childElements) {
                _map = new HashMap<String, Object>();
                _map.put("id", elementLO.get("identifier"));
                _map.put("type", elementLO.get("type"));
                _map.put("parent", String.valueOf(elementLD.get("identifier")));
                put(String.valueOf(elementLO.get("identifier")), new Gson().toJson(_map));
                childElements = (ArrayList<Map<String,Object>>) elementLO.get("children");
                for (Map<String, Object> elementLT : childElements) {
                    _map = new HashMap<String, Object>();
                    _map.put("id", elementLT.get("identifier"));
                    _map.put("type", elementLT.get("type"));
                    _map.put("parent", String.valueOf(elementLO.get("identifier")));
                    put(String.valueOf(elementLT.get("identifier")), new Gson().toJson(_map));
                }
            }
        }
    }
}
