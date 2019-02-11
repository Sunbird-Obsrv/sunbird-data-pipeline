package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.util.ContentDataCache;
import org.ekstep.ep.samza.util.DeviceDataCache;
import org.ekstep.ep.samza.util.DialCodeDataCache;
import org.ekstep.ep.samza.util.UserDataCache;


public class EventUpdaterFactory implements  AbstractFactory {
    private ContentDataCache contentDataCache;
    private UserDataCache userDataCache;
    private DialCodeDataCache dialCodeDataCache;
    private DeviceDataCache deviceDataCache;

    public EventUpdaterFactory(ContentDataCache contentDataCache,
                        UserDataCache userDataCache,
                        DeviceDataCache deviceDataCache,
                        DialCodeDataCache dialCodeDataCache) {
        this.contentDataCache = contentDataCache;
        this.userDataCache = userDataCache;
        this.deviceDataCache = deviceDataCache;
        this.dialCodeDataCache = dialCodeDataCache;
    }

    public IEventUpdater create(String type) {
        if(type.equalsIgnoreCase("content-data-updater")) {
            return new ContentDataUpdater(contentDataCache);
        } else if(type.equalsIgnoreCase("user-data-updater")) {
            return new UserDataUpdater(userDataCache);
        } else if(type.equalsIgnoreCase("device-data-updater")) {
            return new DeviceDataUpdater(deviceDataCache);
        } else if(type.equalsIgnoreCase("dialcode-data-updater")) {
            return new DialcodeDataUpdater(dialCodeDataCache);
        }
        return null;
    }
}
