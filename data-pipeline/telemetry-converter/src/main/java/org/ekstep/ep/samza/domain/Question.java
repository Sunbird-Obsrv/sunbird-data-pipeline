package org.ekstep.ep.samza.domain;

import java.util.Map;

public class Question {

	private String id;
	private Integer maxscore;
	private Double exlength;
	private Object params;
	private String uri;
	private String desc;
	private String title;
	private String[] mmc;
	private String[] mc;

	public Question(Map<String, Object> edata) {
		this.id = (String) edata.getOrDefault("qid", "");
		this.maxscore = (Integer) edata.getOrDefault("maxscore", 0);
		this.exlength = (Double) edata.getOrDefault("exlength", 0.0);
		this.params = edata.get("params");
		this.uri = (String) edata.get("uri");
		this.desc = (String) edata.get("qdesc");
		this.title = (String) edata.get("qtitle");
		this.mmc = (String[]) edata.get("mmc");
		this.mc = (String[]) edata.get("mc");
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getMaxscore() {
		return maxscore;
	}

	public void setMaxscore(Integer maxscore) {
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

	public String[] getMmc() {
		return mmc;
	}

	public void setMmc(String[] mmc) {
		this.mmc = mmc;
	}

	public String[] getMc() {
		return mc;
	}

	public void setMc(String[] mc) {
		this.mc = mc;
	}
}
