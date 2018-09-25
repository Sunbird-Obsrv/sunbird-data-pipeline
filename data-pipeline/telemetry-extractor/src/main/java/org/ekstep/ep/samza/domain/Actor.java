package org.ekstep.ep.samza.domain;

import java.util.Map;

public class Actor {
    private String id = "";
    private String type = "telemetry-sync";

    public Actor() {

    }

    public Actor(Map<String, Object> eventSpec) {
        this.id = (String)eventSpec.get("id");
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }
}
