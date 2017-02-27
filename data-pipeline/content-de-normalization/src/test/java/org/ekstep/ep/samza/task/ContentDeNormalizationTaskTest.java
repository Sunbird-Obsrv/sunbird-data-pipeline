package org.ekstep.ep.samza.task;

import com.google.gson.Gson;
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
import org.ekstep.ep.samza.Content;
import org.ekstep.ep.samza.ContentCache;
import org.ekstep.ep.samza.ContentDeNormalizationMetrics;
import org.ekstep.ep.samza.Event;
import org.ekstep.ep.samza.external.SearchServiceClient;
import org.ekstep.ep.samza.fixture.ContentFixture;
import org.ekstep.ep.samza.fixture.EventFixture;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

public class ContentDeNormalizationTaskTest {

    private static final String SUCCESS_TOPIC = "telemetry.content.de_normalized";
    private static final String FAILED_TOPIC = "telemetry.content.de_normalized.fail";
    private static final String CONTENT_CACHE_TTL = "60000";
    private final String EVENTS_TO_SKIP = "ME_.*";
    private final String EVENTS_TO_ALLOW = "GE_LAUNCH_GAME,OE_.*";
    private MessageCollector collectorMock;
    private SearchServiceClient searchServiceMock;
    private TaskContext contextMock;
    private MetricsRegistry metricsRegistry;
    private Counter counter;
    private TaskCoordinator coordinatorMock;
    private IncomingMessageEnvelope envelopeMock;
    private Config configMock;
    private ContentDeNormalizationTask contentDeNormalizationTask;
    private KeyValueStore contentStoreMock;

    @Before
    public void setUp() {
        collectorMock = mock(MessageCollector.class);
        searchServiceMock = mock(SearchServiceClient.class);
        contextMock = Mockito.mock(TaskContext.class);
        metricsRegistry = Mockito.mock(MetricsRegistry.class);
        counter = Mockito.mock(Counter.class);
        coordinatorMock = mock(TaskCoordinator.class);
        envelopeMock = mock(IncomingMessageEnvelope.class);
        configMock = Mockito.mock(Config.class);
        contentStoreMock = mock(KeyValueStore.class);

        stub(configMock.get("output.success.topic.name", SUCCESS_TOPIC)).toReturn(SUCCESS_TOPIC);
        stub(configMock.get("output.failed.topic.name", FAILED_TOPIC)).toReturn(FAILED_TOPIC);
        stub(configMock.get("events.to.skip", "")).toReturn(EVENTS_TO_SKIP);
        stub(configMock.get("events.to.allow", "")).toReturn(EVENTS_TO_ALLOW);
        stub(configMock.get("content.store.ttl", "60000")).toReturn(CONTENT_CACHE_TTL);
        stub(metricsRegistry.newCounter(ContentDeNormalizationMetrics.class.getName(), "message-count")).toReturn(counter);
        stub(contextMock.getMetricsRegistry()).toReturn(metricsRegistry);

        contentDeNormalizationTask = new ContentDeNormalizationTask(configMock, contextMock, searchServiceMock, contentStoreMock);
    }

