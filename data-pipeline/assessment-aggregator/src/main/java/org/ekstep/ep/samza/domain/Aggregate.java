package org.ekstep.ep.samza.domain;

import com.datastax.driver.core.UDTValue;

import java.util.List;


public class Aggregate {

    private double totalScore;
    private double totalMaxScore;
    private String grandTotal;
    private List<UDTValue> questionsList;

    public Aggregate(double totalScore, double totalMaxScore, String grandTotal, List<UDTValue> questionsList) {

        this.totalScore = totalScore;
        this.totalMaxScore = totalMaxScore;
        this.questionsList = questionsList;
        this.grandTotal = grandTotal;
    }

    public double getTotalScore() {
        return totalScore;
    }


    public double getTotalMaxScore() {
        return totalMaxScore;
    }

    public String getGrandTotal() {
        return grandTotal;
    }

    public List<UDTValue> getQuestionsList() {
        return questionsList;
    }


}
