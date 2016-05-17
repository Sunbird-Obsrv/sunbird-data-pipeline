package org.ekstep.ep.samza;

import org.apache.commons.collections.map.HashedMap;
import org.apache.samza.storage.kv.KeyValueIterator;
import org.apache.samza.storage.kv.KeyValueStore;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class EventTest {

    public static final String UID = "1234321";
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String TS = "2008-06-16T00:00:00 +0530";
    private KeyValueStore keyValueStoreMock;
    private HashMap<String, Object> map;
    private ChildDto childDtoMock;
    private int retryBackoffBase;
    private int retryBackoffLimit;
    private KeyValueStore<String, Object> retryStore;

    class KVStore implements KeyValueStore{
        private Map data = new HashMap();
        @Override
        public Object get(Object key) {
            return data.get(key);
        }

        @Override
        public void put(Object key, Object value) {
            data.put(key,value);
        }

        @Override
        public void delete(Object key) {
            data.remove(key);
        }

        @Override
        public KeyValueIterator range(Object from, Object to) {
            return null;
        }

        @Override
        public KeyValueIterator all() {
            return null;
        }

        @Override
        public void close() {

        }

        @Override
        public void flush() {
            map.clear();
        }

        @Override
        public void putAll(List list) {

        }
    }

    @Before
    public void setUp(){
        map = new HashMap<String, Object>();
        map.put("uid",UID);
        keyValueStoreMock = mock(KeyValueStore.class);
        childDtoMock = mock(ChildDto.class);
        retryStore = new KVStore();
        retryBackoffBase = 10;
    }

    @Test
    public void ShouldNotTryToInitializeEventIfNoUidIsPresent() {
        Event event = new Event(new HashMap<String, Object>(),keyValueStoreMock);

        event.initialize(retryBackoffBase, retryBackoffLimit, retryStore);

        verify(keyValueStoreMock,never()).get(any());
        verifyZeroInteractions(keyValueStoreMock);
    }

    @Test
    public void ShouldNotTryToInitializeEventIfEventDoesNotTime() {
        map.put("uid", UID);

        Event event = new Event(map, keyValueStoreMock);

        event.initialize(retryBackoffBase, retryBackoffLimit, retryStore);

        verify(keyValueStoreMock,never()).get(any());
        verifyZeroInteractions(keyValueStoreMock);
    }

    @Test
    public void ShouldNotTryToInitializeChildIfEventDoesNotHaveValidTime() {
        map.put("ts", "invalid-time");
        map.put("uid", UID);

        Event event = new Event(map, keyValueStoreMock);

        event.initialize(retryBackoffBase, retryBackoffLimit, retryStore);

        verify(keyValueStoreMock,never()).get(any());
        verifyZeroInteractions(keyValueStoreMock);
    }

    @Test
    public void ShouldTryToInitializeChildForAUidAndValidTimeStamp() {
        map.put("ts", TS);
        map.put("uid", UID);

        Event event = new Event(map, keyValueStoreMock);

        event.initialize(retryBackoffBase, retryBackoffLimit, retryStore);

        verify(keyValueStoreMock).get(UID);
    }

    @Test
    public void ShouldNotTryToProcessEventWhenEventDoesNotHaveUid() throws SQLException {
        Event event = new Event(new HashMap<String, Object>(),keyValueStoreMock);

        event.initialize(retryBackoffBase, retryBackoffLimit, retryStore);
        event.process(childDtoMock, DateTime.now());

        Mockito.verifyZeroInteractions(childDtoMock);
    }

    @Test
    public void ShouldNotTryToProcessEventIfItDoesNotTime() {
        map.put("uid", UID);

        Event event = new Event(map, keyValueStoreMock);

        event.initialize(retryBackoffBase, retryBackoffLimit, retryStore);
        event.process(childDtoMock, DateTime.now());

        Mockito.verifyZeroInteractions(childDtoMock);
    }

    @Test
    public void ShouldNotTryToProcessEventIfEventDoesNotHaveValidTime() {
        map.put("ts", "invalid-time");
        map.put("uid", UID);

        Event event = new Event(map, keyValueStoreMock);

        event.initialize(retryBackoffBase, retryBackoffLimit, retryStore);
        event.process(childDtoMock, DateTime.now());

        Mockito.verifyZeroInteractions(childDtoMock);
    }

    @Test
    public void ShouldNotTryToProcessChildIfChildIsProcessed() {
        HashMap<String, Boolean> flags = new HashMap<String, Boolean>();
        flags.put("child_data_processed", true);
        map.put("ts", TS);
        map.put("uid", UID);
        map.put("udata", getUdata());
        map.put("flags", flags);

        Event event = new Event(map, keyValueStoreMock);

        event.initialize(retryBackoffBase, retryBackoffLimit, retryStore);
        event.process(childDtoMock, DateTime.now());

        Mockito.verifyZeroInteractions(childDtoMock);
    }

    @Test
    public void ShouldNotTryToProcessChildIfChildIsCached() {
        map.put("ts", TS);
        map.put("uid", UID);
        Child child = new Child(UID, true, getUdata());

        Mockito.stub(keyValueStoreMock.get(UID)).toReturn(child);

        Event event = new Event(map, keyValueStoreMock);

        event.initialize(retryBackoffBase, retryBackoffLimit, retryStore);
        event.process(childDtoMock, DateTime.now());

        Mockito.verifyZeroInteractions(childDtoMock);
    }

    @Test
    public void ShouldProcessChildIfChildIsNotProcessed() throws SQLException {
        Date date = new Date();
        map.put("ts", new SimpleDateFormat(DATE_FORMAT).format(date));
        map.put("uid", UID);
        Child child = new Child(UID, true,  getUdata());
        stub(childDtoMock.process(any(Child.class), any(Date.class))).toReturn(child);

        Event event = new Event(map, keyValueStoreMock);

        event.initialize(retryBackoffBase, retryBackoffLimit, retryStore);
        event.process(childDtoMock, DateTime.now());

        verify(childDtoMock).process(argThat(validateChild(UID)), argThat(dateMatcher(date)));
    }

    @Test
    public void ShouldGetRightDataWhenAlreadyInitializedWithRightData() {
        HashMap<String, Boolean> flags = new HashMap<String, Boolean>();
        flags.put("child_data_processed", true);
        map.put("ts", TS);
        map.put("uid", UID);
        map.put("udata", getUdata());
        map.put("flags", flags);

        Event event = new Event(map, keyValueStoreMock);

        event.initialize(retryBackoffBase, retryBackoffLimit, retryStore);
        event.process(childDtoMock, DateTime.now());
        Map<String, Object> data = event.getData();

        HashMap<String, Object> actualUdata = (HashMap<String, Object>)data.get("udata");
        assertEquals(UID, data.get("uid"));
        validateUdata(actualUdata);
    }

    @Test
    public void ShouldGetRightDataWhenCached() {
        map.put("ts", TS);
        map.put("uid", UID);
        Child child = new Child(UID, true,  getUdata());

        Mockito.stub(keyValueStoreMock.get(UID)).toReturn(child);

        Event event = new Event(map, keyValueStoreMock);

        event.initialize(retryBackoffBase, retryBackoffLimit, retryStore);
        event.process(childDtoMock, DateTime.now());
        Map<String, Object> data = event.getData();

        HashMap<String, Object> actualUdata = (HashMap<String, Object>)data.get("udata");
        assertEquals(UID, data.get("uid"));
        validateUdata(actualUdata);
    }

    @Test
    public void ShouldGetRightDataWhenReadFromDb() throws SQLException {
        map.put("ts", TS);
        map.put("uid", UID);

        Child child = new Child(UID, true,  getUdata());
        stub(childDtoMock.process(any(Child.class), any(Date.class))).toReturn(child);

        Event event = new Event(map, keyValueStoreMock);

        event.initialize(retryBackoffBase, retryBackoffLimit, retryStore);
        event.process(childDtoMock, DateTime.now());
        Map<String, Object> data = event.getData();

        HashMap<String, Object> actualUdata = (HashMap<String, Object>)data.get("udata");
        assertEquals(UID, data.get("uid"));
        validateUdata(actualUdata);
    }

    @Test
    public void ShouldNotUpdateEventIfChildIsNotProcessed() throws SQLException {
        map.put("ts", TS);
        map.put("uid", UID);

        Child child = new Child(UID, false, getUdata());
        stub(childDtoMock.process(any(Child.class), any(Date.class))).toReturn(child);

        Event event = new Event(map, keyValueStoreMock);

        event.initialize(retryBackoffBase, retryBackoffLimit, retryStore);
        event.process(childDtoMock, DateTime.now());
        Map<String, Object> data = event.getData();

        HashMap<String, Object> actualUdata = (HashMap<String, Object>)data.get("udata");
        assertEquals(UID, data.get("uid"));
        assertEquals(null, actualUdata);
    }

    @Test
    public void ShouldBeAbleToIndicateIfNotAbleToConnectToDatabase() throws SQLException {
        map.put("ts", TS);
        map.put("uid", UID);

        stub(childDtoMock.process(any(Child.class), any(Date.class))).toThrow(new SQLException("Not able to connect to database"));

        Event event = new Event(map, keyValueStoreMock);

        event.initialize(retryBackoffBase, retryBackoffLimit, retryStore);
        event.process(childDtoMock, DateTime.now());
        Map<String, Object> data = event.getData();

        HashMap<String, Object> actualUdata = (HashMap<String, Object>)data.get("udata");
        assertEquals(UID, data.get("uid"));
        assertEquals(null, actualUdata);
        assertTrue(event.hadIssueWithDb());
    }

    @Test
    public void ShouldNotBeProcessedIfItDoesNotHaveChildData() {
        map.put("uid", UID);

        Event event = new Event(map, keyValueStoreMock);

        event.initialize(retryBackoffBase, retryBackoffLimit, retryStore);
        event.process(childDtoMock, DateTime.now());

        assertFalse(event.isProcessed());
    }

    @Test
    public void ShouldBeAbleToProcessIfFlagsDoesNotHaveChildProcessedFlag() throws SQLException {
        HashMap<String, Boolean> flags = new HashMap<String, Boolean>();
        map.put("ts", TS);
        map.put("uid", UID);
        map.put("udata", getUdata());
        map.put("flags", flags);

        Child child = new Child(UID, true, getUdata());
        stub(childDtoMock.process(any(Child.class), any(Date.class))).toReturn(child);
        Event event = new Event(map, keyValueStoreMock);

        event.initialize(retryBackoffBase, retryBackoffLimit, retryStore);
        event.process(childDtoMock, DateTime.now());

       assertTrue(event.isProcessed());
    }

    @Test
    public void ShouldCreateProcessedCountFirstTime() throws SQLException {
        map.put("ts", TS);
        map.put("uid", UID);

        Event event = new Event(map, keyValueStoreMock);

        event.initialize(retryBackoffBase, retryBackoffLimit, retryStore);
        event.addMetadata(DateTime.now());

        Map<String,Object> metadata = (Map<String, Object>) event.getMap().get("metadata");
        assertTrue(metadata.containsKey("processed_count"));
        assertEquals(1, metadata.get("processed_count"));
    }

    @Test
    public void ShouldIncrementProcessedTime() throws SQLException {

        HashMap<String, Object> metadata = new HashMap<String, Object>();
        metadata.put("processed_count",1);
        map.put("ts", TS);
        map.put("uid", UID);
        map.put("metadata", metadata);

        Event event = new Event(map, keyValueStoreMock);

        event.initialize(retryBackoffBase, retryBackoffLimit, retryStore);
        event.addMetadata(DateTime.now());

        Map<String,Object> meta = (Map<String, Object>) event.getMap().get("metadata");
        assertEquals(2, metadata.get("processed_count"));
    }

    @Test
    public void ShouldNotBeSkippedFirstTime(){
        Event event = new Event(map, keyValueStoreMock);
        event.initialize(retryBackoffBase,retryBackoffLimit, retryStore);
        assertEquals(event.isSkipped(),false);
    }

    @Test
    public void ShouldCalculateBackoffAppropriately(){
        Event event = new Event(map, keyValueStoreMock);
        event.initialize(retryBackoffBase,retryBackoffLimit, retryStore);
        event.setLastProcessedCount(1);
        DateTime now = new DateTime();
        event.setLastProcessedAt(now);
        List<DateTime> times = event.backoffTimes(20);
        DateTime a = null;
        int count=0;
        for(DateTime b:times){
            if(a!=null){
                long diff = b.getSecondOfMinute() - a.getSecondOfMinute();
                Assert.assertEquals((long)(10*Math.pow(2,count)),diff);
            }
        }
    }

    @Test
    public void ShouldRetryAppropriately(){
        Event event = new Event(map, keyValueStoreMock);
        event.initialize(retryBackoffBase,retryBackoffLimit, retryStore);
        Assert.assertEquals(false, event.isSkipped());
        DateTime now = new DateTime();
        event.setLastProcessedAt(now);
        event.setLastProcessedCount(1);
        Assert.assertEquals(true, event.isSkipped());
        event.setLastProcessedAt(now.minusSeconds(retryBackoffBase*2+1));
        event.setLastProcessedCount(1);
        Assert.assertEquals(false, event.isSkipped());
        event.setLastProcessedAt(now.minusSeconds(21));
        event.setLastProcessedCount(2);
        Assert.assertEquals(true, event.isSkipped());
        event.setLastProcessedAt(now.minusSeconds(41));
        event.setLastProcessedCount(2);
        Assert.assertEquals(false, event.isSkipped());
    }

    @Test
    public void ShouldBackoffForAllEventsRelevant() throws SQLException {
        DateTimeUtils.setCurrentMillisFixed(0); //big bang
        retryBackoffBase = 10;

        Date date = new Date();
        map.put("ts", new SimpleDateFormat(DATE_FORMAT).format(date));
        map.put("uid", UID);
        Event event = new Event(map,keyValueStoreMock);
        event.initialize(retryBackoffBase, retryBackoffLimit, retryStore);
        Child child = new Child(UID, false,  getUdata());
        stub(childDtoMock.process(any(Child.class), any(Date.class))).toReturn(child);

        Map map2 = new HashedMap();
        map2.put("ts", new SimpleDateFormat(DATE_FORMAT).format(date));
        map2.put("uid", UID);

        Event event2 = new Event(map2, keyValueStoreMock);
        event2.initialize(retryBackoffBase, retryBackoffLimit, retryStore);
//      ----- first event
        Assert.assertEquals(false, event.isSkipped()); //first try for first event is not skipped
        event.process(childDtoMock, DateTime.now());
//      ----- second event
        Assert.assertEquals(true, event2.isSkipped()); //first try for second event is skipped
//      ----- events again
        DateTimeUtils.setCurrentMillisFixed(5 * 1000);
        Assert.assertEquals(true, event.isSkipped()); //first backoff
        Assert.assertEquals(true, event2.isSkipped()); //first backoff
//      ----- events again
        DateTimeUtils.setCurrentMillisFixed(21 * 1000); //10*2^1
        Assert.assertEquals(false, event.isSkipped()); //backoff over
        event.process(childDtoMock, DateTime.now()); //data not available
        Assert.assertEquals(true, event2.isSkipped()); //backoff back
//      ----- events again
        DateTimeUtils.setCurrentMillisFixed(31 * 1000);
        Assert.assertEquals(true, event.isSkipped()); //backoff
        Assert.assertEquals(true, event2.isSkipped()); //backoff
//      ----- events again
        DateTimeUtils.setCurrentMillisFixed((21+40+1) * 1000); //10*2^2
        Assert.assertEquals(false, event.isSkipped()); //backoff over
        event.process(childDtoMock, DateTime.now()); //data not available
        Assert.assertEquals(true, event2.isSkipped()); //backoff back

        child.setAsProcessed();
        DateTimeUtils.setCurrentMillisFixed((21 + 40 + 1 + 80 +1) * 1000); //10*2^2
        Assert.assertEquals(false, event.isSkipped()); //backoff over
        event.process(childDtoMock, DateTime.now()); //data not available
        Assert.assertEquals(false, event2.isSkipped()); //event not skipped
    }


    private void validateUdata(HashMap<String, Object> actualUdata) {
        HashMap<String, Object> expectedUdata = getUdata();

        assertEquals(expectedUdata.get("age_completed_years"),actualUdata.get("age_completed_years"));
        assertEquals(expectedUdata.get("gender"), actualUdata.get("gender"));
        assertEquals(expectedUdata.get("handle"), actualUdata.get("handle"));
        assertEquals(expectedUdata.get("standard"),actualUdata.get("standard"));
        assertEquals(expectedUdata.get("is_group_user"),actualUdata.get("is_group_user"));
    }

    private ArgumentMatcher<Child> validateChild(final String uid) {
        return new ArgumentMatcher<Child>() {
            @Override
            public boolean matches(Object o) {
                Child child = (Child) o;
                assertEquals(uid, child.getUid());
                return true;
            }
        };
    }


    private HashMap<String, Object> getUdata() {
        HashMap<String, Object> udata = new HashMap<String, Object>();
        udata.put("age_completed_years", 7);
        udata.put("gender", "male");
        udata.put("handle", "user@twitter.com");
        udata.put("standard", 2);
        udata.put("is_group_user", true);
        return udata;
    }

    private ArgumentMatcher<Date> dateMatcher(final Date expectedDate) {
        return new ArgumentMatcher<Date>() {
            @Override
            public boolean matches(Object argument) {
                Date actualDate = (Date) argument;
                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                Assert.assertEquals(dateFormat.format(expectedDate),dateFormat.format(actualDate));
                return true;
            }
        };
    }


}