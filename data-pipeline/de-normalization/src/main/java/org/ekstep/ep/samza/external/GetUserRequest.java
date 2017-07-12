package org.ekstep.ep.samza.external;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GetUserRequest {
    private String id;
    private String ver;
    private long ets;
    private Map<String, Object> params;
    private String channelid;

    private GetUserRequest(String id, String ver, long ets, Map<String, Object> params, String channelId) {
        this.id = id;
        this.ver = ver;
        this.ets = ets;
        this.params = params;
        this.channelid = channelId;
    }

    public static GetUserRequest create(String channelId) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("msgid", UUID.randomUUID().toString());
        return new GetUserRequest("ekstep.users.get", "1.0", new Date().getTime(), params, channelId);
    }
}
