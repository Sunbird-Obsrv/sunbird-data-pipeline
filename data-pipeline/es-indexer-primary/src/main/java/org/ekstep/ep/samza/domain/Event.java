package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.reader.Telemetry;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class Event {
    private final Telemetry telemetry;

    public Event(Map<String, Object> message) {
        this.telemetry = new Telemetry(message);
    }

    public Map<String, Object> getMap() {
        return telemetry.getMap();
    }

    public String indexName() {
        return telemetry.<String>read("metadata.index_name").value();
    }

    public String indexType() {
        return telemetry.<String>read("metadata.index_type").value();
    }

    public String id() {
        return telemetry.<String>read("metadata.checksum").value();
    }

    public String getJson() {
        return telemetry.getJson();
    }

    public void markFailed(String status, String errorMsg) {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.es_indexer_processed", false);

        telemetry.addFieldIfAbsent("metadata", new HashMap<String, Object>());
        telemetry.add("metadata.es_indexer_status", status);
        telemetry.add("metadata.es_indexer_error", errorMsg);
    }

    public void markSkipped() {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.es_indexer_skipped", true);
    }

    public boolean can_be_indexed() {
        return (indexName() != null && indexType() != null);
    }
}
