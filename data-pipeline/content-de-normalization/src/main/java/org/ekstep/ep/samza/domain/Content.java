package org.ekstep.ep.samza.domain;

import java.util.ArrayList;

public class Content {
    private String name;
    private String description;
    private ArrayList<String> ageGroup;
    private String mediaType;
    private String contentType;
    private ArrayList<String> language;
    private String owner;
    private String lastUpdatedOn;
    private boolean cacheHit;

    public String name() {
        return name;
    }

    public String description() {return description;}

    public ArrayList<String> ageGroup() {
        return ageGroup;
    }

    public String mediaType() {
        return mediaType;
    }

    public String contentType() {
        return contentType;
    }

    public ArrayList<String> language() {
        return language;
    }

    public String owner() {
        return owner;
    }

    public String lastUpdatedOn() {
        return lastUpdatedOn;
    }

    public void setCacheHit(boolean b) {
        this.cacheHit = b;
    }

    public boolean getCacheHit() {
        return cacheHit;
    }
}

