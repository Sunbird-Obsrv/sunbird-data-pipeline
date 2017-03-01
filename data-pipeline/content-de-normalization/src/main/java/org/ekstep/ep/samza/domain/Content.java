package org.ekstep.ep.samza.domain;

import java.util.ArrayList;

public class Content {
    private String name;
    private String identifier;
    private Integer pkgVersion;
    private String description;
    private String mediaType;
    private String contentType;
    private String owner;
    private String lastUpdatedOn;
    private String duration;

    private boolean cacheHit;

    private ArrayList<String> language;
    private ArrayList<String> ageGroup;
    private ArrayList<String> keywords;
    private ArrayList<String> concepts;
    private ArrayList<String> gradeLevel;

    public String name() { return name; }

    public String identifier() {return identifier;}

    public Integer pkgVersion() {
        return pkgVersion;
    }

    public String description() {return description;}

    public String mediaType() { return mediaType; }

    public String contentType() { return contentType; }

    public String owner() { return owner; }

    public String lastUpdatedOn() { return lastUpdatedOn; }

    public String duration() { return duration; }

    public void setCacheHit(boolean b) { this.cacheHit = b;}

    public boolean getCacheHit() { return cacheHit; }

    public ArrayList<String> ageGroup() { return ageGroup; }

    public ArrayList<String> language() { return language; }

    public ArrayList<String> keywords() { return keywords; }

    public ArrayList<String> concepts() { return concepts; }

    public ArrayList<String> gradeLevel() { return gradeLevel; }
}

