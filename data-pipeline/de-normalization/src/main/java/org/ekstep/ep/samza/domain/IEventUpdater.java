package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.task.DeNormalizationConfig;
import org.ekstep.ep.samza.util.DataCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.text.MessageFormat.format;

public abstract class IEventUpdater {

    static Logger LOGGER = new Logger(IEventUpdater.class);
    DataCache dataCache;
    String cacheType;

    public abstract Event update(Event event);

    public Event update(Event event, String key) {
        Map data;
        List<Map> dataMap = new ArrayList<>();
        try {
            if (key != null && !key.isEmpty()) {
                data = dataCache.getData(key);
                if (data != null && !data.isEmpty()) {
                    dataMap.add(data);
                    event.addMetaData(cacheType, dataMap);
                }
                else {
                    event.setFlag(DeNormalizationConfig.getJobFlag(cacheType), false);
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

    public Event update(Event event, List<String> keys) {
        List<Map> dataMap;
        try {
            if (keys != null && !keys.isEmpty()) {
                dataMap = dataCache.getData(keys);
                if (dataMap != null && !dataMap.isEmpty()) {
                    event.addMetaData(cacheType, dataMap);
                }
                else {
                    event.setFlag(DeNormalizationConfig.getJobFlag(cacheType), false);
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


