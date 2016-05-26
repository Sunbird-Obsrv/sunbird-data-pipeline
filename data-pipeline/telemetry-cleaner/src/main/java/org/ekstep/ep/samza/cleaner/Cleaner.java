package org.ekstep.ep.samza.cleaner;

import java.util.Map;

public interface Cleaner {
    public void process(Map<String, Object> map);
}
