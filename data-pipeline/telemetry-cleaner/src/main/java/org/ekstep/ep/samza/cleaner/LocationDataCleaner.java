package org.ekstep.ep.samza.cleaner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static java.lang.String.format;

public class LocationDataCleaner implements Cleaner {
    private static final String TAG = LocationDataCleaner.class.getSimpleName();
    static Logger LOGGER = LoggerFactory.getLogger(LocationDataCleaner.class);

    @Override
    public void clean(Map<String, Object> map) {
        Map<String, Object> eks = (Map<String, Object>) ((Map<String, Object>) map.get("edata")).get("eks");
        if (eks == null) {
            return;
        }
        eks.remove("loc");

        LOGGER.debug(format("{0} LOC CLEANED EVENT {1}", TAG, map));
    }

}
