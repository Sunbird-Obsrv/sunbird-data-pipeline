package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.util.ContentDataCache;

public class ContentDataUpdater extends IEventUpdater {

    ContentDataUpdater(ContentDataCache contentCache) {
        this.dataCache = contentCache;
        this.cacheType = "content";
    }

    public Event update(Event event)  {
        return event;
    }

}
