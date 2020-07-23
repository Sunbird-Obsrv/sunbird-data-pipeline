package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.util.ContentDataCache;

public class CollectionDataUpdater extends IEventUpdater {

    CollectionDataUpdater(ContentDataCache contentCache) {
        this.dataCache = contentCache;
        this.cacheType = "collection";
    }

    public void update(Event event){
    }
}