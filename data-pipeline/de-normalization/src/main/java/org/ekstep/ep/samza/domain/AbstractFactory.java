package org.ekstep.ep.samza.domain;

public interface AbstractFactory<T> {
    public T getInstance(String type);
}

