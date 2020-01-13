package org.ekstep.ep.samza.domain;

import com.google.gson.Gson;

import java.util.List;
import java.util.ListIterator;
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
        ListIterator<Map<String, Object>>
                iterator = params.listIterator();
        while (iterator.hasNext()) {
            Map<String, Object> values = iterator.next();
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                if (null!= entry.getValue() && (!(entry.getValue() instanceof String)))
                    entry.setValue(new Gson().toJson(entry.getValue()));
            }
        }
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
