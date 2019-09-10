package org.ekstep.ep.samza.domain;

import java.util.List;
import java.util.Map;

public class QuestionData {


    private List<Map<String, Object>> resvalues;
    private double duration;
    private int score;
    private Question item;

    public QuestionData(List<Map<String, Object>> resvalues, double duration, int score, Question item) {

        this.resvalues = resvalues;
        this.duration = duration;
        this.score = score;
        this.item = item;
    }


    public List<Map<String, Object>> getResvalues() {
        return resvalues;
    }

    public double getDuration() {
        return duration;
    }

    public int getScore() {
        return score;
    }

    public Question getItem() {
        return item;
    }
}


