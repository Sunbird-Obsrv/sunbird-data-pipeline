package org.ekstep.ep.samza.system;


import org.apache.samza.storage.kv.KeyValueStore;

import java.util.HashMap;
import java.util.Map;

public class Taxonomy {

    private final KeyValueStore<String, Object> taxonomyStore;
    private final String cid;

    public Taxonomy(String cid, KeyValueStore<String,Object> taxonomyStore) {
        this.cid = cid;
        this.taxonomyStore = taxonomyStore;
    }

    public String getCid() {
        return (String) this.cid;
    }

    public String getCType() {
        Map<String, Object> parent = (Map<String, Object>) taxonomyStore.get(cid);
        String type = (String) parent.get("type");
        return (String) type;
    }

    public Map<String,Object> getTaxonomyData(String cid) {
        Map<String, Object> taxonomyMap = new HashMap<String, Object>();
        String cType = getCType();
        getTaxonomyMap(cid,cType,taxonomyMap);
        return taxonomyMap;
    }

    public void getTaxonomyMap(String cid,String cType, Map<String,Object> taxonomyMap){
        taxonomyMap.put(cType, taxonomyStore.get(cid));
        Map<String,Object> parentMap = (Map<String,Object>) taxonomyStore.get(cid);
        String parent = (String) parentMap.get("parent");
        if (parent == null) {
            return;
        } else {
            Map<String,Object> childParentMap = (Map<String,Object>) taxonomyStore.get(parent);
            String type = (String) childParentMap.get("type");
            getTaxonomyMap(parent,type, taxonomyMap);
        }
    }
}


