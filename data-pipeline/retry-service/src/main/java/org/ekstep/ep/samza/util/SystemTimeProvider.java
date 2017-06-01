package org.ekstep.ep.samza.util;


import org.joda.time.DateTime;

public class SystemTimeProvider implements TimeProvider {
    @Override
    public DateTime getCurrentTime() {
        return DateTime.now();
    }
}
