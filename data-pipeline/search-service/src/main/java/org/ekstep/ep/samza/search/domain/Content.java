package org.ekstep.ep.samza.search.domain;

import java.util.ArrayList;

public class Content implements IObject {

    private String name;
    private String identifier;
    private Integer pkgVersion;
    private String description;
    private String mediaType;
    private String contentType;
    private String lastUpdatedOn;
    private String duration;
    private String createdBy;
    private String author;

    private String code;
    private String curriculum;

    private String medium;
    private String source;
    private String status;
    private String subject;

    private Integer downloads;
    private Integer rating;
    private Integer size;

    private ArrayList<String> language;
    private ArrayList<String> ageGroup;
    private ArrayList<String> keywords;
    private ArrayList<String> concepts;
    private ArrayList<String> audience;
    private ArrayList<String> gradeLevel;
    private ArrayList<String> methods;
    private ArrayList<String> domain;
    private ArrayList<String> createdFor;

    private boolean cacheHit;


    public String name() {
        return name;
    }

    public String identifier() {
        return identifier;
    }

    public Integer pkgVersion() {
        return pkgVersion;
    }

    public String description() {
        return description;
    }

    public String mediaType() {
        return mediaType;
    }

    public String contentType() {
        return contentType;
    }

    public String lastUpdatedOn() {
        return lastUpdatedOn;
    }

    public String duration() {
        return duration;
    }

    public String author() { return author; }

    public String code() { return code; }

    public String curriculum() { return curriculum; }

    public String medium() { return medium; }

    public String source() { return source; }

    public String status() { return status; }

    public String subject() { return subject; }

    public String createdBy() { return createdBy; }

    public Integer downloads() { return downloads; }

    public Integer rating() { return rating; }

    public Integer size() { return size; }

    public ArrayList<String> ageGroup() {
        return ageGroup;
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

    public ArrayList<String> audience() { return audience; }

    public ArrayList<String> gradeLevel() {
        return gradeLevel;
    }

    public ArrayList<String> methods() {
        return methods;
    }

    public ArrayList<String> domain() { return domain; }

    public ArrayList<String> createdFor() { return createdFor; }

    @Override
    public String toString() {
        return "Content{" +
                "name='" + name + '\'' +
                ", identifier='" + identifier + '\'' +
                ", pkgVersion=" + pkgVersion +
                ", description='" + description + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", contentType='" + contentType + '\'' +
                ", lastUpdatedOn='" + lastUpdatedOn + '\'' +
                ", duration='" + duration + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", author='" + author + '\'' +
                ", audience='" + audience + '\'' +
                ", code='" + code + '\'' +
                ", curriculum='" + curriculum + '\'' +
                ", medium='" + medium + '\'' +
                ", source='" + source + '\'' +
                ", status='" + status + '\'' +
                ", subject='" + subject + '\'' +
                ", downloads=" + downloads +
                ", rating=" + rating +
                ", size=" + size +
                ", language=" + language +
                ", ageGroup=" + ageGroup +
                ", keywords=" + keywords +
                ", concepts=" + concepts +
                ", gradeLevel=" + gradeLevel +
                ", methods=" + methods +
                ", domain=" + domain +
                ", createdFor=" + createdFor +
                ", cacheHit=" + cacheHit +
                '}';
    }

    @Override
    public boolean getCacheHit() {
        return cacheHit;
    }

    @Override
    public void setCacheHit(boolean b) {
        this.cacheHit = b;
    }
}

