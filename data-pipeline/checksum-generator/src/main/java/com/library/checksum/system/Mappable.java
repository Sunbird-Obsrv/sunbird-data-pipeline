package com.library.checksum.system;

import java.util.Map;

public interface Mappable {

    public Map<String,Object> getMap();

    public void setMetadata(Map<String,Object> metadata);
}
