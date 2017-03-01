package org.ekstep.ep.samza.service;

import com.google.gson.Gson;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.task.TaskContext;
import org.ekstep.ep.samza.cache.CacheEntry;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.task.ContentDeNormalizationMetrics;

import java.lang.reflect.Type;
import java.util.Date;

public class CacheService<K, V> {
    static Logger LOGGER = new Logger(CacheService.class);
    private KeyValueStore<Object, Object> store;
    private Type cachedValueType;
    private ContentDeNormalizationMetrics metrics;

    //for testing
    public CacheService(KeyValueStore<Object, Object> store, Type cachedValueType, ContentDeNormalizationMetrics metrics) {
        this.store = store;
        this.cachedValueType = cachedValueType;
        this.metrics = metrics;
    }

    public CacheService(TaskContext context, String storeName, Class<CacheEntry> cachedValueType
            , ContentDeNormalizationMetrics metrics) {
        this.cachedValueType = cachedValueType;
        this.metrics = metrics;
        this.store = (KeyValueStore<Object, Object>) context.getStore(storeName);
    }

    public V get(K key, long cacheTTL) {
        String value = (String) store.get(key);
        if (value == null) {
            metrics.incCacheMissCounter();
            return null;
        }
        CacheEntry<V> cacheEntry = (CacheEntry<V>) new Gson().<V>fromJson(value, cachedValueType);
        if (cacheEntry.expired(cacheTTL)) {
            metrics.incCacheExpiredCounter();
            LOGGER.info((String) key, "CACHE ENTRY EXPIRED", cacheEntry);
            return null;
        }
        metrics.incCacheHitCounter();
        return cacheEntry.getValue();
    }

    public void put(K key, V value) {
        String valueJson = new Gson().toJson(new CacheEntry(value, new Date().getTime()));
        store.put(key, valueJson);
    }
}
