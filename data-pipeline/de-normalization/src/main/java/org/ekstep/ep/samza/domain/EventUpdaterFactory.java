package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.util.ContentDataCache;
import org.ekstep.ep.samza.util.DialCodeDataCache;
import org.ekstep.ep.samza.util.UserDataCache;


public class EventUpdaterFactory implements AbstractFactory {

    private ContentDataUpdater contentDataUpdater;
    private UserDataUpdater userDataUpdater;
    private DialcodeDataUpdater dialCodeDataUpdater;
    private CollectionDataUpdater collectionDataUpdater;

    public EventUpdaterFactory(ContentDataCache contentDataCache,
                               UserDataCache userDataCache,
                               DialCodeDataCache dialCodeDataCache) {

        this.contentDataUpdater = new ContentDataUpdater(contentDataCache);
        this.userDataUpdater = new UserDataUpdater(userDataCache);
        this.dialCodeDataUpdater = new DialcodeDataUpdater(dialCodeDataCache);
        this.collectionDataUpdater = new CollectionDataUpdater(contentDataCache);
    }

    public IEventUpdater getInstance(String type) {

        switch (type) {
            case "content-data-updater":
                return this.contentDataUpdater;
            case "user-data-updater":
                return this.userDataUpdater;
            case "dialcode-data-updater":
                return this.dialCodeDataUpdater;
            case "collection-data-updater":
                return this.collectionDataUpdater;
            default:
                return null;
        }
    }
}
