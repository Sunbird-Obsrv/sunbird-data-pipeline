package org.ekstep.ep.samza.domain;

import java.util.Map;

public class Actor {
    private String id = "";
    private String type = "telemetry-sync";

    public Actor() {

    }

    public Actor(Map<String, Object> eventSpec) {
        this.id = (String) eventSpec.get("id");
    }

}
