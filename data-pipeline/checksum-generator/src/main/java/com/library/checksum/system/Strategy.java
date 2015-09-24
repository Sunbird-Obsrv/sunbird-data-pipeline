package com.library.checksum.system;

import java.util.Map;

public interface Strategy {
    public Mappable generateChecksum(Mappable event);
}
