package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.task.DeNormalizationConfig;
import org.ekstep.ep.samza.util.DataCache;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.text.MessageFormat.format;

public abstract class IEventUpdater {

    static Logger LOGGER = new Logger(IEventUpdater.class);
    DataCache dataCache;
    String cacheType;
    DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ").withZoneUTC();
    DateTimeFormatter df1 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").withZoneUTC();

    public abstract void update(Event event);

    public void update(Event event, String key) {

        if (key != null && !key.isEmpty()) {
            Map data = dataCache.getData(key);
            if (data != null && !data.isEmpty()) {
                event.addMetaData(cacheType, getConvertedData(data));
            } else {
                event.setFlag(DeNormalizationConfig.getJobFlag(cacheType), false);
            }
        }
    }

    private Map getConvertedData(Map data) {
        if("content".equals(cacheType))
            return getEpochConvertedContentDataMap(data);
        else if("dialcode".equals(cacheType))
            return getEpochConvertedDialcodeDataMap(data);
        else
            return data;

    }

    private Long getTimestamp(String ts, DateTimeFormatter df) {
        try {
            return df.parseDateTime(ts).getMillis();
        } catch (Exception ex) {
            return 0L;
        }
    }

    private Long getConvertedTimestamp(String ts) {
        Long epochTs = getTimestamp(ts, df);
        if (epochTs == 0) {
            epochTs = getTimestamp(ts, df1);
        }
        return epochTs;
    }


    private Map getEpochConvertedContentDataMap(Map data) {

        Object lastSubmittedOn = data.get("lastsubmittedon");
        Object lastUpdatedOn = data.get("lastupdatedon");
        Object lastPublishedOn = data.get("lastpublishedon");
        if (lastSubmittedOn instanceof String) {
            lastSubmittedOn = getConvertedTimestamp(lastSubmittedOn.toString());
        }
        if (lastUpdatedOn instanceof String) {
            lastUpdatedOn = getConvertedTimestamp(lastUpdatedOn.toString());
        }
        if (lastPublishedOn instanceof String) {
            lastPublishedOn = getConvertedTimestamp(lastPublishedOn.toString());
        }
        data.put("lastsubmittedon", lastSubmittedOn);
        data.put("lastupdatedon", lastUpdatedOn);
        data.put("lastpublishedon", lastPublishedOn);
        return data;
    }

    private Map getEpochConvertedDialcodeDataMap(Map data) {

        Object generatedOn = data.get("generatedon");
        Object publishedOn = data.get("publishedon");
        if (generatedOn instanceof String) {
            generatedOn = getConvertedTimestamp(generatedOn.toString());
        }
        if (publishedOn instanceof String) {
            publishedOn = getConvertedTimestamp(publishedOn.toString());
        }
        data.put("generatedon", generatedOn);
        data.put("publishedon", publishedOn);
        return data;
    }
}


