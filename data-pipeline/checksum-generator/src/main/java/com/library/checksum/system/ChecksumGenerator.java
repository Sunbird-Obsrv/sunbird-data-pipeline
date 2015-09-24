package com.library.checksum.system;

import java.util.Map;

public class ChecksumGenerator {
    private Strategy strategy;

    public ChecksumGenerator(Strategy strategy){
        this.strategy = strategy;
    }

    public Mappable stampChecksum(Mappable event){
        return strategy.generateChecksum(event);
    }
}


