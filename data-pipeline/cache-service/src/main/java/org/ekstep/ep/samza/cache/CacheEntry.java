package org.ekstep.ep.samza.cache;

import java.util.Date;

public class CacheEntry<T> {
    private T value;
    private long cachedTime;

    public CacheEntry(T value, long cachedTime) {
        this.value = value;
        this.cachedTime = cachedTime;
    }

    public long getCachedTime() {
        return cachedTime;
    }

    public T getValue() {
        return value;
    }

    public boolean expired(long cacheTTL) {
        long currentTime = new Date().getTime();
        return (getCachedTime() + cacheTTL) < currentTime;
    }
}
