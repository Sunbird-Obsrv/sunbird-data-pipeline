package org.ekstep.ep.samza.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.ekstep.ep.samza.util.RedisConnect;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import com.google.gson.Gson;

import redis.clients.jedis.Jedis;
import redis.embedded.RedisServer;

public class TelemetryRedacterTaskTest {

  private MessageCollector collectorMock;
  private TaskContext contextMock;
  private MetricsRegistry metricsRegistry;
  private Counter counter;
  private TaskCoordinator coordinatorMock;
  private IncomingMessageEnvelope envelopeMock;
  private Config configMock;
  private TelemetryRedacterTask telemetryRedacterTask;
  private RedisServer redisServer;
  private RedisConnect redisConnect;

  @Before
  public void setUp() throws IOException {

    redisServer = new RedisServer(6379);
    redisServer.start();
    collectorMock = mock(MessageCollector.class);
    contextMock = Mockito.mock(TaskContext.class);
    metricsRegistry = Mockito.mock(MetricsRegistry.class);
    counter = Mockito.mock(Counter.class);
    coordinatorMock = mock(TaskCoordinator.class);
    envelopeMock = mock(IncomingMessageEnvelope.class);
    configMock = Mockito.mock(Config.class);
    when(configMock.get("redis.host", "localhost")).thenReturn("localhost");
    when(configMock.getInt("redis.port", 6379)).thenReturn(6379);
    when(configMock.getInt("redis.connection.max", 2)).thenReturn(2);
    when(configMock.getInt("redis.connection.idle.max", 2)).thenReturn(2);
    when(configMock.getInt("redis.connection.idle.min", 1)).thenReturn(1);
    when(configMock.getInt("redis.connection.minEvictableIdleTimeSeconds", 120)).thenReturn(120);
    when(configMock.getInt("redis.connection.timeBetweenEvictionRunsSeconds", 300)).thenReturn(300);
    when(configMock.getInt("redis.contentDB.index", 5)).thenReturn(5);
    redisConnect = new RedisConnect(configMock);

    stub(configMock.get("redacted.route.topic", "telemetry.raw")).toReturn("telemetry.raw");
    stub(configMock.get("nonredacted.route.topic", "telemetry.assess.raw")).toReturn("telemetry.assess.raw");
    stub(configMock.get("failed.topic.name", "telemetry.failed")).toReturn("telemetry.failed");

    stub(metricsRegistry.newCounter(anyString(), anyString())).toReturn(counter);
    stub(contextMock.getMetricsRegistry()).toReturn(metricsRegistry);

    telemetryRedacterTask = new TelemetryRedacterTask(configMock, contextMock);
  }

  @After
  public void tearDown() {
    redisServer.stop();
  }

  @Test
  public void shouldSendEventToErroTopicIfEventIsNotParseable() throws Exception {

    stub(envelopeMock.getMessage()).toReturn(EventFixture.UNPARSABLE_GE_GENIE_UPDATE_EVENT);
    telemetryRedacterTask.process(envelopeMock, collectorMock, coordinatorMock);
    verify(collectorMock).send(argThat(validateOutputTopic("telemetry.failed")));
  }

  @Test
  public void shouldRedactEventAndSendToRawTopic() throws Exception {

    Jedis jedis = redisConnect.getConnection(5);
    jedis.set("801ae93c-8807-4be5-8853-dd49362d8776", "{\"questionType\":\"Registration\"}");
    jedis.close();
    stub(envelopeMock.getMessage()).toReturn(EventFixture.ASSESS_EVENT);
    telemetryRedacterTask.process(envelopeMock, collectorMock, coordinatorMock);
    verify(collectorMock, times(2)).send(argThat(validateRedactedTopic(true)));
  }
  
  @Test
  public void shouldRedactEventAndSendToRawTopic2() throws Exception {

    Jedis jedis = redisConnect.getConnection(5);
    jedis.set("do_21299463432309145612386", "{\"questionType\":\"Registration\"}");
    jedis.close();
    stub(envelopeMock.getMessage()).toReturn(EventFixture.RESPONSE_EVENT);
    telemetryRedacterTask.process(envelopeMock, collectorMock, coordinatorMock);
    verify(collectorMock, times(2)).send(argThat(validateRedactedTopic(true)));
  }

  @Test
  public void shouldNotRedactEventAndSendToRawTopic1() throws Exception {
    
    stub(envelopeMock.getMessage()).toReturn(EventFixture.ASSESS_EVENT_WITHOUT_QID);
    telemetryRedacterTask.process(envelopeMock, collectorMock, coordinatorMock);
    verify(collectorMock, times(1)).send(argThat(validateOutputTopic("telemetry.raw")));
  }
  
  @Test
  public void shouldNotRedactEventAndSendToRawTopic2() throws Exception {
    
    Jedis jedis = redisConnect.getConnection(5);
    jedis.del("801ae93c-8807-4be5-8853-dd49362d8776");
    jedis.close();

    stub(envelopeMock.getMessage()).toReturn(EventFixture.ASSESS_EVENT);
    telemetryRedacterTask.process(envelopeMock, collectorMock, coordinatorMock);
    verify(collectorMock, times(1)).send(argThat(validateOutputTopic("telemetry.raw")));
  }
  
  @Test
  public void shouldNotRedactEventAndSendToRawTopic3() throws Exception {

    Jedis jedis = redisConnect.getConnection(5);
    jedis.set("801ae93c-8807-4be5-8853-dd49362d8776", "{\"questionType\":\"Survey\"}");
    jedis.close();
    stub(envelopeMock.getMessage()).toReturn(EventFixture.ASSESS_EVENT);
    telemetryRedacterTask.process(envelopeMock, collectorMock, coordinatorMock);
    verify(collectorMock).send(argThat(validateOutputTopic("telemetry.raw")));
  }
  
  public ArgumentMatcher<OutgoingMessageEnvelope> validateRedactedTopic(final boolean redacted) {
    return new ArgumentMatcher<OutgoingMessageEnvelope>() {
      @Override
      public boolean matches(Object o) {
        OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
        SystemStream systemStream = outgoingMessageEnvelope.getSystemStream();
        assertEquals("kafka", systemStream.getSystem());
        assertTrue(Arrays.asList("telemetry.raw", "telemetry.assess.raw").contains(systemStream.getStream()));

        if ("telemetry.raw".equals(systemStream.getStream())) {
          String message = (String) outgoingMessageEnvelope.getMessage();
          Map<String, Object> eventMap = new Gson().fromJson(message, Map.class);
          Event event = new Event(eventMap);
          if (redacted) {
            assertEquals(0, event.readResValues().size());
          }
        }
        if ("telemetry.assess.raw".equals(systemStream.getStream())) {
          System.out.println("Redacted and message sent to raw stream");
          String message = (String) outgoingMessageEnvelope.getMessage();
          Map<String, Object> eventMap = new Gson().fromJson(message, Map.class);
          Event event = new Event(eventMap);
          assertEquals(1, event.readResValues().size());
        }

        return true;
      }
    };
  }

  public ArgumentMatcher<OutgoingMessageEnvelope> validateOutputTopic(final String stream) {
    return new ArgumentMatcher<OutgoingMessageEnvelope>() {
      @Override
      public boolean matches(Object o) {
        OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
        SystemStream systemStream = outgoingMessageEnvelope.getSystemStream();
        assertEquals("kafka", systemStream.getSystem());
        assertEquals(stream, systemStream.getStream());

        return true;
      }
    };
  }

}
