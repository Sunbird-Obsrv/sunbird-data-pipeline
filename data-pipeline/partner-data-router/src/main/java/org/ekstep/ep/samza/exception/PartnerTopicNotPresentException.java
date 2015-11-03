package org.ekstep.ep.samza.exception;

public class PartnerTopicNotPresentException extends RuntimeException{
    public PartnerTopicNotPresentException(String message) {
        super(message);
    }
}
