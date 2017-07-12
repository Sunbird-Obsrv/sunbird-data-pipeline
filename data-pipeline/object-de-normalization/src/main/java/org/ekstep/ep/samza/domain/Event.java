package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.config.ObjectDeNormalizationConfig;
import org.ekstep.ep.samza.data.RetryData;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;
import org.ekstep.ep.samza.task.ObjectDeNormalizationSink;
import org.ekstep.ep.samza.util.Flag;
import org.joda.time.DateTime;
import java.util.HashMap;
import java.util.Map;

public class Event {
    static Logger LOGGER = new Logger(Event.class);
    private final Telemetry telemetry;
    private final RetryData retryData;
    private Boolean processed = false;
    private Boolean triedProcessing = false;

    public Event(Telemetry telemetry, ObjectDeNormalizationConfig config) {
        this.telemetry = telemetry;
        retryData = new RetryData(telemetry, config.retryStore(), config.retryBackoffBase(),config.retryBackoffLimit(),config.retryBackoffLimitEnable(), new Flag("od"));
    }

    public String id() {
        NullableValue<String> checksum = telemetry.read("metadata.checksum");
        return checksum.value();
    }

    public String eid() {
        NullableValue<String> checksum = telemetry.read("eid");
        return checksum.value();
    }

    @Override
    public String toString() {
        return "Event{" +
                "telemetry=" + telemetry +
                '}';
    }

    public Map<String, Object> map() {
        return telemetry.getMap();
    }

    public <T> NullableValue<T> read(String path) {
        return telemetry.read(path);
    }

    public void update(String path, HashMap<String, Object> data) {
        telemetry.add(path, data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        return telemetry != null ? telemetry.equals(event.telemetry) : event.telemetry == null;
    }

    @Override
    public int hashCode() {
        return telemetry != null ? telemetry.hashCode() : 0;
    }

    public void markProcessed() {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.od_processed", true);
        retryData.removeMetadataFromStore();
        updateMetadata(true);
    }

    public void markRetry(Object err, Object errmsg) {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.od_retry", true);
        telemetry.addFieldIfAbsent("metadata", new HashMap<String, Object>());
        telemetry.add("metadata.od_err", err);
        telemetry.add("metadata.od_errmsg", errmsg);
        retryData.updateMetadataToStore();
        updateMetadata(false);
    }

    private void updateMetadata(boolean processed) {
        if(triedProcessing)
            this.processed &=  processed;
        else
            this.processed = processed;
        triedProcessing = true;
        retryData.addMetadata(DateTime.now());
    }

    public boolean shouldBackOff() {
        return retryData.shouldBackOff();
    }

    public void setDeNormalizationId(String deNormalizationId, String channel){
        String metadata_key = String.valueOf(deNormalizationId+"_"+channel);
        retryData.setMetadataKey(metadata_key);
    }

    public void addLastSkippedAt(DateTime now) {
        retryData.addLastSkippedAt(now);
    }

    public void flowIn(ObjectDeNormalizationSink sink) {
        if(!triedProcessing){
            LOGGER.info(id(), "EVENT NOT PROCESSED, PASSING THROUGH");
            markSkipped();
            sink.toSuccessTopic(this);
            return;
        }
        LOGGER.info(id(), "PASSING EVENT THROUGH");

        boolean shouldAddInRetry = !retryData.maxAttemptReached() && !processed;
        boolean failedToProcess = retryData.maxAttemptReached() && !processed;

        if(shouldAddInRetry){
            LOGGER.info(id(),"ADDING in retry");
            sink.toRetryTopic(this);

        }
        else{
            LOGGER.info(id(),"ADDING in success");
            sink.toSuccessTopic(this);
        }

        if (failedToProcess){
            LOGGER.info(id(),"ADDING in failed");
            sink.toFailedTopic(this);
        }

    }

    private void markSkipped() {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.od_skipped", true);
    }

    public String channel() {
        return telemetry.<String>read("channel").value();
    }
}

