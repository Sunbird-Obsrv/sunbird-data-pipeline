package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.reader.Telemetry;

public class TObject {
    private String id;
    private String type;
    private String ver;
    private Rollup rollUp;
    transient private String defaultType = "Content";
    
    public TObject(Telemetry reader){
    	this.id = reader.<String>read("gdata.id").valueOrDefault("");
    	
    	if("genieservices.android".equals(this.id) || "org.ekstep.genieservices".equals(this.id)){
    		this.type = "";
    	} else {
    		this.type = defaultType;
    	}
    	this.ver = reader.<String>read("gdata.ver").valueOrDefault("");
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

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public Rollup getRollUp() {
        return rollUp;
    }

    public void setRollUp(Rollup rollUp) {
        this.rollUp = rollUp;
    }
}
