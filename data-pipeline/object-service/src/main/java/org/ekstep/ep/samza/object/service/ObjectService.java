package org.ekstep.ep.samza.object.service;

import org.ekstep.ep.samza.object.dto.GetObjectResponse;
import org.ekstep.ep.samza.object.dto.SaveObjectDetailsResponse;
import org.ekstep.ep.samza.object.dto.SaveObjectResponse;

import java.io.IOException;
import java.util.Map;

public interface ObjectService {
    SaveObjectResponse createOrUpdate(Map<String, Object> requestMap, String channelId) throws IOException;

    GetObjectResponse get(String id, String channelId) throws IOException;

    SaveObjectDetailsResponse saveDetails(String id, String details, String channelId) throws IOException;
}
