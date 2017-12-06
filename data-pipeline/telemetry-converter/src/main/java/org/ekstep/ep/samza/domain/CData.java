package org.ekstep.ep.samza.domain;

import java.util.Map;

public class CData {
    private String type;
    private String id;

    public CData(Map<String, Object> cdata) {
        this.id = (String) cdata.get("id");
        this.type = (String) cdata.get("type");
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }
}
