package org.ekstep.ep.samza.domain;

import java.util.Map;

public class Plugin {
	
	private String id;
	private String ver;
	private String category;
	
	public Plugin(Map<String, Object> edata){
		this.id = (String)edata.getOrDefault("pluginid","");
		this.ver = (String)edata.getOrDefault("pluginver","");
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
	
}
