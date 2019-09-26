package org.ekstep.ep.samza.domain;

import java.util.List;
import java.util.Map;

public class Question {
    private String id;
    private double maxscore;
    private List<Map<String, Object>> params;
    private String title;
    private String type;
    private String desc;

    public Question(String id, double maxscore, List<Map<String, Object>> params, String title, String type, String desc) {
        this.id = id;
        this.maxscore = maxscore;
        this.params = params;
        this.title = title;
        this.type = type;
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public double getMaxScore() {
        return maxscore;
    }


    public List<Map<String, Object>> getParams() {
        return params;
    }

    public String getTitle() {
        return title;
    }


    public String getType() {
        return type;
    }


    public String getDesc() {
        return desc;
    }


}
