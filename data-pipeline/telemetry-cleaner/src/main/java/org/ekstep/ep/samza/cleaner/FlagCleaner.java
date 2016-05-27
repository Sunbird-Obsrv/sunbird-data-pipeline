package org.ekstep.ep.samza.cleaner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static java.text.MessageFormat.format;

public class FlagCleaner implements Cleaner {
    private static final String TAG = FlagCleaner.class.getSimpleName();
    static Logger LOGGER = LoggerFactory.getLogger(FlagCleaner.class);

    @Override
    public void clean(Map<String, Object> map) {
        removeFlags(map);
    }

    private void removeFlags(Map<String, Object> map) {
        Map<String, Object> flags = (Map<String, Object>) map.get("flags");
        if (flags == null) {
            return;
        }
        flags.remove("ldata_processed");
        flags.remove("ldata_obtained");
        flags.remove("child_data_processed");

        LOGGER.debug(format("{0} CLEANED FLAGS {1}", TAG , map));
    }
}
