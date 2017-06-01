package org.ekstep.ep.samza.util;


import org.joda.time.DateTime;

public interface TimeProvider {
    public DateTime getCurrentTime();
}
