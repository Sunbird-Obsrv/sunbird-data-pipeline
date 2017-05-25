package org.ekstep.ep.samza.object.service;

import org.ekstep.ep.samza.object.dto.GetObjectResponse;
import org.ekstep.ep.samza.object.dto.SaveObjectResponse;

import java.io.IOException;
import java.util.Map;

public interface ObjectService {
    SaveObjectResponse createOrUpdate(Map<String, Object> requestMap) throws IOException;

    GetObjectResponse get(String id);
}
