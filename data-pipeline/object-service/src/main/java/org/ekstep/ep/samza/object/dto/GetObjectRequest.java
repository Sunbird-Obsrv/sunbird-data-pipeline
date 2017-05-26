package org.ekstep.ep.samza.object.dto;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GetObjectRequest {
    private String id;
    private String ver;
    private long ets;
    private Map<String, Object> params;
    private Map<String, Object> request;

    private GetObjectRequest(String id, String ver, long ets, Map<String, Object> params, Map<String, Object> request) {
        this.id = id;
        this.ver = ver;
        this.ets = ets;
        this.params = params;
        this.request = request;
    }

    public static GetObjectRequest create(String id) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("msgid", UUID.randomUUID().toString());

        HashMap<String, Object> request = new HashMap<String, Object>();
        request.put("id", id);

        return new GetObjectRequest("ekstep.object_service.get", "1.0", new Date().getTime(), params, request);
    }
}
