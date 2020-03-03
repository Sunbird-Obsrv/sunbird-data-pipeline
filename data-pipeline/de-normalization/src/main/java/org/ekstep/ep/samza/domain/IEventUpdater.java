package org.ekstep.ep.samza.domain;

import com.google.gson.Gson;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.task.DeNormalizationConfig;
import org.ekstep.ep.samza.util.DataCache;
import org.ekstep.ep.samza.util.RestUtil;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

    public void update(Event event, String key, RestUtil restUtil) {
        if (key != null && !key.isEmpty()) {
            Map data = dataCache.getData(key);
            System.out.println("cacheType" + cacheType);
            if (data != null && !data.isEmpty()) {
                event.addMetaData(cacheType, getConvertedData(data));
            } else if (cacheType.equalsIgnoreCase("dialcode")) {
                Map dialCodeMetaData = this.getDialCodeMetaData(key, restUtil);
                if (dialCodeMetaData != null) {
                    event.addMetaData(cacheType, getConvertedData(dialCodeMetaData));
                    dataCache.insertData(key, new Gson().toJson(dialCodeMetaData));
                } else {
                    event.setFlag(DeNormalizationConfig.getJobFlag(cacheType), false);
                }
            } else {
                event.setFlag(DeNormalizationConfig.getJobFlag(cacheType), false);
            }
        }
    }

    private Map getConvertedData(Map data) {
        if ("content".equals(cacheType) || "collection".equals((cacheType)))
            return getEpochConvertedContentDataMap(data);
        else if ("dialcode".equals(cacheType))
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

    private Map<String, Object> getDialCodeMetaData(String dialCode, RestUtil restUtil) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJkNjNiMjgwZTQ1NDE0NDU4ODk4NzcwYzZhOGZiZjQ1MCJ9.Ji-22XcRrOiVy4dFAmE68wPxLkNmX4wKbTj_IB7fG6Y");
        try {
            okhttp3.Response httpResponse = restUtil.get("https://qa.ekstep.in/api/dialcode/v3/read/" + dialCode, headers);
            String responseBody = httpResponse.body().string();
            System.out.println("res" + responseBody);
            return new Gson().fromJson(responseBody, Map.class);
        } catch (IOException e) {
            System.out.println("Exception" + e);
            return null;
        }
    }
}


