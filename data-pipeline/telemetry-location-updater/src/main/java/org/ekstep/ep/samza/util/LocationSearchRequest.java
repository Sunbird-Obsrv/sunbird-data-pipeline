package org.ekstep.ep.samza.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class LocationSearchRequest {
    public static final String REQUEST_ID = "unique API ID";
    public static final String VERSION = "1.0";
    private final List<String> identifier;

    public LocationSearchRequest(List<String> identifier) {
        this.identifier = identifier;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> m = new HashMap<String, Object>();
        m.put("id", REQUEST_ID);
        m.put("ts", new Date().toString());
        m.put("ver", VERSION);
        m.put("request", getRequest());
        m.put("params", new HashMap<String, String>());

        return m;
    }

    private HashMap<String,Object> getRequest() {
        HashMap<String, Object> request = new HashMap<String, Object>();
        HashMap<String, Object> filters = new HashMap<String, Object>();
        ArrayList<String> identifiers = new ArrayList<String>();
        ArrayList<String> status = new ArrayList<String>();
        filters.put("id", identifier);
        filters.put("type", "state");
        request.put("filters", filters);
        return request;
    }
}
