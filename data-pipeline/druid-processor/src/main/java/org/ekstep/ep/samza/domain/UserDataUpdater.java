package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.util.UserDataCache;

public class UserDataUpdater extends IEventUpdater {

    UserDataUpdater(UserDataCache userCache) {
        this.dataCache = userCache;
        this.cacheType = "user";
    }

    public Event update(Event event) {
        return event;
    }

}
