package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.task.DruidProcessorConfig;
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

    public abstract Event update(Event event);

    public Event update(Event event, String key, Boolean convertionRequired) {
        Map data;
        Map convertedData;
        List<Map> dataMap = new ArrayList<>();
        try {
            if (key != null && !key.isEmpty()) {
                data = dataCache.getData(key);
                if (data != null && !data.isEmpty()) {
                    if(convertionRequired)
                        convertedData = getConvertedData(data);
                    else
                        convertedData = data;
                    dataMap.add(convertedData);
                    event.addMetaData(cacheType, dataMap);
                }
                else {
                    event.setFlag(DruidProcessorConfig.getJobFlag(cacheType), false);
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

    public Event update(Event event, List<String> keys, Boolean convertionRequired) {
        List<Map> dataMap;
        List<Map> convertedDataMap = new ArrayList<>();
        try {
            if (keys != null && !keys.isEmpty()) {
                dataMap = dataCache.getData(keys);
                if (dataMap != null && !dataMap.isEmpty()) {
                    for (Map entry : dataMap) {
                        if(convertionRequired)
                            convertedDataMap.add(getConvertedData(entry));
                        else
                            convertedDataMap.add(entry);
                    }
                    event.addMetaData(cacheType, convertedDataMap);
                }
                else {
                    event.setFlag(DruidProcessorConfig.getJobFlag(cacheType), false);
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

    public Map getConvertedData(Map data) {
        if("content".equals(cacheType))
            return getEpochConvertedContentDataMap(data);
        else if("dialcode".equals(cacheType))
            return getEpochConvertedDialcodeDataMap(data);
        else
            return data;

    }

    public Long getTimestamp(String ts, DateTimeFormatter df) {
        try {
            return df.parseDateTime(ts).getMillis();
        } catch(Exception ex) {
            ex.printStackTrace();
            return 0L;
        }
    }

    public Long getConvertedTimestamp(String ts) {
        Long epochTs = getTimestamp(ts, df);
        if (epochTs == 0) {
            epochTs = getTimestamp(ts, df1);
        }
        return epochTs;
    }


    public Map getEpochConvertedContentDataMap(Map data) {

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
        data.remove("lastsubmittedon");
        data.remove("lastupdatedon");
        data.remove("lastpublishedon");
        data.put("lastsubmittedon", lastSubmittedOn);
        data.put("lastupdatedon", lastUpdatedOn);
        data.put("lastpublishedon", lastPublishedOn);
        return data;
    }

    public Map getEpochConvertedDialcodeDataMap(Map data) {

        Object generatedOn = data.get("generatedon");
        Object publishedOn = data.get("publishedon");
        if (generatedOn instanceof String) {
            generatedOn = getConvertedTimestamp(generatedOn.toString());
        }
        if (publishedOn instanceof String) {
            publishedOn = getConvertedTimestamp(publishedOn.toString());
        }
        data.remove("generatedon");
        data.remove("publishedon");
        data.put("generatedon", generatedOn);
        data.put("publishedon", publishedOn);
        return data;
    }
}


