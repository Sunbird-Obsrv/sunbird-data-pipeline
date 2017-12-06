package org.ekstep.ep.samza.cleaner;

import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static java.text.MessageFormat.format;

public class DeviceDataCleaner implements Cleaner {
    private static final String TAG = DeviceDataCleaner.class.getSimpleName();
    static Logger LOGGER = LoggerFactory.getLogger(DeviceDataCleaner.class);

    @Override
    public void clean(Telemetry telemetry) {
        NullableValue<Object> dspec = telemetry.read("edata.dsepc");

        if (dspec.isNull()) {
            return;
        }

        Map<String, Object> dspecMap = (Map<String, Object>) dspec.value();

        dspecMap.remove("dlocname");
        dspecMap.remove("dname");
        dspecMap.remove("id");

        LOGGER.debug(format("{0} DSPEC CLEANED EVENT {1}", TAG, telemetry.getMap()));
    }
}
