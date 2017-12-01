package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.reader.Telemetry;

public class Visit {

    private String objid;
    private String objtype;
    private String objver;
    private String section;
    private Integer index;

    public Visit(Telemetry reader) {
        objid = reader.<String>read("edata.eks.id").valueOrDefault("");
        objtype = "";
        objver = "";
        section = "";
        index = 0;
    }

    public String getObjid() {
        return objid;
    }

    public String getObjtype() {
        return objtype;
    }

    public String getObjver() {
        return objver;
    }

    public String getSection() {
        return section;
    }

    public Integer getIndex() {
        return index;
    }

}
