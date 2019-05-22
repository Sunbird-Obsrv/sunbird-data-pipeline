package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.task.DeNormalizationConfig;
import org.ekstep.ep.samza.util.UserDataCache;

import java.util.Map;

import static java.text.MessageFormat.format;

public class UserDataUpdater extends IEventUpdater {

    private UserDataCache userDataCache;

    UserDataUpdater(UserDataCache userDataCache) {
        this.userDataCache = userDataCache;
        this.cacheType = "user";
    }

    public void update(Event event) {
        Map<String, Object> userCacheData;
        String userId = event.actorId();
        if (userId != null && !userId.isEmpty() && !"system".equalsIgnoreCase(event.actorType())) {
            userCacheData = userDataCache.getUserData(event.actorId());
            if (userCacheData != null && !userCacheData.isEmpty()) {
                event.addUserData(userCacheData);
            } else {
                event.setFlag(DeNormalizationConfig.getUserLocationJobFlag(), false);
            }
        }
    }

}
