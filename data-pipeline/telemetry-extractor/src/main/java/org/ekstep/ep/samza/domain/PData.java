package org.ekstep.ep.samza.domain;

import java.util.Map;

public class PData {

    private String id = "";
    private String pid = "";
    private String ver;

    public PData() {

    }

    public PData(Map<String, String> data) {
        this.id = data.get("id");
        this.pid = data.get("pid");
        this.ver = data.get("ver");
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public String getVer() {
        return ver;
    }

}
