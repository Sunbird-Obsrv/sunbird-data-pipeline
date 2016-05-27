package org.ekstep.ep.samza.cleaner;

import java.util.Map;

public interface Cleaner {
    public void clean(Map<String, Object> map);
}
