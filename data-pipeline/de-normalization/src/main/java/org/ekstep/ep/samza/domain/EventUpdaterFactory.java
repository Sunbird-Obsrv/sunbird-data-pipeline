package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.util.ContentDataCache;
import org.ekstep.ep.samza.util.DeviceDataCache;
import org.ekstep.ep.samza.util.DialCodeDataCache;
import org.ekstep.ep.samza.util.UserDataCache;


public class EventUpdaterFactory implements  AbstractFactory {
    private ContentDataUpdater contentDataUpdater;
    private UserDataUpdater userDataUpdater;
    private DialcodeDataUpdater dialCodeDataUpdater;
    private DeviceDataUpdater deviceDataUpdater;

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

        this.contentDataUpdater = new ContentDataUpdater(contentDataCache);
        this.userDataUpdater = new UserDataUpdater(userDataCache);
        this.dialCodeDataUpdater = new DialcodeDataUpdater(dialCodeDataCache);
        this.deviceDataUpdater = new DeviceDataUpdater(deviceDataCache);
    }

    public IEventUpdater getInstance(String type) {

        switch (type) {
            case "content-data-updater":
                if (this.contentDataUpdater == null)
                    return new ContentDataUpdater(contentDataCache);
                else return this.contentDataUpdater;
            case "user-data-updater":
                if (this.userDataUpdater == null)
                    return new UserDataUpdater(userDataCache);
                else return this.userDataUpdater;
            case "device-data-updater":
                if (this.deviceDataUpdater == null)
                    return new DeviceDataUpdater(deviceDataCache);
                else return this.deviceDataUpdater;
            case "dialcode-data-updater":
                if (this.dialCodeDataUpdater == null)
                    return new DialcodeDataUpdater(dialCodeDataCache);
                else return this.dialCodeDataUpdater;
            default:
                return null;
        }
    }
}
