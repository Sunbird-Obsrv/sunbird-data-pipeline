package org.ekstep.ep.samza.engine;

import org.apache.samza.storage.kv.KeyValueStore;

import java.util.Date;

public class DeDupEngine {

    private KeyValueStore<Object, Object> deDuplicationStore;

    public DeDupEngine(KeyValueStore<Object, Object> deDuplicationStore) {
        this.deDuplicationStore = deDuplicationStore;
    }

    public boolean isUniqueEvent(String checksum){
        return deDuplicationStore.get(checksum) == null;
    }

    public void storeChecksum(String checksum){
        deDuplicationStore.put(checksum, new Date().toString());
    }
}
