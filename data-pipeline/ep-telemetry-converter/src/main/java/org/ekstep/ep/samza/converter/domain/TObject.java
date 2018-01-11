package org.ekstep.ep.samza.converter.domain;

import com.google.gson.annotations.SerializedName;
import org.ekstep.ep.samza.reader.Telemetry;
import org.ekstep.ep.samza.reader.TelemetryReaderException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TObject {
    private String id;
    private String type;
    private String ver;
    private Rollup rollUp;

    @SerializedName("subtype")
    private String subType;

    private final HashMap<String, String> parent = new HashMap<>();
    
    public TObject(Telemetry reader) throws TelemetryReaderException {
    	
    	setObjectAttr(reader);
    	
    	ArrayList<Map> cData = (ArrayList<Map>) reader.read("cdata").value();

        if(cData != null)
            createRollupData(cData);
    }

    private void createRollupData(ArrayList<Map> cData) {
        for (Map data : cData) {
            if (data.containsKey("type") && data.get("type").equals("collection")) {
                String[] rollupData = ((String) data.get("id")).split("/");
                this.setRollUp(new Rollup(rollupData));
            }
        }
    }

private void setObjectAttr(Telemetry reader) throws TelemetryReaderException {
        
        String eid = reader.mustReadValue("eid");
        switch (eid.substring(0, 2)) {
            case "GE": 
                if ("GE_FEEDBACK".equals(eid)) {
                    this.id = reader.mustReadValue("edata.eks.context.id");
                    this.type = reader.mustReadValue("edata.eks.context.type");
                }
                break;
            case "CE":
                this.id = reader.<String>read("context.content_id").valueOrDefault("");
                this.type = "Content";
                break;
            case "OE":
                this.id = reader.<String>read("gdata.id").valueOrDefault("");
                this.type = "Content";
                this.ver = reader.<String>read("gdata.ver").valueOrDefault("");
                break;
            case "BE":
                if("BE_OBJECT_LIFECYCLE".equals(eid)) {
                    this.id = reader.<String>read("edata.eks.id").valueOrDefault("");
                    this.type = reader.<String>read("edata.eks.type").valueOrDefault("");
                    this.ver = reader.<String>read("edata.eks.ver").valueOrDefault("");
                    this.subType = reader.<String>read("edata.eks.subtype").valueOrDefault("");
                    String parentId = reader.<String>read("edata.eks.parentid").valueOrDefault("");
                    String parentType = reader.<String>read("edata.eks.parenttype").valueOrDefault("");
                    this.parent.put("id", parentId);
                    this.parent.put("type", parentType);
                }
                break;
            case "CP":
                this.id = reader.<String>read("context.content_id").valueOrDefault("");
                if(!id.isEmpty()) {
                    this.type = "Content";
                }
                break;
            default:
                break;
        }
    }

    public String getId() {
        return id;
    }

    public Map<String, String> getParent() {
        return parent;
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

    public String getSubType() {
        return subType;
    }
}
