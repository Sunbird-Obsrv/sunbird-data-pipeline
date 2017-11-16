package org.ekstep.ep.samza.domain;

import java.util.HashMap;
import java.util.Map;

public class EdataObject {

	private String id;
	private String type;
	private String ver;
	private String subtype;
	private String name;
	private String code;
	private HashMap<String, String> parent;
	
	public EdataObject(Map<String, Object> edata) {
		this.id = (String)edata.getOrDefault("objectid","");
		this.type = (String)edata.getOrDefault("objecttype","");
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
	public String getSubtype() {
		return subtype;
	}
	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public HashMap<String, String> getParent() {
		return parent;
	}
	public void setParent(HashMap<String, String> parent) {
		this.parent = parent;
	}
}
