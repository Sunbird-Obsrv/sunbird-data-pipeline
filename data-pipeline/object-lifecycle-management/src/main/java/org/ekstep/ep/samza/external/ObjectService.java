package org.ekstep.ep.samza.external;

import java.io.IOException;
import java.util.Map;

public interface ObjectService {
    ObjectResponse createOrUpdate(Map<String, Object> requestMap) throws IOException;
}
