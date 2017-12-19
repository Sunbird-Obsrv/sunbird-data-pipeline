package org.ekstep.ep.samza.converter.domain;

import com.google.gson.annotations.SerializedName;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;

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
    
    public TObject(Telemetry reader){
    	this.id = computeId(reader);
        String type = reader.<String>read("edata.eks.objecttype").valueOrDefault(null);
        if (type == null) {
            type = reader.<String>read("edata.eks.type").valueOrDefault(defaultType);
        }
    	if("genieservices.android".equals(this.id) || "genieservice.android".equals(this.id) || "org.ekstep.genieservices".equals(this.id)){
    		this.type = "";
    	} else {
    		this.type = type;
    	}
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

    private String computeId(Telemetry reader) {
        NullableValue<String> gdataId = reader.<String>read("gdata.id");
        if (!gdataId.isNull()) {
            return gdataId.value();
        }

        NullableValue<String> edataEksId = reader.<String>read("edata.eks.id");
        if (!edataEksId.isNull()) {
            return edataEksId.value();
        }

        return reader.<String>read("edata.eks.objectid").valueOrDefault("");
    }

    public String getId() {
        return id;
    }

    public Map<String, String> getParent() { return parent; }

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
