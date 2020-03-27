package org.sunbird.dp.reader;

public interface ParentType {
    <T> T readChild();

    void addChild(Object value);
}
