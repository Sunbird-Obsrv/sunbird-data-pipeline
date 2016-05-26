package org.ekstep.ep.samza.cleaner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static java.text.MessageFormat.format;

public class DeviceDataCleaner implements Cleaner {
    private static final String TAG = DeviceDataCleaner.class.getSimpleName();
    static Logger LOGGER = LoggerFactory.getLogger(DeviceDataCleaner.class);

    @Override
    public void process(Map<String, Object> map) {
        removeDeviceSpec(map);
    }

    private void removeDeviceSpec(Map<String, Object> map) {
        Map<String, Object> dspec = (Map<String, Object>) ((Map<String, Object>) ((Map<String, Object>) map.get("edata")).get("eks")).get("dspec");
        if (dspec == null) {
            return;
        }
        dspec.remove("dlocname");
        dspec.remove("dname");
        dspec.remove("id");

        LOGGER.debug(format("{0} CLEANED DSPEC {1}", TAG , map));
    }
}
