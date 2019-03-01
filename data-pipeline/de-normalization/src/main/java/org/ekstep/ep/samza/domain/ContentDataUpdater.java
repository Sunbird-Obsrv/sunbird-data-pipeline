package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.task.DeNormalizationConfig;
import org.ekstep.ep.samza.util.ContentDataCache;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static java.text.MessageFormat.format;

public class ContentDataUpdater extends IEventUpdater {

    DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ").withZoneUTC();

    ContentDataUpdater(ContentDataCache contentCache) {
        this.dataCache = contentCache;
        this.cacheType = "content";
    }

    public Event update(Event event) {

        Map data;
        String key = event.getKey("content").get(0);
        List<Map> dataMap = new ArrayList<>();
        try {
            if (key != null && !key.isEmpty()) {
                data = dataCache.getData(key);
                if (data != null && !data.isEmpty()) {
                    // convert string to epoch time format
                    dataMap.add(getEpochConvertedDataMap(data));
                    event.addMetaData(cacheType, dataMap);
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

    public Long getTimestamp(String ts) {
        try {
            return df.parseDateTime(ts).getMillis();
        } catch(Exception ex) {
            return 0L;
        }
    }

    public Map getEpochConvertedDataMap(Map data) {

        Object lastSubmittedOn = data.get("lastsubmittedon");
        Object lastUpdatedOn = data.get("lastupdatedon");
        Object lastPublishedOn = data.get("lastpublishedon");
        if (lastSubmittedOn instanceof String) {
            lastSubmittedOn = getTimestamp(lastSubmittedOn.toString());
        }
        if (lastUpdatedOn instanceof String) {
            lastUpdatedOn = getTimestamp(lastUpdatedOn.toString());
        }
        if (lastPublishedOn instanceof String) {
            lastPublishedOn = getTimestamp(lastPublishedOn.toString());
        }
        data.remove("lastsubmittedon");
        data.remove("lastupdatedon");
        data.remove("lastpublishedon");
        data.put("lastsubmittedon", lastSubmittedOn);
        data.put("lastupdatedon", lastUpdatedOn);
        data.put("lastpublishedon", lastPublishedOn);
        return data;
    }

}
