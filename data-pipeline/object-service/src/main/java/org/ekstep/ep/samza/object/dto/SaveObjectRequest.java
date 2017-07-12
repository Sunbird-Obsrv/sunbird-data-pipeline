package org.ekstep.ep.samza.object.dto;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SaveObjectRequest {
    private String id;
    private String ver;
    private long ets;
    private Map<String, Object> params;
    private Map<String, Object> request;
    private String channel;

    private SaveObjectRequest(String id, String ver, long ets, Map<String, Object> params, Map<String, Object> request, String channel) {
        this.id = id;
        this.ver = ver;
        this.ets = ets;
        this.params = params;
        this.request = request;
        this.channel = channel;
    }

    public static SaveObjectRequest create(Map<String,Object> requestMap,String channelId) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("msgid", UUID.randomUUID().toString());

        HashMap<String, Object> request = new HashMap<String, Object>();
        request.putAll(requestMap);

        return new SaveObjectRequest("ekstep.object_service.create_or_update", "1.0", new Date().getTime(), params, request, channelId);
    }
}
