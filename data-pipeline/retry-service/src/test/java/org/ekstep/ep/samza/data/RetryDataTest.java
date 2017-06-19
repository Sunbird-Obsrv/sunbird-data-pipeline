package org.ekstep.ep.samza.data;

import org.apache.samza.storage.kv.KeyValueStore;
import org.ekstep.ep.samza.reader.Telemetry;
import org.ekstep.ep.samza.util.Flag;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;

import static org.junit.Assert.*;

public class RetryDataTest {

    private final String PROCESSED_COUNT_FLAG = "test_processed_count";
    private final String LAST_PROCESSED_AT_FLAG = "test_last_processed_at";
    private KeyValueStore<String,Object> retryStoreMock;

    @Before
    public void Setup(){
        retryStoreMock = Mockito.mock(KeyValueStore.class);
    }

    @Test
    public void shouldNotBackOffWhenMaxLimitReached() {
        HashMap<String, Object> telemetry = new HashMap<String, Object>();
        HashMap<String, Object> metadata = new HashMap<String, Object>();
        telemetry.put("metadata", metadata);
        telemetry.put("uid","123");
        metadata.put("processed_count",4);

        RetryData retryData = new RetryData(new Telemetry(telemetry), retryStoreMock, 10, 3, true, new Flag("test"));
        retryData.setMetadataKey((String) telemetry.get("uid"));

        assertFalse(retryData.shouldBackOff());
    }

    @Test
    public void shouldBackOffWhenMaxLimitNotReachedAndNextProcessingInFuture() {
        HashMap<String, Object> telemetry = new HashMap<String, Object>();
        HashMap<String, Object> metadata = new HashMap<String, Object>();
        telemetry.put("metadata", metadata);
        telemetry.put("uid","123");
        metadata.put(PROCESSED_COUNT_FLAG,2);
        metadata.put(LAST_PROCESSED_AT_FLAG, DateTime.now().toString());

        RetryData retryData = new RetryData(new Telemetry(telemetry), retryStoreMock, 10, 3, true, new Flag("test"));
        retryData.setMetadataKey((String) telemetry.get("uid"));

        assertTrue(retryData.shouldBackOff());
    }

    @Test
    public void shouldNotBackOffWhenMaxLimitReachedAndNextProcessingInPast() {
        HashMap<String, Object> telemetry = new HashMap<String, Object>();
        HashMap<String, Object> metadata = new HashMap<String, Object>();
        telemetry.put("metadata", metadata);
        telemetry.put("uid","123");
        metadata.put(PROCESSED_COUNT_FLAG,3);
        metadata.put(LAST_PROCESSED_AT_FLAG, DateTime.now().minus(20).toString());

        RetryData retryData = new RetryData(new Telemetry(telemetry), retryStoreMock, 1, 3, true, new Flag("test"));
        retryData.setMetadataKey((String) telemetry.get("uid"));

        assertTrue(retryData.shouldBackOff());
    }

    @Test
    public void shouldBackOffWhenMaxLimitNotEnabledAndNextProcessingInFuture() {
        HashMap<String, Object> telemetry = new HashMap<String, Object>();
        HashMap<String, Object> metadata = new HashMap<String, Object>();
        telemetry.put("metadata", metadata);
        telemetry.put("uid","123");

        metadata.put(PROCESSED_COUNT_FLAG,4);
        metadata.put(LAST_PROCESSED_AT_FLAG, DateTime.now().toString());

        RetryData retryData = new RetryData(new Telemetry(telemetry), retryStoreMock, 10, 3, false, new Flag("test"));
        retryData.setMetadataKey((String) telemetry.get("uid"));

        assertTrue(retryData.shouldBackOff());
    }


    @Test
    public void shouldNotBackOffWhenMaxLimitNotEnabledAndNextProcessingInPast() {
        HashMap<String, Object> telemetry = new HashMap<String, Object>();
        HashMap<String, Object> metadata = new HashMap<String, Object>();
        telemetry.put("metadata", metadata);
        telemetry.put("uid","123");

        metadata.put(PROCESSED_COUNT_FLAG,1);
        metadata.put(LAST_PROCESSED_AT_FLAG, DateTime.now().minus(25).toString());

        RetryData retryData = new RetryData(new Telemetry(telemetry), retryStoreMock, 10, 3, false, new Flag("test"));
        retryData.setMetadataKey((String) telemetry.get("uid"));

        assertTrue(retryData.shouldBackOff());
    }

}