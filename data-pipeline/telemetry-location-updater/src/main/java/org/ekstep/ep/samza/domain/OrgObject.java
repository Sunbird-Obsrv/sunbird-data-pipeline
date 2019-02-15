package org.ekstep.ep.samza.domain;

import java.util.List;

public class OrgObject {
    private String name;
    private String identifier;
    private List<String> locationIds;


    public String name() {
        return name;
    }

    public String identifier() {
        return identifier;
    }

    public List<String> locationIds() {
        return locationIds;
    }


    @Override
    public String toString() {
        return "OrgObject{" +
                "name='" + name + '\'' +
                ", identifier='" + identifier + '\'' +
                ", locationIds=[" + String.join(",", locationIds) + "]" +
                '}';
    }
}
