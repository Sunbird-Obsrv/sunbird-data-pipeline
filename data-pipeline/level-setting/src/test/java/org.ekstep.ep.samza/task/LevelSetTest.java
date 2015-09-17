package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.ekstep.ep.samza.system.Event;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;

public class LevelSetTest {

    private Config configMock;
    private TaskContext contextMock;
    private LevelSetStreamTask levelSetTask;
    private IncomingMessageEnvelope incomingMessageEnvelop;
    private MessageCollector messageCollector;
    private TaskCoordinator taskCoordinator;

    @Before
    public void setMock() {
        configMock = Mockito.mock(Config.class);
        contextMock = Mockito.mock(TaskContext.class);
        levelSetTask = new LevelSetStreamTask();
        incomingMessageEnvelop = Mockito.mock(IncomingMessageEnvelope.class);
        messageCollector = Mockito.mock(MessageCollector.class);
        taskCoordinator = Mockito.mock(TaskCoordinator.class);
    }

    @Test
    public void ShouldIndexLevelSet() throws Exception{
        levelSetTask.init(configMock,contextMock);
        levelSetTask.process(incomingMessageEnvelop,messageCollector,taskCoordinator);
    }

}
