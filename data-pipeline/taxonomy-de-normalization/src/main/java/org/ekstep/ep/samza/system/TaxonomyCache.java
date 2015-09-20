package org.ekstep.ep.samza.system;

import org.apache.samza.storage.kv.KeyValueStore;
import org.ekstep.ep.samza.service.Fetchable;

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
        if(LAMap.containsKey(key)){
            long timeDiff = cacheClock.getDate().getTime() - LAMap.get(key);
            if(timeDiff<ttl){
                return cacheStore.get(key);
            }
        }
        cacheStore.delete(key);
        return null;
    }
    public void put(String key,Object value){
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
        HashMap<String,Object> map = (HashMap<String,Object>)service.fetch();
        for(Map.Entry<String,Object> entry: map.entrySet()){
            put(entry.getKey(),entry.getValue());
        }
    }
}
