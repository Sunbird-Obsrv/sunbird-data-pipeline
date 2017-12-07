package org.ekstep.ep.samza.converter.exceptions;

import java.util.Map;

public class TelemetryConversionException extends Exception {
    private final Map<String, Object> sourceEvent;

    public TelemetryConversionException(String message, Map<String, Object> sourceEvent) {
        super(message);
        this.sourceEvent = sourceEvent;
    }

    public Map<String, Object> getSourceEvent() {
        return sourceEvent;
    }
}
