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

}
