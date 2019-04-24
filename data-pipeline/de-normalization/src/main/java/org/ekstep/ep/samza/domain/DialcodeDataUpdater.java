package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.util.DialCodeDataCache;

public class DialcodeDataUpdater extends IEventUpdater {

    DialcodeDataUpdater(DialCodeDataCache dialcodeCache) {
        this.dataCache = dialcodeCache;
        this.cacheType = "dialcode";
    }

    public Event update(Event event) {
        return event;
    }
}
