package org.ekstep.ep.samza.domain;

import java.util.List;
import java.util.Map;

public class Question {

	private String id;
	private Double maxscore;
	private Double exlength;
	private Object params;
	private String uri;
	private String desc;
	private String title;
	private List<String> mmc;
	private List<String> mc;

	public Question(Map<String, Object> edata) {
		this.id = (String) edata.getOrDefault("qid", "");
		this.maxscore = ((Number) edata.getOrDefault("maxscore", 0)).doubleValue();
		this.exlength = ((Number) edata.getOrDefault("exlength", 0.0)).doubleValue();
		this.params = edata.get("params");
		this.uri = (String) edata.get("uri");
		this.desc = (String) edata.get("qdesc");
		this.title = (String) edata.get("qtitle");
		this.mmc = (List<String>) edata.get("mmc");
		this.mc = (List<String>) edata.get("mc");
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Double getMaxscore() {
		return maxscore;
	}

	public void setMaxscore(Double maxscore) {
		this.maxscore = maxscore;
	}

	public Double getExlength() {
		return exlength;
	}

	public void setExlength(Double exlength) {
		this.exlength = exlength;
	}

	public Object getParams() {
		return params;
	}

	public void setParams(Object params) {
		this.params = params;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getMmc() {
		return mmc;
	}

	public void setMmc(List<String> mmc) {
		this.mmc = mmc;
	}

	public List<String> getMc() {
		return mc;
	}

	public void setMc(List<String> mc) {
		this.mc = mc;
	}
}
