package org.ekstep.ep.samza.cleaner;


import java.util.List;

import static java.util.Arrays.asList;

public class CleanerFactory {
    public static List<Cleaner> cleaners() {
        return asList(
                new ChildDataCleaner(),
                new LocationDataCleaner(),
                new DeviceDataCleaner(),
                new FlagCleaner());
    }
}
