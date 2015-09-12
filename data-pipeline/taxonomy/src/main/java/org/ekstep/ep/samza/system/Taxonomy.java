package org.ekstep.ep.samza.system;


import org.apache.samza.storage.kv.KeyValueStore;

import java.util.HashMap;
import java.util.Map;

public class Taxonomy {

    private final KeyValueStore<String, Object> taxonomyStore;
    private final String cid;

    public Taxonomy(String cid, KeyValueStore<String,Object> taxonomyStore) {
        this.taxonomyStore = taxonomyStore;
        this.cid = cid;
    }

    public String getCid() {
        return (String) this.cid;
    }

    public Map<String,Object> getTaxonomyData(String cid) {
        Map<String, Object> taxonomyMap = new HashMap<String, Object>();
        getTaxonomyMap(cid,taxonomyMap);
        return taxonomyMap;
    }

    public void getTaxonomyMap(String cid,Map<String,Object> taxonomyMap){
        taxonomyMap.put(cid, taxonomyStore.get(cid));
        Map<String,Object> parentMap = (Map<String,Object>) taxonomyStore.get(cid);
        String parent = (String) parentMap.get("parent");
        if (parent == null) {
            return;
        } else {
            getTaxonomyMap(parent, taxonomyMap);
        }
    }
}


