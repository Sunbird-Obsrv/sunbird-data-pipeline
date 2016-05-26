package org.ekstep.ep.samza.cleaner;


import java.util.ArrayList;

public class CleanerFactory {
    public static ArrayList<Cleaner> cleaners(){
        ArrayList<Cleaner> cleaners = new ArrayList<Cleaner>();
        cleaners.add(new ChildDataCleaner());
        cleaners.add(new LocationDataCleaner());
        cleaners.add(new DeviceDataCleaner());
        return cleaners;
    }
}
