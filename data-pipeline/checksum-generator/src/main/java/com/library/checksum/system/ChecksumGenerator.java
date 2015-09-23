package com.library.checksum.system;

import java.util.Map;

public class ChecksumGenerator {
    private Strategy strategy;

    public ChecksumGenerator(Strategy strategy){
        this.strategy = strategy;
    }

    public String generateChecksum(Map<String,Object> event, String []keys){
        return strategy.createChecksum(event,keys);
    }
}


