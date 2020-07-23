package org.ekstep.ep.samza.domain;

import com.google.gson.Gson;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class QuestionData {


    private List<Map<String, Object>> resvalues;
    private double duration;
    private double score;
    private Question item;
    private long assessts;

    public QuestionData(List<Map<String, Object>> resvalues, double duration, double score, Question item) {

        this.resvalues = resvalues;
        this.duration = duration;
        this.score = score;
        this.item = item;
    }


    public List<Map<String, Object>> getResvalues() {
        ListIterator<Map<String, Object>>
                iterator = resvalues.listIterator();
        while (iterator.hasNext()) {
            Map<String, Object> values = iterator.next();
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                if (null!= entry.getValue() && (!(entry.getValue() instanceof String)))
                    entry.setValue(new Gson().toJson(entry.getValue()));
            }
        }
        return resvalues;
    }

    public double getDuration() {
        return duration;
    }

    public double getScore() {
        return score;
    }

    public Question getItem() {
        return item;
    }

    public long getEts() {
        return assessts;
    }

    public void setEts(long ets) {
        this.assessts = ets;
    }

}


