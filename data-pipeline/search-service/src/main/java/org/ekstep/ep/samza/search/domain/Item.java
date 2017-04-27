package org.ekstep.ep.samza.search.domain;

import java.util.ArrayList;

public class Item {
    private Integer version;
    private Integer num_answers;

    private String title;
    private String name;
    private String template;
    private String type;
    private String owner;
    private String status;
    private String question;
    private String createdBy;
    private String createdOn;
    private String lastUpdatedBy;
    private String lastUpdatedOn;
    private String qlevel;

    private ArrayList<String> language;
    private ArrayList<String> keywords;
    private ArrayList<String> concepts;
    private ArrayList<String> gradeLevel;

    private boolean cacheHit;

    public void setCacheHit(boolean b) { this.cacheHit = b;}

    public boolean getCacheHit() { return cacheHit; }

    public String title() { return title; }

    public Integer version() {
        return version;
    }

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

    public String question() {
        return question;
    }

    public String status() {
        return status;
    }

    public String owner() {
        return owner;
    }

    public String createdBy() {
        return createdBy;
    }

    public String createdOn() {
        return createdOn;
    }

    public String lastUpdatedBy() {
        return lastUpdatedBy;
    }

    public String lastUpdatedOn() {
        return lastUpdatedOn;
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

    public boolean isCacheHit() {
        return cacheHit;
    }


    @Override
    public String toString() {
        return "Item{" +
                "version=" + version +
                ", num_answers=" + num_answers +
                ", title='" + title + '\'' +
                ", name='" + name + '\'' +
                ", template='" + template + '\'' +
                ", type='" + type + '\'' +
                ", owner='" + owner + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdOn='" + createdOn + '\'' +
                ", lastUpdatedBy='" + lastUpdatedBy + '\'' +
                ", lastUpdatedOn='" + lastUpdatedOn + '\'' +
                ", qlevel='" + qlevel + '\'' +
                ", language=" + language +
                ", keywords=" + keywords +
                ", concepts=" + concepts +
                ", gradeLevel=" + gradeLevel +
                '}';
    }
}
