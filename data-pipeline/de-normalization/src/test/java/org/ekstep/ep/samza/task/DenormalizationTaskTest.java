package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.ekstep.ep.samza.model.ChildDto;
import org.ekstep.ep.samza.model.Event;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import java.util.HashMap;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

public class DenormalizationTaskTest{
    private MessageCollector collectorMock;
    private Event eventMock;
    private ChildDto childDtoMock;
    private DeNormalizationTask deNormalizationTask;
    private Config configMock;

    @Before
    public void setUp(){
        collectorMock = mock(MessageCollector.class);
        eventMock = mock(Event.class);
        childDtoMock = mock(ChildDto.class);

        configMock = Mockito.mock(Config.class);
        stub(configMock.get("output.success.topic.name", "events_with_de_normalization")).toReturn("events_with_de_normalization");
        stub(configMock.get("output.failed.topic.name", "events_failed_de_normalization")).toReturn("events_failed_de_normalization");

        deNormalizationTask = new DeNormalizationTask();
    }
    @Test
    public void ShouldInitializeEvent() {
        deNormalizationTask.processEvent(collectorMock, eventMock, childDtoMock);

        verify(eventMock).initialize();
    }

    @Test
    public void ShouldProcessEvent() {
        deNormalizationTask.processEvent(collectorMock, eventMock, childDtoMock);

        verify(eventMock).process(childDtoMock);
    }

    @Test
    public void ShouldSendOutputToFailedTopicAndSuccessTopic() throws Exception {
        stub(eventMock.isProcessed()).toReturn(false);
        HashMap<String, Object> message = new HashMap<String, Object>();
        stub(eventMock.getData()).toReturn(message);
        deNormalizationTask.init(configMock, Mockito.mock(TaskContext.class));

        deNormalizationTask.processEvent(collectorMock, eventMock, childDtoMock);

        verify(collectorMock,times(2)).send(any(OutgoingMessageEnvelope.class));
    }

    @Test
    public void ShouldSendOutputToSuccessTopic() throws Exception {
        stub(eventMock.isProcessed()).toReturn(true);
        HashMap<String, Object> message = new HashMap<String, Object>();
        stub(eventMock.getData()).toReturn(message);
        deNormalizationTask.init(configMock, Mockito.mock(TaskContext.class));

        deNormalizationTask.processEvent(collectorMock, eventMock, childDtoMock);

        verify(collectorMock).send(argThat(validateOutputTopic(message, "events_with_de_normalization")));

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
}