package org.ekstep.ep.samza.converter.domain;

import java.util.Map;

public class CData {
    private String type;
    private String id;

    public CData(Map<String, Object> cdata) {
        this.id = (String) cdata.get("id");
        this.type = (String) cdata.get("type");
    }

    public CData(String type, String id) {
        this.type = type;
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }
}
