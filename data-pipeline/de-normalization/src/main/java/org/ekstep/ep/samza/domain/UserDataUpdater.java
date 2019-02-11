package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.task.DeNormalizationConfig;
import org.ekstep.ep.samza.util.UserDataCache;

import java.util.Map;

import static java.text.MessageFormat.format;

public class UserDataUpdater implements IEventUpdater {
    static Logger LOGGER = new Logger(UserDataUpdater.class);
    private UserDataCache userCache;

    UserDataUpdater(UserDataCache userCache) {
        this.userCache = userCache;
    }

    public Event update(Event event) {

        Map user;
        try {
            String userId = event.actorId();
            String userType = event.actorType();
            if (userId != null && !userId.isEmpty()) {
                if(!userType.equalsIgnoreCase("system")) {
                    user = userCache.getDataForUserId(userId);
                    if (user != null && !user.isEmpty()) {
                        event.addUserData(user);
                    } else {
                        event.setFlag(DeNormalizationConfig.getUserLocationJobFlag(), false);
                    }
                }
            }
            return event;
        } catch(Exception ex) {
            LOGGER.error(null,
                    format("EXCEPTION. EVENT: {0}, EXCEPTION:",
                            event),
                    ex);
            return event;
        }
    }
}
