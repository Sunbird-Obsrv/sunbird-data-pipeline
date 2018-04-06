package org.ekstep.ep.samza.search.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ItemSearchRequest {

    public static final String REQUEST_ID = "ekstep.composite-searchItem.searchItem";
    public static final String VERSION = "1.0";
    private final String identifier;

    public ItemSearchRequest(String identifier) {
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
        identifiers.add(identifier);
        filters.put("identifier", identifiers);
        filters.put("objectType","AssessmentItem");
        filters.put("status", status);
        request.put("filters", filters);
        return request;
    }
}
