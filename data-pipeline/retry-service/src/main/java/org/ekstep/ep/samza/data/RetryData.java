package org.ekstep.ep.samza.data;

import org.apache.samza.storage.kv.KeyValueStore;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;
import org.ekstep.ep.samza.util.Flag;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.HashMap;
import java.util.Map;

public class RetryData {

    private Telemetry telemetry;
    private KeyValueStore<String, Object> retryStore;
    private int retryBackoffBase;
    private static final int RETRY_BACKOFF_BASE_DEFAULT = 10;
    static Logger LOGGER = new Logger(RetryData.class);
    private int maxAttempts;
    private Boolean enableMaxAttempts;
    private boolean maxAttemptReached = false;
    private Flag flag;
    private String metadataKey;

    public RetryData(Telemetry telemetry, KeyValueStore<String, Object> retryStore, int retryBackoffBase, Flag flag) {
        this(telemetry,retryStore,retryBackoffBase,0,false,flag);
    }

    public RetryData(Telemetry telemetry, KeyValueStore<String, Object> retryStore, int retryBackoffBase, int maxAttempts, Boolean enableMaxAttempts, Flag flag) {
        this.maxAttempts = maxAttempts;
        this.enableMaxAttempts = enableMaxAttempts;
        if (retryBackoffBase == 0)
            retryBackoffBase = RETRY_BACKOFF_BASE_DEFAULT;
        this.telemetry = telemetry;
        this.retryStore = retryStore;
        this.retryBackoffBase = retryBackoffBase;
        this.flag = flag;
    }

    public void setMetadataKey(String metadataKey){
        this.metadataKey = metadataKey;
    }

    public void addMetadata(DateTime currentTime) {
        Map<String, Object> metadata = getMetadata();
        if (metadata != null) {
            setLastProcessedAt(currentTime);
            if (metadata.get(flag.processedCount()) == null)
                setLastProcessedCount(1);
            else {
                Integer count = (((Double) Double.parseDouble(String.valueOf(metadata.get(flag.processedCount())))).intValue());
                count = count + 1;
                setLastProcessedCount(count);
            }
        } else {
            setLastProcessedAt(currentTime);
            setLastProcessedCount(1);
        }
        LOGGER.info(this.telemetry.id(), "METADATA - ADDED " + metadata);
    }

    public void addLastSkippedAt(DateTime currentTime) {
        telemetry.addFieldIfAbsent(flag.metadata(),new HashMap<String,Object>());
        String lastSkippedAt = String.format("%s.%s", flag.metadata(), flag.lastSkippedAt());
        telemetry.add(lastSkippedAt, currentTime.toString());
        LOGGER.info(telemetry.id(), "METADATA LAST SKIPPED AT - ADDED " + telemetry.<String>read(flag.metadata()));
    }

    public boolean shouldBackOff() {
        Integer processedCount = getProcessedCount();
        if(enableMaxAttempts && processedCount != null && maxAttempts < processedCount) {
            maxAttemptReached = true;
            return false;

        }
        LOGGER.info(telemetry.id(), "CHECK - AT " + DateTime.now());
        DateTime nextProcessingTime = getNextProcessingTime(getLastProcessedTime());
        if (nextProcessingTime == null || nextProcessingTime.isBeforeNow()) {
            LOGGER.info(telemetry.id(), "CHECK - PROCESSING " + telemetry.getMap());
            return false;
        } else {
            LOGGER.info(telemetry.id(), "CHECK - BACKING OFF " + telemetry.getMap());
            addMetadataToStore();
            return true;
        }
    }

    public void removeMetadataFromStore() {
        LOGGER.info(this.telemetry.id(), "METADATA - DELETING. KEY: " + metadataKey);
        retryStore.delete(metadataKey);
    }

    public void updateMetadataToStore() {
        NullableValue<Map<String, Object>> metadata = telemetry.read(flag.metadata());
        if (!metadata.isNull()) {
            Map _map = new HashMap();
            _map.put(flag.metadata(), metadata.value());
            retryStore.put(metadataKey, _map);
            LOGGER.info(telemetry.id(), "STORE - UPDATED " + _map + " UID " + metadataKey);
        }
    }

    private void addMetadataToStore() {
        if (retryStore.get(metadataKey) == null) {
            updateMetadataToStore();
            LOGGER.info(telemetry.id(), "STORE - ADDED FOR " + metadataKey);
        }
    }

    private DateTime getNextProcessingTime(DateTime lastProcessedTime) {
        Integer nextBackoffInterval = getNextBackoffInterval();
        if (lastProcessedTime == null || nextBackoffInterval == null)
            return null;
        DateTime nextProcessingTime = lastProcessedTime.plusSeconds(nextBackoffInterval);
        LOGGER.info(telemetry.id(), "nextProcessingTime: " + nextProcessingTime.toString());
        return nextProcessingTime;
    }

    private Integer getNextBackoffInterval() {
        Integer processedCount = getProcessedCount();
        if (processedCount == null)
            return null;
        return retryBackoffBase * (int) Math.pow(2, processedCount);
    }

    private Integer getProcessedCount() {
        Map metadata = getMetadata();
        if (metadata == null) {
            return null;
        } else {
            Object processed_count_object = metadata.get(flag.processedCount());
            if(processed_count_object == null) return null;
            Integer processedCount = (Integer) processed_count_object;
            return processedCount;
        }
    }

    private void setLastProcessedAt(DateTime time) {
        Map<String, Object> metadata = getMetadata();
        if(metadata!= null)
            metadata.put(flag.lastProcessedAt(), time.toString());
    }

    private void setLastProcessedCount(int n) {
        Map<String, Object> metadata = getMetadata();
        if(metadata!= null)
            metadata.put(flag.processedCount(), n);
    }

    private DateTime getLastProcessedTime() {
        Map metadata = getMetadata();
        if(metadata == null)
            return null;
        String lastProcessedAt = (String) metadata.get(flag.lastProcessedAt());
        if (lastProcessedAt == null)
            return null;
        DateTimeFormatter formatter = ISODateTimeFormat.dateTime();
        DateTime dt = formatter.parseDateTime(lastProcessedAt);
        return dt;
    }

    private Map<String, Object> getMetadata() {
        if(metadataKey == null || metadataKey.isEmpty()) return null;
        Map retryData = (Map) retryStore.get(metadataKey);
        Telemetry telemetryData = retryData == null ? this.telemetry : new Telemetry(retryData);
        NullableValue<Map<String, Object>> metadata = telemetryData.read(flag.metadata());
        if (metadata.isNull()) {
            Map<String, Object> newMetadata = new HashMap<String, Object>();
            telemetryData.add(flag.metadata(), newMetadata);
            return newMetadata;
        }
        return metadata.value();
    }

    public Boolean maxAttemptReached(){
        return maxAttemptReached;
    }
}
