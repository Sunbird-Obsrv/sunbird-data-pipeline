package org.ekstep.ep.samza.converter.domain;

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
        id = (String) edata.getOrDefault("qid", "");
        maxscore = ((Number) edata.getOrDefault("maxscore", 0)).doubleValue();
        exlength = ((Number) edata.getOrDefault("exlength", 0.0)).doubleValue();
        params = edata.get("params");
        uri = (String) edata.get("uri");
        desc = (String) edata.get("qdesc");
        title = (String) edata.get("qtitle");
        mmc = (List<String>) edata.get("mmc");
        mc = (List<String>) edata.get("mc");
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

    public double getExlength() {
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
