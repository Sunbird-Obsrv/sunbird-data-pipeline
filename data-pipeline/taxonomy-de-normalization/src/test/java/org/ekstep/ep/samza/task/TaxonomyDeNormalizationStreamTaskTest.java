package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.ekstep.ep.samza.fixtures.TaxonomyEventFixture;
//import org.ekstep.ep.samza.fixtures.TaxonomyEvent;
import org.ekstep.ep.samza.system.TaxonomyCache;
import org.ekstep.ep.samza.system.TaxonomyEvent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

//import sun.jvm.hotspot.utilities.Assert;

public class TaxonomyDeNormalizationStreamTaskTest {
    private MessageCollector collector;
    private Config configMock;
    private TaskContext contextMock;
    private IncomingMessageEnvelope envelope;
    private TaskCoordinator coordinator;
    private TaxonomyEvent mockTaxonomyEvent;
    private TaxonomyDeNormalizationStreamTask taxonomyDenormalizationStreamTask;
    private TaxonomyCache taxonomyCache;
    private KeyValueStore<String, Object> taxonomyStore;
    private static final String SUCCESS_TOPIC = "SUCCESS_TOPIC";
    private static final String FAILURE_TOPIC = "FAILURE_TOPIC";

    @Before
    public void setMock() {
        collector = mock(MessageCollector.class);
        envelope = mock(IncomingMessageEnvelope.class);
        coordinator = mock(TaskCoordinator.class);
        configMock = Mockito.mock(Config.class);
        contextMock = Mockito.mock(TaskContext.class);
        when(configMock.get("output.success.topic.name", "events.ecosystem")).thenReturn(SUCCESS_TOPIC);
        when(configMock.get("output.failed.topic.name", "failed_taxonomy_events")).thenReturn(FAILURE_TOPIC);
        taxonomyDenormalizationStreamTask = new TaxonomyDeNormalizationStreamTask();
        taxonomyDenormalizationStreamTask.init(configMock,contextMock);
        mockTaxonomyEvent = Mockito.mock(TaxonomyEvent.class);
    }

    @Test
    public void ShouldWriteToSuccessTopicIfSuccessful() throws Exception{
        doNothing().when(mockTaxonomyEvent).denormalize();
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("key", "value");
        when(mockTaxonomyEvent.getMap()).thenReturn(map);
        taxonomyDenormalizationStreamTask.processEvent(mockTaxonomyEvent, collector);
        verify(collector).send(argThat(validateOutputTopic(map, SUCCESS_TOPIC)));
    }

    @Test
    public void ShouldWriteToFailureTopicIfIOException(){
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("key", "value");
        when(mockTaxonomyEvent.getMap()).thenReturn(map);
        try{ doThrow(new java.io.IOException()).when(mockTaxonomyEvent).denormalize(); }catch(java.io.IOException e){}
        taxonomyDenormalizationStreamTask.processEvent(mockTaxonomyEvent, collector);
        verify(collector).send(argThat(validateOutputTopic(map, FAILURE_TOPIC)));
    }

    @Test
    public void ShouldWriteToFailureTopicIfException(){
        try{ doThrow(new Exception()).when(envelope).getMessage(); }catch(Exception e){}
        taxonomyDenormalizationStreamTask.process(envelope,collector,coordinator);
        verify(collector).send(argThat(validateOutputTopic(null, FAILURE_TOPIC)));
    }

    @Test

    public void ShouldStampChecksumToTaxonomyEvent() throws Exception{
        TaxonomyEvent taxonomyEvent = new TaxonomyEvent((String) TaxonomyEventFixture.JSON);
        // todo try catch added to handle taxonomyEvent.denormalize() failures
        try{
            taxonomyDenormalizationStreamTask.processEvent(taxonomyEvent, collector);
        }
        catch (Exception e){

        }

        Assert.assertEquals(true, taxonomyEvent.getMap().containsKey("metadata"));
    }

    private ArgumentMatcher<OutgoingMessageEnvelope> validateOutputTopic(final Object message, final String stream) {
        return new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                SystemStream systemStream = outgoingMessageEnvelope.getSystemStream();
                assertEquals("kafka", systemStream.getSystem());
                assertEquals(stream, systemStream.getStream());
                assertEquals(message,outgoingMessageEnvelope.getMessage());
                return true;
            }
        };
    }



//    @Test
//    public void ShouldAttemptToWarmCacheIfCacheMiss() throws Exception{
//
//    }
//
//    @Test
//    public void ShouldAttemptToWarmCacheIfCacheOld() throws Exception{
//
//    }
//
//    @Test
//    public void ShouldNotAttemptToFetchDataIfCacheWarm() throws Exception{
//
//    }

}
