package org.ekstep.ep.samza.domain;

public class LocObject {
    private String code;
    private String name;
    private String id;
    private String type;


    public String name() {
        return name;
    }

    public String code() {
        return code;
    }

    public String id() {
        return id;
    }

    public String type() {
        return type;
    }


    @Override
    public String toString() {
        return "OrgObject{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", id=" + id +
                ", type=" + type +
                '}';
    }
}
