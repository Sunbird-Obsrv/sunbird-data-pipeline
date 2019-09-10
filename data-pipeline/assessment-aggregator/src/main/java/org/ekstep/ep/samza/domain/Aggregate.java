package org.ekstep.ep.samza.domain;

import com.datastax.driver.core.UDTValue;

import java.util.List;


public class Aggregate {

    private int totalScore;
    private int totalMaxScore;
    private List<UDTValue> questionsList;

    public Aggregate(int totalScore, int totalMaxScore, List<UDTValue> questionsList) {

        this.totalScore = totalScore;
        this.totalMaxScore = totalMaxScore;
        this.questionsList = questionsList;
    }

    public int getTotalScore() {
        return totalScore;
    }


    public int getTotalMaxScore() {
        return totalMaxScore;
    }


    public List<UDTValue> getQuestionsList() {
        return questionsList;
    }


}