    @Test
    public void shouldSkipIfContentIdIsBlank() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.EventWithoutContentId());
        contentDeNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUCCESS_TOPIC)));
    }

    @Test
    public void shouldProcessEventFromCacheIfPresentAndSkipServiceCall() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.OeEvent());

        ContentCache contentCache = new ContentCache(ContentFixture.getContent(), new Date().getTime());
        String contentCacheJson = new Gson().toJson(contentCache, ContentCache.class);

        stub(contentStoreMock.get(ContentFixture.getContentID())).toReturn(contentCacheJson);

        contentDeNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);

        verify(contentStoreMock, times(1)).get(ContentFixture.getContentID());
        verify(searchServiceMock, times(0)).search(ContentFixture.getContentID());
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUCCESS_TOPIC)));
    }

    @Test
    public void shouldCallSearchApiAndUpdateCacheIfEventIsNotPresentInCache() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.OeEvent());
        when(contentStoreMock.get(ContentFixture.getContentID())).thenReturn(null, getContentCacheJson());

        stub(searchServiceMock.search(ContentFixture.getContentID())).toReturn(ContentFixture.getContent());

        contentDeNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);

        verify(contentStoreMock, times(2)).get(ContentFixture.getContentID());
        verify(searchServiceMock, times(1)).search(ContentFixture.getContentID());
        verify(contentStoreMock, times(1)).put(anyString(), anyString());
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUCCESS_TOPIC)));
    }

    @Test
    public void shouldCallSearchApiAndUpdateCacheIfCacheIsExpired() throws Exception {

        ContentCache contentCache = new ContentCache(ContentFixture.getContent(), new Date().getTime() - 100000);
        String contentCacheJson = new Gson().toJson(contentCache, ContentCache.class);

        stub(contentStoreMock.get(ContentFixture.getContentID())).toReturn(contentCacheJson);
        stub(envelopeMock.getMessage()).toReturn(EventFixture.OeEvent());
        stub(searchServiceMock.search(ContentFixture.getContentID())).toReturn(ContentFixture.getContent());

        contentDeNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);

        verify(contentStoreMock, times(2)).get(ContentFixture.getContentID());
        verify(searchServiceMock, times(1)).search(ContentFixture.getContentID());
        verify(contentStoreMock, times(1)).put(anyString(), anyString());
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUCCESS_TOPIC)));
    }

    @Test
    public void shouldProcessAllOeEventsAndUpdateContentData() throws Exception {
        when(contentStoreMock.get(ContentFixture.getContentID())).thenReturn(null, getContentCacheJson());
        stub(envelopeMock.getMessage()).toReturn(EventFixture.OeEvent());
        stub(searchServiceMock.search("do_30076072")).toReturn(ContentFixture.getContent());

        contentDeNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);

        verify(searchServiceMock, times(1)).search("do_30076072");
        Map<String, Object> processedMessage = (Map<String, Object>) envelopeMock.getMessage();

        assertTrue(processedMessage.containsKey("contentdata"));

        HashMap<String, Object> contentData = (HashMap<String, Object>) processedMessage.get("contentdata");
        assertEquals(contentData.get("name"), ContentFixture.getContentMap().get("name"));
        assertEquals(contentData.get("description"), ContentFixture.getContentMap().get("description"));

        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUCCESS_TOPIC)));
    }

    @Test
    public void shouldProcessGeLaunchEventAndUpdateContentData() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.GeLaunchEvent());
        stub(searchServiceMock.search(ContentFixture.getContentID())).toReturn(ContentFixture.getContent());
        ContentCache contentCache = new ContentCache(ContentFixture.getContent(), new Date().getTime() - 100000);
        String contentCacheJson = new Gson().toJson(contentCache, ContentCache.class);
        stub(contentStoreMock.get(ContentFixture.getContentID())).toReturn(contentCacheJson);

        contentDeNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);

        verify(searchServiceMock, times(1)).search("do_30076072");
        Map<String, Object> processedMessage = (Map<String, Object>) envelopeMock.getMessage();

        assertTrue(processedMessage.containsKey("contentdata"));

        HashMap<String, Object> contentData = (HashMap<String, Object>) processedMessage.get("contentdata");
        assertEquals(contentData.get("name"), ContentFixture.getContentMap().get("name"));
        assertEquals(contentData.get("description"), ContentFixture.getContentMap().get("description"));

        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUCCESS_TOPIC)));
    }

    @Test
    public void shouldNotProcessAnyMeEvents() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.MeEvent());

        contentDeNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);

        verify(contentStoreMock, times(0)).get(anyString());
        verify(searchServiceMock, times(0)).search(anyString());
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUCCESS_TOPIC)));
    }

    @Test
    public void shouldNotProcessAnyGeEventOtherThanGenieLaunch() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.OtherGeEvent());

        contentDeNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);

        verify(contentStoreMock, times(0)).get(anyString());
        verify(searchServiceMock, times(0)).search(anyString());
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUCCESS_TOPIC)));
    }

    private ArgumentMatcher<OutgoingMessageEnvelope> validateOutputTopic(final Object message, final String stream) {
        return new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                SystemStream systemStream = outgoingMessageEnvelope.getSystemStream();
                assertEquals("kafka", systemStream.getSystem());
                assertEquals(stream, systemStream.getStream());
                assertEquals(message, outgoingMessageEnvelope.getMessage());
                return true;
            }
        };
    }

    private String getContentCacheJson() {
        Content content = ContentFixture.getContent();
        ContentCache contentCache = new ContentCache(content, new Date().getTime());
        return new Gson().toJson(contentCache, ContentCache.class);
    }

}