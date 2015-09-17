package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.ekstep.ep.samza.system.TaxonomyCache;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.metrics.Counter;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class TaxonomyDeNormalizationStreamTaskTest {
    private MessageCollector collector;
    private Config configMock;
    private TaskContext contextMock;
    private IncomingMessageEnvelope envelope;
    private TaskCoordinator coordinator;
    private TaxonomyDeNormalizationStreamTask taxonomyDenormalizationStreamTask;
    private TaxonomyCache taxonomyCache;

    @Before
    public void setMock() {
        collector = mock(MessageCollector.class);
        configMock = Mockito.mock(Config.class);
        contextMock = Mockito.mock(TaskContext.class);
        envelope = Mockito.mock(IncomingMessageEnvelope.class);
        coordinator = Mockito.mock(TaskCoordinator.class);
        taxonomyDenormalizationStreamTask = new TaxonomyDeNormalizationStreamTask();
        taxonomyCache = Mockito.mock(TaxonomyCache.class);
        taxonomyDenormalizationStreamTask.init(configMock,contextMock);
        taxonomyDenormalizationStreamTask.process(envelope, collector, coordinator);
    }

    @Test
    public void ShouldAttemptToLookupConceptInCache() throws Exception{
    }

    @Test
    public void ShouldAttemptToWarmCacheIfCacheMiss() throws Exception{

    }

    @Test
    public void ShouldAttemptToWarmCacheIfCacheOld() throws Exception{

    }

    @Test
    public void ShouldNotAttemptToFetchDataIfCacheWarm() throws Exception{

    }

}
