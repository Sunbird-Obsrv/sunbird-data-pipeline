package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.ekstep.ep.samza.system.TaxonomyCache;
import org.ekstep.ep.samza.fixtures.TaxonomyEventFixture;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

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
    private KeyValueStore<String, Object> taxonomyStore;

    @Before
    public void setMock() {
        collector = mock(MessageCollector.class);
        configMock = Mockito.mock(Config.class);
        contextMock = Mockito.mock(TaskContext.class);
        envelope = Mockito.mock(IncomingMessageEnvelope.class);
        coordinator = Mockito.mock(TaskCoordinator.class);
        taxonomyDenormalizationStreamTask = new TaxonomyDeNormalizationStreamTask();
        taxonomyCache = Mockito.mock(TaxonomyCache.class);
        taxonomyStore = (KeyValueStore<String, Object>) contextMock.getStore("de-normalization");
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
