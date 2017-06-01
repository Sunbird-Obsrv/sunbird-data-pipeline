package org.ekstep.ep.samza.data;

import org.apache.samza.storage.kv.KeyValueStore;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;
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

    public RetryData(Telemetry telemetry, KeyValueStore<String, Object> retryStore, int retryBackoffBase) {
        if (retryBackoffBase == 0)
            retryBackoffBase = RETRY_BACKOFF_BASE_DEFAULT;
        this.telemetry = telemetry;
        this.retryStore = retryStore;
        this.retryBackoffBase = retryBackoffBase;
    }

    public void addMetadata(DateTime currentTime) {
        Map<String, Object> metadata = getMetadata();
        if (metadata != null) {
            setLastProcessedAt(currentTime);
            if (metadata.get("processed_count") == null)
                setLastProcessedCount(1);
            else {
                Integer count = (((Double) Double.parseDouble(String.valueOf(metadata.get("processed_count")))).intValue());
                count = count + 1;
                setLastProcessedCount(count);
            }
        } else {
            setLastProcessedAt(currentTime);
            setLastProcessedCount(1);
        }
        LOGGER.info(telemetry.id(), "METADATA - ADDED " + metadata);
    }

    public void addLastSkippedAt(DateTime currentTime) {
        NullableValue<Map<String, Object>> metadata = telemetry.read("metadata");
        if (metadata.isNull())
            telemetry.add("metadata", new HashMap<String, Object>());
        telemetry.add("metadata.last_skipped_at", currentTime.toString());
        LOGGER.info(telemetry.id(), "METADATA LAST SKIPPED AT - ADDED " + telemetry.<String>read("metadata"));
    }

    public boolean shouldBackOff() {
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
        retryStore.delete(telemetry.getUID());
    }

    public void updateMetadataToStore() {
        NullableValue<Map<String, Object>> metadata = telemetry.read("metadata");
        if (!metadata.isNull()) {
            Map _map = new HashMap();
            _map.put("metadata", metadata.value());
            retryStore.put(telemetry.getUID(), _map);
            LOGGER.info(telemetry.id(), "STORE - UPDATED " + _map + " UID " + telemetry.getUID());
        }
    }

    private void addMetadataToStore() {
        if (retryStore.get(telemetry.getUID()) == null) {
            updateMetadataToStore();
            LOGGER.info(telemetry.id(), "STORE - ADDED FOR " + telemetry.getUID());
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
            Integer processedCount = (Integer) metadata.get("processed_count");
            return processedCount;
        }
    }

    private void setLastProcessedAt(DateTime time) {
        Map<String, Object> metadata = getMetadata();
        metadata.put("last_processed_at", time.toString());
    }

    private void setLastProcessedCount(int n) {
        Map<String, Object> metadata = getMetadata();
        metadata.put("processed_count", n);
    }

    private DateTime getLastProcessedTime() {
        Map metadata = getMetadata();
        String lastProcessedAt = (String) metadata.get("last_processed_at");
        if (lastProcessedAt == null)
            return null;
        DateTimeFormatter formatter = ISODateTimeFormat.dateTime();
        DateTime dt = formatter.parseDateTime(lastProcessedAt);
        return dt;
    }

    private Map<String, Object> getMetadata() {
        String uid = telemetry.getUID();
        Map retryData = (Map) retryStore.get(uid);
        Telemetry telemetryData = retryData == null ? this.telemetry : new Telemetry(retryData);
        NullableValue<Map<String, Object>> metadata = telemetryData.read("metadata");
        if (metadata.isNull()) {
            Map<String, Object> newMetadata = new HashMap<String, Object>();
            telemetryData.add("metadata", newMetadata);
            return newMetadata;
        }
        return metadata.value();
    }

}
