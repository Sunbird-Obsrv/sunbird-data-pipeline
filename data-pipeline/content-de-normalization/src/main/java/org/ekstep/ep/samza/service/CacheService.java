package org.ekstep.ep.samza.service;

import com.google.gson.Gson;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.task.TaskContext;
import org.ekstep.ep.samza.cache.CacheEntry;
import org.ekstep.ep.samza.logger.Logger;

import java.lang.reflect.Type;
import java.util.Date;

public class CacheService<K, V> {
    static Logger LOGGER = new Logger(CacheService.class);
    private KeyValueStore<Object, Object> store;
    private Type cachedValueType;

    //for testing
    public CacheService(KeyValueStore<Object, Object> store, Type cachedValueType) {
        this.store = store;
        this.cachedValueType = cachedValueType;
    }

    public CacheService(TaskContext context, String storeName, Class<CacheEntry> cachedValueType) {
        this.cachedValueType = cachedValueType;
        this.store = (KeyValueStore<Object, Object>) context.getStore(storeName);
    }

    public V get(K key, long cacheTTL) {
        String value = (String) store.get(key);
        if (value == null) {
            return null;
        }

        CacheEntry<V> cacheEntry = (CacheEntry<V>) new Gson().<V>fromJson(value, cachedValueType);
        if (cacheEntry.expired(cacheTTL)) {
            LOGGER.info((String) key, "CACHE ENTRY EXPIRED", cacheEntry);
            return null;
        }
        return cacheEntry.getValue();
    }

    public void put(K key, V value) {
        String valueJson = new Gson().toJson(new CacheEntry(value, new Date().getTime()));
        store.put(key, valueJson);
    }
}
