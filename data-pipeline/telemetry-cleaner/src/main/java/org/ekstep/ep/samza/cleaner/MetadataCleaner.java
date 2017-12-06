package org.ekstep.ep.samza.cleaner;

import org.ekstep.ep.samza.reader.Telemetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

import static java.text.MessageFormat.format;

public class MetadataCleaner implements Cleaner {
    private static final String TAG = MetadataCleaner.class.getSimpleName();
    static Logger LOGGER = LoggerFactory.getLogger(MetadataCleaner.class);

    @Override
    public void clean(Telemetry telemetry) {
        Map<String,Object> map = telemetry.getMap();
        map.remove("metadata");
        map.remove("flags");
        map.remove("ready_to_index");
        map.remove("key");

        LOGGER.debug(format("{0} CLEANED METADATA & FLAGS {1}", TAG , map));
    }
}
