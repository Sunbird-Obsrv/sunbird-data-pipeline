package org.ekstep.ep.samza.domain;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class TObject {
    private String id;
    private String type;
    private String ver;

    @SerializedName("subtype")
    private String subType;

    public TObject(Map<String, Object> eventSpec) {

        this.id = (String) eventSpec.get("mid");
        this.type = "event";

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
