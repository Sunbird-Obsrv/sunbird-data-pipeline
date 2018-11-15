package org.ekstep.ep.samza.domain;

public class OrgObject {
    private String name;
    private String identifier;
    private String locationId;


    public String name() {
        return name;
    }

    public String identifier() {
        return identifier;
    }

    public String locationId() {
        return locationId;
    }


    @Override
    public String toString() {
        return "OrgObject{" +
                "name='" + name + '\'' +
                ", identifier='" + identifier + '\'' +
                ", locationId=" + locationId +
                '}';
    }
}
