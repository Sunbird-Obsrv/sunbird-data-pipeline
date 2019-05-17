package org.ekstep.ep.samza.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;

public class LRUCache {

    private Cache<String, String> cache;

    public Cache<String, String> getConnection() {
        this.cache = this.cache == null ?
        Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                //.maximumSize(1_00_000)
                .build() : this.cache;
        return this.cache;
    }
}
