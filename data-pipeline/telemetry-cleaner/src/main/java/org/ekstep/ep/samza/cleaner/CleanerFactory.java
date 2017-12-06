package org.ekstep.ep.samza.cleaner;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

public class CleanerFactory {
    static Logger LOGGER = LoggerFactory.getLogger(DeviceDataCleaner.class);
    private static List<String> eventsToSkip;

    public CleanerFactory(List<String> eventsToSkip) {
        this.eventsToSkip = eventsToSkip;
    }

    public void clean(Map<String, Object> map) {
        for (Cleaner cleaner : cleaners()) {
            cleaner.clean(map);
        }
    }

    public boolean shouldSkipEvent(String eventID) {
        for (String eventToSkip : eventsToSkip) {
            Pattern p = Pattern.compile(eventToSkip);
            Matcher m = p.matcher(eventID);
            if (m.matches()) {
                LOGGER.info(m.toString(), "SKIPPING EVENT");
                return true;
            }
        }
        return false;
    }

    private List<Cleaner> cleaners() {
        return asList(
                new ChildDataCleaner(),
                new LocationDataCleaner(),
                new DeviceDataCleaner(),
                new MetadataCleaner());
    }
}
