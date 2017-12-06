package org.ekstep.ep.samza.cleaner;

import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static java.lang.String.format;

public class LocationDataCleaner implements Cleaner {
    private static final String TAG = LocationDataCleaner.class.getSimpleName();
    static Logger LOGGER = LoggerFactory.getLogger(LocationDataCleaner.class);

    @Override
    public void clean(Telemetry telemetry) {
        NullableValue<Object> edata = telemetry.read("edata");
        if (edata.isNull()) {
            return;
        }

        Map<String, Object> edataMap = (Map<String, Object>)  edata.value();
        edataMap.remove("loc");

        LOGGER.debug(format("{0} LOC CLEANED EVENT {1}", TAG, telemetry.getMap()));
    }

}
