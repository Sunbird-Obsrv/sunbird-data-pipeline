package org.ekstep.ep.samza.cleaner;

import org.ekstep.ep.samza.reader.Telemetry;

public interface Cleaner {
    public void clean(Telemetry telemetry);
}
