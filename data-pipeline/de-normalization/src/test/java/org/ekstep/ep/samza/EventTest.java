package org.ekstep.ep.samza;

import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.BasicConfigurator;
import org.apache.samza.storage.kv.KeyValueIterator;
import org.apache.samza.storage.kv.KeyValueStore;
import org.ekstep.ep.samza.external.UserService;
import org.ekstep.ep.samza.external.UserServiceClient;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

public class EventTest {

    public static final String UID = "1234321";
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String TS = "2008-06-16T00:00:00 +0530";
    private KeyValueStore keyValueStoreMock;
    private HashMap<String, Object> map;
    private UserService userServiceMock;
    private int retryBackoffBase;
    private int retryBackoffLimit;
    private KeyValueStore<String, Object> retryStore;
    private List<String> backendEvents;

    public List<String> getBackendEvents() {
        return new ArrayList<String>(
                Arrays.asList("BE_.*", "CE_.*", "CP_.*"));
    }

    class KVStore implements KeyValueStore{
        private Map data = new HashMap();
        @Override
        public Object get(Object key) {
            return data.get(key);
        }

        @Override
        public Map getAll(List list) {
            HashMap<Object, Object> map = new HashMap<Object, Object>();
            for (Object key : list) {
                map.put(key, data.get(key));
            }
            return map;
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
        public void deleteAll(java.util.List list) {
            for (Object key : list) {
                data.remove(key);
            }
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
    public void setUp() {
        map = new HashMap<String, Object>();
        map.put("uid", UID);
        keyValueStoreMock = mock(KeyValueStore.class);
        userServiceMock = mock(UserServiceClient.class);
        backendEvents = getBackendEvents();
        retryStore = new KVStore();
        retryBackoffBase = 10;
        BasicConfigurator.configure();
    }

    @Test
    public void ShouldNotTryToInitializeEventIfNoUidIsPresent() {
        Event event = new Event(new HashMap<String, Object>(),keyValueStoreMock, backendEvents,retryBackoffBase, retryStore);

        event.initialize();

        verify(keyValueStoreMock,never()).get(any());
        verifyZeroInteractions(keyValueStoreMock);
    }

    @Test
    public void ShouldNotTryToInitializeEventIfEventDoesNotTime() {
        map.put("uid", UID);

        Event event = new Event(map, keyValueStoreMock, backendEvents,retryBackoffBase, retryStore);

        event.initialize();

        verify(keyValueStoreMock,never()).get(any());
        verifyZeroInteractions(keyValueStoreMock);
    }

    @Test
    public void ShouldNotTryToInitializeChildIfEventDoesNotHaveValidTime() {
        map.put("ts", "invalid-time");
        map.put("uid", UID);

        Event event = new Event(map, keyValueStoreMock, backendEvents,retryBackoffBase, retryStore);

        event.initialize();

        verify(keyValueStoreMock,never()).get(any());
        verifyZeroInteractions(keyValueStoreMock);
    }

    @Test
    public void ShouldTryToInitializeChildForAUidAndValidTimeStamp() {
        map.put("ts", TS);
        map.put("uid", UID);

        Event event = new Event(map, keyValueStoreMock, backendEvents,retryBackoffBase, retryStore);

        event.initialize();

        verify(keyValueStoreMock).get(UID);
    }

    @Test
    public void ShouldNotTryToProcessEventWhenEventDoesNotHaveUid() throws IOException {
        Event event = new Event(new HashMap<String, Object>(),keyValueStoreMock, backendEvents,retryBackoffBase, retryStore);

        event.initialize();
        event.process(userServiceMock, DateTime.now());

        Mockito.verifyZeroInteractions(userServiceMock);
    }

    @Test
    public void ShouldNotTryToProcessEventIfItDoesNotTime() {
        map.put("uid", UID);

        Event event = new Event(map, keyValueStoreMock, backendEvents,retryBackoffBase, retryStore);

        event.initialize();
        event.process(userServiceMock, DateTime.now());

        Mockito.verifyZeroInteractions(userServiceMock);
    }

    @Test
    public void ShouldNotTryToProcessEventIfEventDoesNotHaveValidTime() {
        map.put("ts", "invalid-time");
        map.put("uid", UID);

        Event event = new Event(map, keyValueStoreMock, backendEvents,retryBackoffBase, retryStore);

        event.initialize();
        event.process(userServiceMock, DateTime.now());

        Mockito.verifyZeroInteractions(userServiceMock);
    }

    @Test
    public void ShouldNotTryToProcessChildIfChildIsProcessed() {
        HashMap<String, Boolean> flags = new HashMap<String, Boolean>();
        flags.put("child_data_processed", true);
        map.put("ts", TS);
        map.put("uid", UID);
        map.put("udata", getUdata());
        map.put("flags", flags);

        Event event = new Event(map, keyValueStoreMock, backendEvents,retryBackoffBase, retryStore);

        event.initialize();
        event.process(userServiceMock, DateTime.now());

        Mockito.verifyZeroInteractions(userServiceMock);
    }

    @Test
    public void ShouldNotTryToProcessChildIfChildIsCached() {
        map.put("ts", TS);
        map.put("uid", UID);
        Child child = new Child(UID, true, getUdata());

        Mockito.stub(keyValueStoreMock.get(UID)).toReturn(child);

        Event event = new Event(map, keyValueStoreMock, backendEvents,retryBackoffBase, retryStore);

        event.initialize();
        event.process(userServiceMock, DateTime.now());

        Mockito.verifyZeroInteractions(userServiceMock);
    }

    @Test
    public void ShouldProcessChildIfChildIsNotProcessed() throws IOException {
        Date date = new Date();
        map.put("ts", new SimpleDateFormat(DATE_FORMAT).format(date));
        map.put("uid", UID);
        Child child = new Child(UID, true,  getUdata());
        stub(userServiceMock.getUserFor(any(Child.class), any(Date.class), any(String.class))).toReturn(child);

        Event event = new Event(map, keyValueStoreMock, backendEvents,retryBackoffBase, retryStore);

        event.initialize();
        event.process(userServiceMock, new DateTime(date));

        verify(userServiceMock).getUserFor(argThat(validateChild(UID)), argThat(dateMatcher(date)), any(String.class));
    }

    @Test
    public void ShouldGetRightDataWhenAlreadyInitializedWithRightData() {
        HashMap<String, Boolean> flags = new HashMap<String, Boolean>();
        flags.put("child_data_processed", true);
        map.put("ts", TS);
        map.put("uid", UID);
        map.put("udata", getUdata());
        map.put("flags", flags);

        Event event = new Event(map, keyValueStoreMock, backendEvents,retryBackoffBase, retryStore);

        event.initialize();
        event.process(userServiceMock, DateTime.now());
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

        Event event = new Event(map, keyValueStoreMock, backendEvents,retryBackoffBase, retryStore);

        event.initialize();
        event.process(userServiceMock, DateTime.now());
        Map<String, Object> data = event.getData();

        HashMap<String, Object> actualUdata = (HashMap<String, Object>)data.get("udata");
        assertEquals(UID, data.get("uid"));
        validateUdata(actualUdata);
    }

    @Test
    public void ShouldGetRightDataWhenReadFromDb() throws IOException {
        map.put("ts", TS);
        map.put("uid", UID);

        Child child = new Child(UID, true,  getUdata());
        stub(userServiceMock.getUserFor(any(Child.class), any(Date.class), any(String.class))).toReturn(child);

        Event event = new Event(map, keyValueStoreMock, backendEvents,retryBackoffBase, retryStore);

        event.initialize();
        event.process(userServiceMock, DateTime.now());
        Map<String, Object> data = event.getData();

        HashMap<String, Object> actualUdata = (HashMap<String, Object>)data.get("udata");
        assertEquals(UID, data.get("uid"));
        validateUdata(actualUdata);
    }

    @Test
    public void ShouldNotUpdateEventIfChildIsNotProcessed() throws IOException {
        map.put("ts", TS);
        map.put("uid", UID);

        Child child = new Child(UID, false, getUdata());
        stub(userServiceMock.getUserFor(any(Child.class), any(Date.class), any(String.class))).toReturn(child);

        Event event = new Event(map, keyValueStoreMock, backendEvents,retryBackoffBase, retryStore);

        event.initialize();
        event.process(userServiceMock, DateTime.now());
        Map<String, Object> data = event.getData();

        HashMap<String, Object> actualUdata = (HashMap<String, Object>)data.get("udata");
        assertEquals(UID, data.get("uid"));
        assertEquals(null, actualUdata);
    }

    @Test
    public void ShouldBeAbleToIndicateIfNotAbleToConnectToDatabase() throws IOException {
        map.put("ts", TS);
        map.put("uid", UID);

        stub(userServiceMock.getUserFor(any(Child.class), any(Date.class), any(String.class))).toThrow(new IOException("Not able to connect to database"));

        Event event = new Event(map, keyValueStoreMock, backendEvents,retryBackoffBase, retryStore);

        event.initialize();
        event.process(userServiceMock, DateTime.now());
        Map<String, Object> data = event.getData();

        HashMap<String, Object> actualUdata = (HashMap<String, Object>)data.get("udata");
        assertEquals(UID, data.get("uid"));
        assertEquals(null, actualUdata);
        assertTrue(event.hadIssueWithDb());
    }

    @Test
    public void ShouldNotBeProcessedIfItDoesNotHaveChildData() {
        map.put("uid", UID);

        Event event = new Event(map, keyValueStoreMock, backendEvents,retryBackoffBase, retryStore);

        event.initialize();
        event.process(userServiceMock, DateTime.now());

        assertFalse(event.isProcessed());
    }

    @Test
    public void ShouldBeAbleToProcessIfFlagsDoesNotHaveChildProcessedFlag() throws IOException {
        HashMap<String, Boolean> flags = new HashMap<String, Boolean>();
        map.put("ts", TS);
        map.put("uid", UID);
        map.put("udata", getUdata());
        map.put("flags", flags);

        Child child = new Child(UID, true, getUdata());
        stub(userServiceMock.getUserFor(any(Child.class), any(Date.class), any(String.class))).toReturn(child);
        Event event = new Event(map, keyValueStoreMock, backendEvents,retryBackoffBase, retryStore);

        event.initialize();
        event.process(userServiceMock, DateTime.now());

       assertTrue(event.isProcessed());
    }

    @Test
    public void ShouldCreateProcessedCountFirstTime() throws IOException {
        map.put("ts", TS);
        map.put("uid", UID);

        Event event = new Event(map, keyValueStoreMock, backendEvents,retryBackoffBase, retryStore);

        event.initialize();
        event.process(null,DateTime.now());

        Map<String,Object> metadata = (Map<String, Object>) event.getData().get("metadata");
        assertTrue(metadata.containsKey("processed_count"));
        assertEquals(1, metadata.get("processed_count"));
    }

    @Test
    public void ShouldIncrementProcessedTime() throws IOException {

        HashMap<String, Object> metadata = new HashMap<String, Object>();
        metadata.put("processed_count",1);
        map.put("ts", TS);
        map.put("uid", UID);
        map.put("metadata", metadata);

        Event event = new Event(map, keyValueStoreMock, backendEvents,retryBackoffBase, retryStore);

        event.initialize();
        event.process(null,DateTime.now());

        Map<String,Object> meta = (Map<String, Object>) event.getData().get("metadata");
        assertEquals(2, metadata.get("processed_count"));
    }

    @Test
    public void ShouldNotBeSkippedFirstTime(){
        Event event = new Event(map, keyValueStoreMock, backendEvents,retryBackoffBase, retryStore);
        event.initialize();
        assertEquals(event.shouldBackoff(),false);
    }

//    @Test
//    public void ShouldRetryAppropriately(){
//        Event event = new Event(map, keyValueStoreMock, backendEvents);
//        event.initialize(retryBackoffBase, retryStore);
//        Assert.assertEquals(false, event.shouldBackOff());
//        DateTime now = new DateTime();
//        event.setLastProcessedAt(now);
//        event.setLastProcessedCount(1);
//        Assert.assertEquals(true, event.shouldBackOff());
//        event.setLastProcessedAt(now.minusSeconds(retryBackoffBase*2+1));
//        event.setLastProcessedCount(1);
//        Assert.assertEquals(false, event.shouldBackOff());
//        event.setLastProcessedAt(now.minusSeconds(21));
//        event.setLastProcessedCount(2);
//        Assert.assertEquals(true, event.shouldBackOff());
//        event.setLastProcessedAt(now.minusSeconds(41));
//        event.setLastProcessedCount(2);
//        Assert.assertEquals(false, event.shouldBackOff());
//    }

    @Test
    public void ShouldBackoffForAllEventsRelevant() throws IOException {
        DateTimeUtils.setCurrentMillisFixed(0); //big bang
        retryBackoffBase = 10;

        Date date = new Date();
        map.put("ts", new SimpleDateFormat(DATE_FORMAT).format(date));
        map.put("uid", UID);
        Event event = new Event(map,keyValueStoreMock, backendEvents,retryBackoffBase, retryStore);
        event.initialize();
        Child child = new Child(UID, false,  getUdata());
        stub(userServiceMock.getUserFor(any(Child.class), any(Date.class), any(String.class))).toReturn(child);

        Map map2 = new HashedMap();
        map2.put("ts", new SimpleDateFormat(DATE_FORMAT).format(date));
        map2.put("uid", UID);

        Assert.assertNull(retryStore.get(UID));

        Event event2 = new Event(map2, keyValueStoreMock, backendEvents,retryBackoffBase, retryStore);
        event2.initialize();
//      ----- first event
        Assert.assertEquals(false, event.shouldBackoff()); //first try for first event is not skipped
        event.process(userServiceMock, DateTime.now());
        Assert.assertNotNull(retryStore.get(UID));

//      ----- second event
        Assert.assertEquals(true, event2.shouldBackoff()); //first try for second event is skipped
        Assert.assertNotNull(retryStore.get(UID));
//      ----- events again
        DateTimeUtils.setCurrentMillisFixed(5 * 1000);
        Assert.assertEquals(true, event.shouldBackoff()); //first backoff
        Assert.assertEquals(true, event2.shouldBackoff()); //first backoff
        Assert.assertNotNull(retryStore.get(UID));
//      ----- events again
        DateTimeUtils.setCurrentMillisFixed(21 * 1000); //10*2^1
        Assert.assertEquals(false, event.shouldBackoff()); //backoff over
        event.process(userServiceMock, DateTime.now()); //data not available
        Assert.assertEquals(true, event2.shouldBackoff()); //backoff back
        Assert.assertNotNull(retryStore.get(UID));
//      ----- events again
        DateTimeUtils.setCurrentMillisFixed(31 * 1000);
        Assert.assertEquals(true, event.shouldBackoff()); //backoff
        Assert.assertEquals(true, event2.shouldBackoff()); //backoff
        Assert.assertNotNull(retryStore.get(UID));
//      ----- events again
        DateTimeUtils.setCurrentMillisFixed((21 + 40 + 1) * 1000); //10*2^2
        Assert.assertEquals(false, event.shouldBackoff()); //backoff over
        event.process(userServiceMock, DateTime.now()); //data not available
        Assert.assertEquals(true, event2.shouldBackoff()); //backoff back
        Assert.assertNotNull(retryStore.get(UID));
//      ----- events again but this time they get processed
        child.setAsProcessed();
        DateTimeUtils.setCurrentMillisFixed((21 + 40 + 1 + 80 + 1) * 1000); //10*2^2
        Assert.assertEquals(false, event.shouldBackoff()); //backoff over
        event.process(userServiceMock, DateTime.now()); //data not available
        Assert.assertNull(retryStore.get(UID));
        Assert.assertEquals(false, event2.shouldBackoff()); //event not skipped
        event2.process(userServiceMock, DateTime.now());

        Assert.assertEquals(4, ((Map) (map.get("metadata"))).get("processed_count"));
        Assert.assertEquals(DateTime.now().toString(),((Map)(map.get("metadata"))).get("last_processed_at"));
        Assert.assertEquals(1, ((Map) (map2.get("metadata"))).get("processed_count"));
        Assert.assertEquals(DateTime.now().toString(), ((Map) (map2.get("metadata"))).get("last_processed_at"));

        Assert.assertNull(retryStore.get(UID));
    }

    private void validateUdata(HashMap<String, Object> actualUdata) {
        HashMap<String, Object> expectedUdata = getUdata();

        assertEquals(expectedUdata.get("age_completed_years"),actualUdata.get("age_completed_years"));
        assertEquals(expectedUdata.get("gender"), actualUdata.get("gender"));
        assertEquals(expectedUdata.get("board"), actualUdata.get("board"));
        assertEquals(expectedUdata.get("medium"), actualUdata.get("medium"));
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
        udata.put("medium", "kannada");
        udata.put("board", "SSLC");
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
