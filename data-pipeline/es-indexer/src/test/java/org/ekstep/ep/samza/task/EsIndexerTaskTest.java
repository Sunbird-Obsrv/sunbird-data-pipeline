package org.ekstep.ep.samza.task;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.samza.Partition;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.ekstep.ep.samza.esclient.ElasticSearchService;
import org.ekstep.ep.samza.esclient.IndexResponse;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

import java.util.List;

public class EsIndexerTaskTest {

	private static final String FAILED_TOPIC = "telemetry.indexer.failed";
	private static final String ES_HOST = "localhost";
	private static final String ES_PORT = "9200";
	private static final String INDEX_STREAM_MAPPING = "{\"telemetry.with_location\":\"default\",\"telemetry.log\":\"backend\",\"telemetry.failed\":\"failed-telemetry\"}";
	private MessageCollector collectorMock;
	private ElasticSearchService esServiceMock;
	private TaskContext contextMock;
	private MetricsRegistry metricsRegistry;
	private Counter counter;
	private TaskCoordinator coordinatorMock;
	private IncomingMessageEnvelope envelopeMock;
	private SystemStreamPartition streamMock;
	private Config configMock;
	private EsIndexerTask esIndexerPrimaryTask;

	@Before
	public void setUp() throws Exception {

		collectorMock = mock(MessageCollector.class);
		contextMock = Mockito.mock(TaskContext.class);
		metricsRegistry = Mockito.mock(MetricsRegistry.class);
		counter = Mockito.mock(Counter.class);
		coordinatorMock = mock(TaskCoordinator.class);
		envelopeMock = mock(IncomingMessageEnvelope.class);
		configMock = Mockito.mock(Config.class);
		esServiceMock = Mockito.mock(ElasticSearchService.class);
		streamMock = Mockito.mock(SystemStreamPartition.class);

		stub(envelopeMock.getSystemStreamPartition()).toReturn(streamMock);

		stub(configMock.get("output.failed.topic.name", FAILED_TOPIC)).toReturn(FAILED_TOPIC);
		stub(configMock.get("hosts.elastic_search", ES_HOST)).toReturn(ES_HOST);
		stub(configMock.get("port.elastic_search", ES_PORT)).toReturn(ES_PORT);

		stub(configMock.get("indexer.primary.index", "telemetry")).toReturn("telemetry");
		stub(configMock.get("indexer.summary.index", "summary")).toReturn("summary");
		stub(configMock.get("indexer.summary.cumulative.index", "summary-cumulative")).toReturn("summary-cumulative");
		stub(configMock.get("indexer.stream.mapping", INDEX_STREAM_MAPPING)).toReturn(INDEX_STREAM_MAPPING);
		stub(configMock.get("esindex.name.suffix.datetime.field", "ts")).toReturn("ts");
		stub(configMock.get("esindex.name.suffix.datetime.field.pattern", "yyyy-MM-dd'T'HH:mm:ss"))
				.toReturn("yyyy-MM-dd'T'HH:mm:ss");
		stub(configMock.get("esindex.name.suffix.datetime.pattern", "yyyy.MM"))
				.toReturn("yyyy.MM");

		stub(metricsRegistry.newCounter(anyString(), anyString())).toReturn(counter);
		stub(contextMock.getMetricsRegistry()).toReturn(metricsRegistry);
		stub(envelopeMock.getOffset()).toReturn("2");
        stub(streamMock.getPartition()).toReturn(mock(Partition.class));
		stub(streamMock.getStream()).toReturn("input.topic");

		esIndexerPrimaryTask = new EsIndexerTask(configMock, contextMock, esServiceMock);
	}

	@Test
	public void shouldIndexEventsToTelemetryIndex() throws Exception {
		
		stub(streamMock.getStream()).toReturn("telemetry.with_location");
		stub(envelopeMock.getMessage()).toReturn(EventFixture.getEvent(EventFixture.RAW_EVENT));
		stub(esServiceMock.index(anyString(), anyString(), anyString(), anyString())).toReturn(new IndexResponse("200", null));
		ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);

		esIndexerPrimaryTask.process(envelopeMock, collectorMock, coordinatorMock);

		verify(esServiceMock, times(1)).index(argument.capture(), argument.capture(), argument.capture(), argument.capture());
		
		List<String> values = argument.getAllValues();
		
		assertEquals("telemetry-2017.02", values.get(0));
		assertEquals("events", values.get(1));
		assertEquals(EventFixture.getEvent(EventFixture.RAW_EVENT).get("mid"), values.get(3));
		
