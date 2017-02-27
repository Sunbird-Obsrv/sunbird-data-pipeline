package org.ekstep.ep.samza;

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

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

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
}

