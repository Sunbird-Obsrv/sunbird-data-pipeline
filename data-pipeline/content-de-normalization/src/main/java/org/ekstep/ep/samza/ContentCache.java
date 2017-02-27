package org.ekstep.ep.samza;

import java.util.Date;

public class ContentCache {
    private Content content;
    private long cachedTime;

    public ContentCache(Content content, long cachedTime) {
        this.content = content;
        this.cachedTime = cachedTime;
    }

    public long getCachedTime() {
        return cachedTime;
    }

    public Content getContent() {
        return content;
    }

    public boolean expired(long cacheTTL) {
        long currentTime = new Date().getTime();
        return (getCachedTime() + cacheTTL) < currentTime;
    }
}
