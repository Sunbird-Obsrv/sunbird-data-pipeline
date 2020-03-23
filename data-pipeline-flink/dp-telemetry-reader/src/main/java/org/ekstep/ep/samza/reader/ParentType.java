package org.ekstep.ep.samza.reader;

public interface ParentType {
    <T> T readChild();

    void addChild(Object value);
}
