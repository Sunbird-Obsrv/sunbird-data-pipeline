package com.library.checksum.system;

import java.util.Map;

public interface Strategy {
    public String createChecksum(Map<String,Object> event, String[] keys);
}
