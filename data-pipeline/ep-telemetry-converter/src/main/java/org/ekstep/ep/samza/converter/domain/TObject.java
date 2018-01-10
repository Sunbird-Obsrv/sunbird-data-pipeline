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

    transient private String defaultType = "Content";
    
    public TObject(Telemetry reader) throws TelemetryReaderException {
    	//this.id = computeId(reader);
    	
    	setIdType(reader);
    	
    	this.ver = reader.<String>read("gdata.ver").valueOrDefault("");
    	this.subType = reader.<String>read("edata.eks.subtype").valueOrDefault("");

        String parentId = reader.<String>read("edata.eks.parentid").valueOrDefault("");
        String parentType = reader.<String>read("edata.eks.parenttype").valueOrDefault("");
        this.parent.put("id", parentId);
        this.parent.put("type", parentType);

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

    private void setIdType(Telemetry reader) throws TelemetryReaderException {
        
    	String id = "";
        String type = "";
        
    	String eid = reader.mustReadValue("eid");
    	
    	switch (eid.substring(0, 2)) {
    		case "GE": 
    			id = reader.<String>read("gdata.id").valueOrDefault("");
                if("genieservices.android".equals(this.id) || "genieservice.android".equals(this.id) || "org.ekstep.genieservices".equals(this.id))
            		type = "";
            		
                if ("GE_FEEDBACK".equals(eid)) {
                    // for GE_FEEDBACK, object.id is edata.eks.context.id
                    id = reader.mustReadValue("edata.eks.context.id");
                    type = reader.mustReadValue("edata.eks.context.type");
                }
                break;
    		case "CE":
    			id = reader.<String>read("context.content_id").valueOrDefault("");
    			System.out.println(id);
            	if(eid.equals("CE_ERROR"))
            		type = reader.<String>read("edata.eks.objecttype").valueOrDefault(defaultType);
            	break;
    		case "OE":
    			id = reader.<String>read("gdata.id").valueOrDefault("");
                type = this.defaultType;
                break;
    		case "BE":
    			id = reader.<String>read("edata.eks.id").valueOrDefault("");
                type = reader.<String>read("edata.eks.type").valueOrDefault(defaultType);
                break;
    		case "CP":
    			id = reader.<String>read("context.content_id").valueOrDefault("");
    			break;
    		default:
    			id = "";
    			type = defaultType;
    			break;
    	}
    	// setting id & type
    	this.id = id;
    	this.type = type;
    }
    
//    private String computeId(Telemetry reader) throws TelemetryReaderException {
//        String eid = reader.mustReadValue("eid");
//        if (eid.startsWith("GE_")) {
//            String id = reader.<String>read("gdata.id").valueOrDefault("");
//            if ("GE_FEEDBACK".equals(eid)) {
//                // for GE_FEEDBACK, object.id is edata.eks.context.id
//                id = reader.mustReadValue("edata.eks.context.id");
//            }
//            return id;
//        }
//
//        if (eid.startsWith("CE_") || eid.startsWith("CP_")) {
//            return reader.<String>read("context.content_id").valueOrDefault("");
//        }
//
//        if (eid.startsWith("OE_")) {
//            return reader.<String>read("gdata.id").valueOrDefault("");
//        }
//
//        if (eid.startsWith("BE_")) {
//            return reader.<String>read("edata.eks.id").valueOrDefault("");
//        }
//
//        return "";
//    }

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
