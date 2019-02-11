package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.task.DeNormalizationConfig;
import org.ekstep.ep.samza.util.ContentDataCache;
import org.ekstep.ep.samza.util.UserDataCache;

import java.util.Map;

import static java.text.MessageFormat.format;

public class ContentDataUpdater implements IEventUpdater {
    static Logger LOGGER = new Logger(ContentDataUpdater.class);
    private ContentDataCache contentCache;

    ContentDataUpdater(ContentDataCache contentCache) {
        this.contentCache = contentCache;
    }

    public Event update(Event event) {

        Map content;
        try {
            String contentId = event.objectID();
            if (contentId != null && !contentId.isEmpty()) {
                content = contentCache.getDataForContentId(contentId);
                if (content != null && !content.isEmpty()) {
                    event.addContentData(content);
                }
                else {
                    event.setFlag(DeNormalizationConfig.getContentLocationJobFlag(), false);
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
