package org.ekstep.ep.samza.object.service;

import org.ekstep.ep.samza.object.dto.ObjectResponse;

import java.io.IOException;
import java.util.Map;

public interface ObjectService {
    ObjectResponse createOrUpdate(Map<String, Object> requestMap) throws IOException;
}
