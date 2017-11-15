package org.ekstep.ep.samza.search.domain;

import java.util.ArrayList;

public class Item implements IObject {
    private Integer num_answers;

    private String title;
    private String name;
    private String template;
    private String type;
    private String owner;
    private String status;
    private String qlevel;

    private ArrayList<String> language;
    private ArrayList<String> keywords;
    private ArrayList<String> concepts;
    private ArrayList<String> gradeLevel;

    private boolean cacheHit;

    public String title() { return title; }

    public Integer num_answers() {
        return num_answers;
    }

    public String name() {
        return name;
    }

    public String template() {
        return template;
    }

    public String type() {
        return type;
    }

    public String status() {
        return status;
    }

    public String owner() {
        return owner;
    }

    public String qlevel() {
        return qlevel;
    }

    public ArrayList<String> language() {
        return language;
    }

    public ArrayList<String> keywords() {
        return keywords;
    }

    public ArrayList<String> concepts() {
        return concepts;
    }

    public ArrayList<String> gradeLevel() {
        return gradeLevel;
    }

    @Override
    public boolean getCacheHit() {
        return false;
    }

    @Override
    public void setCacheHit(boolean b) {

    }

    @Override
    public String toString() {
        return "Item{" +
                "num_answers=" + num_answers +
                ", title='" + title + '\'' +
                ", name='" + name + '\'' +
                ", template='" + template + '\'' +
                ", type='" + type + '\'' +
                ", owner='" + owner + '\'' +
                ", status='" + status + '\'' +
                ", qlevel='" + qlevel + '\'' +
                ", language=" + language +
                ", keywords=" + keywords +
                ", concepts=" + concepts +
                ", gradeLevel=" + gradeLevel +
                ", cacheHit=" + cacheHit +
                '}';
    }
}
