package org.ekstep.ep.samza.search.domain;

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

    private String author;
    private String code;
    private String curriculum;
    private String developer;
    private String domain;
    private String downloadUrl;
    private String edition;
    private String genre;
    private String medium;
    private String portalOwner;
    private String publication;
    private String publisher;
    private String source;
    private String status;
    private String subject;
    private String templateType;
    private String theme;

    private Integer downloads;
    private Integer popularity;
    private Integer rating;
    private Integer size;
    private Integer me_totalDevices;
    private Integer me_totalDownloads;
    private Integer me_totalInteractions;
    private Integer me_totalRatings;
    private Integer me_totalSessionsCount;
    private Integer me_totalSideloads;
    private Integer me_totalTimespent;
    private Integer me_totalUsage;

    private ArrayList<String> language;
    private ArrayList<String> ageGroup;
    private ArrayList<String> keywords;
    private ArrayList<String> concepts;
    private ArrayList<String> gradeLevel;
    private ArrayList<String> collaborators;
    private ArrayList<String> collections;
    private ArrayList<String> methods;
    private ArrayList<String> words;

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

    public String owner() {
        return owner;
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

    public String developer() { return developer; }

    public String domain() { return domain; }

    public String downloadUrl() { return downloadUrl; }

    public String edition() { return edition; }

    public String genre() { return genre; }

    public String medium() { return medium; }

    public String portalOwner() { return portalOwner; }

    public String publication() { return publication; }

    public String publisher() { return publisher; }

    public String source() { return source; }

    public String status() { return status; }

    public String subject() { return subject; }

    public String templateType() { return templateType; }

    public String theme() { return theme; }

    public Integer downloads() { return downloads; }

    public Integer popularity() { return popularity; }

    public Integer rating() { return rating; }

    public Integer size() { return size; }

    public Integer me_totalDevices() { return me_totalDevices; }

    public Integer me_totalDownloads() { return me_totalDownloads; }

    public Integer me_totalInteractions() { return me_totalInteractions; }

    public Integer me_totalRatings() { return me_totalRatings; }

    public Integer me_totalSessionsCount() { return me_totalSessionsCount; }

    public Integer me_totalSideloads() { return me_totalSideloads; }

    public Integer me_totalTimespent() { return me_totalTimespent; }

    public Integer me_totalUsage() { return me_totalUsage; }

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

    public ArrayList<String> gradeLevel() {
        return gradeLevel;
    }

    public ArrayList<String> collaborators() {
        return collaborators;
    }

    public ArrayList<String> collections() {
        return collections;
    }

    public ArrayList<String> methods() {
        return methods;
    }

    public ArrayList<String> words() {
        return words;
    }


    public void setCacheHit(boolean b) {
        this.cacheHit = b;
    }

    public boolean getCacheHit() {
        return cacheHit;
    }

    @Override
    public String toString() {
        return "Content{" +
                "name='" + name + '\'' +
                ", identifier='" + identifier + '\'' +
                ", pkgVersion=" + pkgVersion +
                ", description='" + description + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", contentType='" + contentType + '\'' +
                ", owner='" + owner + '\'' +
                ", lastUpdatedOn='" + lastUpdatedOn + '\'' +
                ", duration='" + duration + '\'' +
                ", author='" + author + '\'' +
                ", code='" + code + '\'' +
                ", curriculum='" + curriculum + '\'' +
                ", developer='" + developer + '\'' +
                ", domain='" + domain + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", edition='" + edition + '\'' +
                ", genre='" + genre + '\'' +
                ", medium='" + medium + '\'' +
                ", portalOwner='" + portalOwner + '\'' +
                ", publication='" + publication + '\'' +
                ", publisher='" + publisher + '\'' +
                ", source='" + source + '\'' +
                ", status='" + status + '\'' +
                ", subject='" + subject + '\'' +
                ", templateType='" + templateType + '\'' +
                ", theme='" + theme + '\'' +
                ", downloads=" + downloads +
                ", popularity=" + popularity +
                ", rating=" + rating +
                ", size=" + size +
                ", me_totalDevices=" + me_totalDevices +
                ", me_totalDownloads=" + me_totalDownloads +
                ", me_totalInteractions=" + me_totalInteractions +
                ", me_totalRatings=" + me_totalRatings +
                ", me_totalSessionsCount=" + me_totalSessionsCount +
                ", me_totalSideloads=" + me_totalSideloads +
                ", me_totalTimespent=" + me_totalTimespent +
                ", me_totalUsage=" + me_totalUsage +
                ", language=" + language +
                ", ageGroup=" + ageGroup +
                ", keywords=" + keywords +
                ", concepts=" + concepts +
                ", gradeLevel=" + gradeLevel +
                ", collaborators=" + collaborators +
                ", collections=" + collections +
                ", methods=" + methods +
                ", words=" + words +
                ", cacheHit=" + cacheHit +
                '}';
    }
}

