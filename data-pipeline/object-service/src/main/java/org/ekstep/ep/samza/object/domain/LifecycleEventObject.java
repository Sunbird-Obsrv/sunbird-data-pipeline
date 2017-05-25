package org.ekstep.ep.samza.object.domain;


public class LifecycleEventObject {
    public String id;
    public String type;
    public String subType;
    public String parentId;
    public String parentType;
    public String code;
    public String name;

    @Override
    public String toString() {
        return "LifecycleEventObject{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", subType='" + subType + '\'' +
                ", parentId='" + parentId + '\'' +
                ", parentType='" + parentType + '\'' +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

