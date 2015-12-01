package org.ekstep.ep.samza;

import org.apache.samza.storage.kv.KeyValueStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class EventTest {

    public static final String UID = "1234321";
    private KeyValueStore keyValueStoreMock;
    private HashMap<String, Object> map;
    private ChildDto childDtoMock;

    @Before
    public void setUp(){
        map = new HashMap<String, Object>();
        keyValueStoreMock = mock(KeyValueStore.class);
        childDtoMock = mock(ChildDto.class);
    }

    @Test
    public void ShouldNotTryToInitializeEventIfNoUidIsPresent() {
        Event event = new Event(new HashMap<String, Object>(),keyValueStoreMock);

        event.initialize();

        verify(keyValueStoreMock,never()).get(any());
        verifyZeroInteractions(keyValueStoreMock);
    }

    @Test
    public void ShouldNotTryToInitializeEventIfEventDoesNotTime() {
        map.put("uid", UID);

        Event event = new Event(map, keyValueStoreMock);

        event.initialize();

        verify(keyValueStoreMock,never()).get(any());
        verifyZeroInteractions(keyValueStoreMock);
    }

    @Test
    public void ShouldNotTryToInitializeChildIfEventDoesNotHaveValidTime() {
        map.put("ts", "invalid-time");
        map.put("uid", UID);

        Event event = new Event(map, keyValueStoreMock);

        event.initialize();

        verify(keyValueStoreMock,never()).get(any());
        verifyZeroInteractions(keyValueStoreMock);
    }

    @Test
    public void ShouldTryToInitializeChildForAUidAndValidTimeStamp() {
        map.put("ts", "2008-06-16T00:00:00 +0530");
        map.put("uid", UID);

        Event event = new Event(map, keyValueStoreMock);

        event.initialize();

        verify(keyValueStoreMock).get(UID);
    }

    @Test
    public void ShouldNotTryToProcessEventWhenEventDoesNotHaveUid() throws SQLException {
        Event event = new Event(new HashMap<String, Object>(),keyValueStoreMock);

        event.initialize();
        event.process(childDtoMock);

        Mockito.verifyZeroInteractions(childDtoMock);
    }

    @Test
    public void ShouldNotTryToProcessEventIfItDoesNotTime() {
        map.put("uid", UID);

        Event event = new Event(map, keyValueStoreMock);

        event.initialize();
        event.process(childDtoMock);

        Mockito.verifyZeroInteractions(childDtoMock);
    }

    @Test
    public void ShouldNotTryToProcessEventIfEventDoesNotHaveValidTime() {
        map.put("ts", "invalid-time");
        map.put("uid", UID);

        Event event = new Event(map, keyValueStoreMock);

        event.initialize();
        event.process(childDtoMock);

        Mockito.verifyZeroInteractions(childDtoMock);
    }

    @Test
    public void ShouldNotTryToProcessChildIfChildIsProcessed() {
        HashMap<String, Boolean> flags = new HashMap<String, Boolean>();
        flags.put("child_data_processed", true);
        map.put("ts", "2008-06-16T00:00:00 +0530");
        map.put("uid", UID);
        map.put("udata", getUdata());
        map.put("flags", flags);

        Event event = new Event(map, keyValueStoreMock);

        event.initialize();
        event.process(childDtoMock);

        Mockito.verifyZeroInteractions(childDtoMock);
    }

    @Test
    public void ShouldNotTryToProcessChildIfChildIsCached() {
        map.put("ts", "2008-06-16T00:00:00 +0530");
        map.put("uid", UID);
        Child child = new Child(UID, true, getUdata());

        Mockito.stub(keyValueStoreMock.get(UID)).toReturn(child);

        Event event = new Event(map, keyValueStoreMock);

        event.initialize();
        event.process(childDtoMock);

        Mockito.verifyZeroInteractions(childDtoMock);
    }

    @Test
    public void ShouldProcessChildIfChildIsNotProcessed() throws SQLException {
        Date date = new Date();
        map.put("ts", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(date));
        map.put("uid", UID);
        Child child = new Child(UID, true,  getUdata());
        stub(childDtoMock.process(any(Child.class), any(Date.class))).toReturn(child);

        Event event = new Event(map, keyValueStoreMock);

        event.initialize();
        event.process(childDtoMock);

        verify(childDtoMock).process(argThat(validateChild(UID)), argThat(dateMatcher(date)));
    }

    @Test
    public void ShouldGetRightDataWhenAlreadyInitializedWithRightData() {
        HashMap<String, Boolean> flags = new HashMap<String, Boolean>();
        flags.put("child_data_processed", true);
        map.put("ts", "2008-06-16T00:00:00 +0530");
        map.put("uid", UID);
        map.put("udata", getUdata());
        map.put("flags", flags);

        Event event = new Event(map, keyValueStoreMock);

        event.initialize();
        event.process(childDtoMock);
        Map<String, Object> data = event.getData();

        HashMap<String, Object> actualUdata = (HashMap<String, Object>)data.get("udata");
        assertEquals(UID, data.get("uid"));
        validateUdata(actualUdata);
    }

    @Test
    public void ShouldGetRightDataWhenCached() {
        map.put("ts", "2008-06-16T00:00:00 +0530");
        map.put("uid", UID);
        Child child = new Child(UID, true,  getUdata());

        Mockito.stub(keyValueStoreMock.get(UID)).toReturn(child);

        Event event = new Event(map, keyValueStoreMock);

        event.initialize();
        event.process(childDtoMock);
        Map<String, Object> data = event.getData();

        HashMap<String, Object> actualUdata = (HashMap<String, Object>)data.get("udata");
        assertEquals(UID, data.get("uid"));
        validateUdata(actualUdata);
    }

    @Test
    public void ShouldGetRightDataWhenReadFromDb() throws SQLException {
        map.put("ts", "2008-06-16T00:00:00 +0530");
        map.put("uid", UID);

        Child child = new Child(UID, true,  getUdata());
        stub(childDtoMock.process(any(Child.class), any(Date.class))).toReturn(child);

        Event event = new Event(map, keyValueStoreMock);

        event.initialize();
        event.process(childDtoMock);
        Map<String, Object> data = event.getData();

        HashMap<String, Object> actualUdata = (HashMap<String, Object>)data.get("udata");
        assertEquals(UID, data.get("uid"));
        validateUdata(actualUdata);
    }

    @Test
    public void ShouldNotUpdateEventIfChildIsNotProcessed() throws SQLException {
        map.put("ts", "2008-06-16T00:00:00 +0530");
        map.put("uid", UID);

        Child child = new Child(UID, false, getUdata());
        stub(childDtoMock.process(any(Child.class), any(Date.class))).toReturn(child);

        Event event = new Event(map, keyValueStoreMock);

        event.initialize();
        event.process(childDtoMock);
        Map<String, Object> data = event.getData();

        HashMap<String, Object> actualUdata = (HashMap<String, Object>)data.get("udata");
        assertEquals(UID, data.get("uid"));
        assertEquals(null, actualUdata);
    }

    @Test
    public void ShouldBeAbleToIndicateIfNotAbleToConnectToDatabase() throws SQLException {
        map.put("ts", "2008-06-16T00:00:00 +0530");
        map.put("uid", UID);

        stub(childDtoMock.process(any(Child.class), any(Date.class))).toThrow(new SQLException("Not able to connect to database"));

        Event event = new Event(map, keyValueStoreMock);

        event.initialize();
        event.process(childDtoMock);
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

        event.initialize();
        event.process(childDtoMock);

        assertFalse(event.isProcessed());
    }

    @Test
    public void ShouldBeAbleToProcessIfFlagsDoesNotHaveChildProcessedFlag() throws SQLException {
        HashMap<String, Boolean> flags = new HashMap<String, Boolean>();
        map.put("ts", "2008-06-16T00:00:00 +0530");
        map.put("uid", UID);
        map.put("udata", getUdata());
        map.put("flags", flags);

        Child child = new Child(UID, true, getUdata());
        stub(childDtoMock.process(any(Child.class), any(Date.class))).toReturn(child);
        Event event = new Event(map, keyValueStoreMock);

        event.initialize();
        event.process(childDtoMock);

       assertTrue(event.isProcessed());
    }

    @Test
    public void ShouldCreateProcessedCountFirstTime() throws SQLException {
        map.put("ts", "2008-06-16T00:00:00 +0530");
        map.put("uid", UID);

        Event event = new Event(map, keyValueStoreMock);

        event.initialize();
        event.addMetadata();

        Map<String,Object> metadata = (Map<String, Object>) event.getMap().get("metadata");
        assertTrue(metadata.containsKey("processed_count"));
        assertEquals(1, metadata.get("processed_count"));
    }

    @Test
    public void ShouldIncrementProcessedTime() throws SQLException {

        HashMap<String, Object> metadata = new HashMap<String, Object>();
        metadata.put("processed_count",1);
        map.put("ts", "2008-06-16T00:00:00 +0530");
        map.put("uid", UID);
        map.put("metadata", metadata);

        Event event = new Event(map, keyValueStoreMock);

        event.initialize();
        event.addMetadata();

        Map<String,Object> meta = (Map<String, Object>) event.getMap().get("metadata");
        assertEquals(2,metadata.get("processed_count"));
    }


    private void validateUdata(HashMap<String, Object> actualUdata) {
        HashMap<String, Object> expectedUdata = getUdata();

        assertEquals(expectedUdata.get("age_completed_years"),actualUdata.get("age_completed_years"));
        assertEquals(expectedUdata.get("gender"), actualUdata.get("gender"));
        assertEquals(expectedUdata.get("handle"), actualUdata.get("handle"));
        assertEquals(expectedUdata.get("standard"),actualUdata.get("standard"));
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
        return udata;
    }

    private ArgumentMatcher<Date> dateMatcher(final Date expectedDate) {
        return new ArgumentMatcher<Date>() {
            @Override
            public boolean matches(Object argument) {
                Date actualDate = (Date) argument;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Assert.assertEquals(dateFormat.format(expectedDate),dateFormat.format(actualDate));
                return true;
            }
        };
    }


}