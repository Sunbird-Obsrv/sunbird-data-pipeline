package org.ekstep.ep.samza;

import com.google.gson.Gson;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.task.TaskContext;

public class CacheService<K, V> {
    private KeyValueStore<Object, Object> store;
    private Class cachedValueClass;

    //for testing
    public CacheService(KeyValueStore<Object, Object> store, Class cachedValueClass) {
        this.store = store;
        this.cachedValueClass = cachedValueClass;
    }

    public CacheService(TaskContext context, String storeName, Class<ContentCache> cachedValueClass) {
        this.cachedValueClass = cachedValueClass;
        this.store = (KeyValueStore<Object, Object>) context.getStore(storeName);
    }

    public V get(K key) {
        String value = (String) store.get(key);
        return value != null ? (V) new Gson().<V>fromJson(value, cachedValueClass) : null;
    }

    public void put(K key, V value) {
        String valueJson = new Gson().toJson(value);
        store.put(key, valueJson);
    }
}
