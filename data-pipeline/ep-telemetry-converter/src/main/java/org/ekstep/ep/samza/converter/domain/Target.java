package org.ekstep.ep.samza.converter.domain;

import java.util.HashMap;
import java.util.Map;

public class Target {
	
	private String id;
	private String  ver;
	private String type;
	private HashMap<String, String> parent;
	
	public Target(Map<String, Object> edata){
		this.id = (String)edata.getOrDefault("target", edata.getOrDefault("qid", ""));
		this.type = (String)edata.getOrDefault("type", "");
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public HashMap<String, String> getParent() {
		return parent;
	}

	public void setParent(HashMap<String, String> parent) {
		this.parent = parent;
	}
	
	
}
