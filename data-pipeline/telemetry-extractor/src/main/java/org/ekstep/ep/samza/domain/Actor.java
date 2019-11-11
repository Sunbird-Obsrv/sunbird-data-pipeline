package org.ekstep.ep.samza.domain;

import java.util.Map;

public class Actor {
    private String id = "";
    public Actor() {

    }

    public Actor(Map<String, Object> eventSpec) {
        this.id = (String)eventSpec.get("id");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