		verify(collectorMock, times(0)).send(any(OutgoingMessageEnvelope.class));
	}

	@Test
	public void testWhenEmptyTsFieldForTelemetryIndex() throws Exception {
		DateTimeUtils.setCurrentMillisFixed(10L);
		JsonObject modifiedRawEvent = new JsonParser().parse(EventFixture.RAW_EVENT).getAsJsonObject();
		modifiedRawEvent.remove("ts");
		stub(streamMock.getStream()).toReturn("telemetry.with_location");
		stub(envelopeMock.getMessage()).toReturn(EventFixture.getEvent(modifiedRawEvent.toString()));
		stub(esServiceMock.index(anyString(), anyString(), anyString(), anyString())).
				toReturn(new IndexResponse("200", null));
		ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);

		esIndexerPrimaryTask.process(envelopeMock, collectorMock, coordinatorMock);

		verify(esServiceMock, times(1)).index(argument.capture(), argument.capture(), argument.capture(), argument.capture());

		List<String> values = argument.getAllValues();

		assertEquals("telemetry-1970.01", values.get(0));
		assertEquals("events", values.get(1));
		assertEquals(EventFixture.getEvent(EventFixture.RAW_EVENT).get("mid"), values.get(3));

		verify(collectorMock, times(0)).send(any(OutgoingMessageEnvelope.class));
		DateTimeUtils.setCurrentMillisSystem();
	}

	
	@Test
	public void shouldIndexEventsToSummaryIndex() throws Exception {
		
		stub(streamMock.getStream()).toReturn("telemetry.with_location");
		stub(envelopeMock.getMessage()).toReturn(EventFixture.getEvent(EventFixture.SUMMARY_EVENT));
		stub(esServiceMock.index(anyString(), anyString(), anyString(), anyString())).toReturn(new IndexResponse("200", null));
		ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);

		esIndexerPrimaryTask.process(envelopeMock, collectorMock, coordinatorMock);

		verify(esServiceMock, times(1)).index(argument.capture(), argument.capture(), argument.capture(), argument.capture());
		
		List<String> values = argument.getAllValues();
		
		assertEquals("summary-2017.02", values.get(0));
		assertEquals("events", values.get(1));
		assertEquals(EventFixture.getEvent(EventFixture.RAW_EVENT).get("mid"), values.get(3));
		
		verify(collectorMock, times(0)).send(any(OutgoingMessageEnvelope.class));
	}

	@Test
	public void shouldIndexEventsToCumulativeSummaryIndex() throws Exception {

		stub(streamMock.getStream()).toReturn("telemetry.with_location");
		stub(envelopeMock.getMessage()).toReturn(EventFixture.getEvent(EventFixture.CUMULATIVE_SUMMARY_EVENT));
		stub(esServiceMock.index(anyString(), anyString(), anyString(), anyString())).toReturn(new IndexResponse("200", null));
		ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);

		esIndexerPrimaryTask.process(envelopeMock, collectorMock, coordinatorMock);

		verify(esServiceMock, times(1)).index(argument.capture(), argument.capture(), argument.capture(), argument.capture());
		
		List<String> values = argument.getAllValues();
		
		assertEquals("summary-cumulative-2017.02", values.get(0));
		assertEquals("events", values.get(1));
		assertEquals(EventFixture.getEvent(EventFixture.RAW_EVENT).get("mid"), values.get(3));
		
		verify(collectorMock, times(0)).send(any(OutgoingMessageEnvelope.class));
	}
	
	@Test
	public void shouldIndexEventsToBackendIndex() throws Exception {

		stub(streamMock.getStream()).toReturn("telemetry.log");
		stub(envelopeMock.getMessage()).toReturn(EventFixture.getEvent(EventFixture.CUMULATIVE_SUMMARY_EVENT));
		stub(esServiceMock.index(anyString(), anyString(), anyString(), anyString())).toReturn(new IndexResponse("200", null));
		ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);

		esIndexerPrimaryTask.process(envelopeMock, collectorMock, coordinatorMock);

		verify(esServiceMock, times(1)).index(argument.capture(), argument.capture(), argument.capture(), argument.capture());
		
		List<String> values = argument.getAllValues();

		assertEquals("backend-2017.02", values.get(0));
		assertEquals("events", values.get(1));
		assertEquals(EventFixture.getEvent(EventFixture.RAW_EVENT).get("mid"), values.get(3));
		
		verify(collectorMock, times(0)).send(any(OutgoingMessageEnvelope.class));
	}
	
	@Test
	public void shouldIndexEventsToFailedIndex() throws Exception {

		stub(streamMock.getStream()).toReturn("telemetry.failed");
		stub(envelopeMock.getMessage()).toReturn(EventFixture.getEvent(EventFixture.CUMULATIVE_SUMMARY_EVENT));
		stub(esServiceMock.index(anyString(), anyString(), anyString(), anyString())).toReturn(new IndexResponse("200", null));
		ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);

		esIndexerPrimaryTask.process(envelopeMock, collectorMock, coordinatorMock);

		verify(esServiceMock, times(1)).index(argument.capture(), argument.capture(), argument.capture(), argument.capture());
		
		List<String> values = argument.getAllValues();
		
		assertEquals("failed-telemetry-2017.02", values.get(0));
		assertEquals("events", values.get(1));
		assertEquals(EventFixture.getEvent(EventFixture.RAW_EVENT).get("mid"), values.get(3));
		
		verify(collectorMock, times(0)).send(any(OutgoingMessageEnvelope.class));
	}

	@Test
	public void shouldIndexMissingEidEventsToFailedIndex() throws Exception {

		stub(streamMock.getStream()).toReturn("telemetry.failed");
		stub(envelopeMock.getMessage()).toReturn(EventFixture.getEvent(EventFixture.EVENT_WITH_EID_MISSING));
		stub(esServiceMock.index(anyString(), anyString(), anyString(), anyString()))
				.toReturn(new IndexResponse("200", null));
		ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);

		esIndexerPrimaryTask.process(envelopeMock, collectorMock, coordinatorMock);

		verify(esServiceMock, times(1)).index(argument.capture(), argument.capture(),
				argument.capture(), argument.capture());

		List<String> values = argument.getAllValues();

		assertEquals("failed-telemetry-2017.02", values.get(0));
		assertEquals("events", values.get(1));

		verify(collectorMock, times(0)).send(any(OutgoingMessageEnvelope.class));
	}
	
	
	@Test
	public void shouldSendEventsToFailedTopic() throws Exception {

		stub(envelopeMock.getMessage()).toReturn(EventFixture.getEvent(EventFixture.RAW_EVENT));
		stub(streamMock.getStream()).toReturn("telemetry.xyz");
		esIndexerPrimaryTask.process(envelopeMock, collectorMock, coordinatorMock);
		verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), FAILED_TOPIC)));
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

}