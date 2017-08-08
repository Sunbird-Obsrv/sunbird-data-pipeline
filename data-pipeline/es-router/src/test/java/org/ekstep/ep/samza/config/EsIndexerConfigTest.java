package org.ekstep.ep.samza.config;

import org.ekstep.ep.es_router.config.*;
import org.ekstep.ep.es_router.domain.Event;
import org.ekstep.ep.samza.reader.Telemetry;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;

import static org.ekstep.ep.es_router.util.Constants.DEFAULT_INDEX_TYPE;
import static org.junit.Assert.*;

/**
 * Created by aks on 01/08/17.
 */
public class EsIndexerConfigTest {
  EsIndexDateConfig defaultEsIndexDateConfig = new EsIndexDateConfig("ts", "string", "", "", false);
  private Telemetry telemetry;
  private RuleConfig geRuleConfig;
  private RuleConfig oeRuleConfig;

  @Before
  public void setup(){
    geRuleConfig = new RuleConfig("eid", "GE.*");
    oeRuleConfig = new RuleConfig("eid", "OE.*");
  }

  @Test
  public void shouldApplyApplicableTopicConfig() {

    EventConfig geEventConfig = new EventConfig(Arrays.asList(geRuleConfig), "geIndex", 2.0, defaultEsIndexDateConfig, false, DEFAULT_INDEX_TYPE);
    EventConfig geEventConfig2 = new EventConfig(Arrays.asList(geRuleConfig), "geIndex2", 2.0, defaultEsIndexDateConfig, false, DEFAULT_INDEX_TYPE);
    TopicConfig kafka1 = new TopicConfig("kafka1", Arrays.asList(geEventConfig));
    TopicConfig kafka2 = new TopicConfig("kafka2", Arrays.asList(geEventConfig2));
    EsIndexerConfig esIndexerConfig = new EsIndexerConfig(Arrays.asList(kafka1, kafka2));
    Event event = getEvent("GE_ASSESS", "kafka2");

    esIndexerConfig.updateEsIndex(event);

    assertEquals("geIndex2-2017.07",telemetry.read("metadata.index_name").value());
  }

  @Test
  public void shouldNotApplyWhenNoTopicConfigMatches() {
    EventConfig geEventConfig = new EventConfig(Arrays.asList(geRuleConfig), "geIndex", 2.0, defaultEsIndexDateConfig, false, DEFAULT_INDEX_TYPE);
    EventConfig geEventConfig2 = new EventConfig(Arrays.asList(geRuleConfig), "geIndex2", 2.0, defaultEsIndexDateConfig, false, DEFAULT_INDEX_TYPE);
    TopicConfig kafka1 = new TopicConfig("kafka1", Arrays.asList(geEventConfig));
    TopicConfig kafka2 = new TopicConfig("kafka2", Arrays.asList(geEventConfig2));
    EsIndexerConfig esIndexerConfig = new EsIndexerConfig(Arrays.asList(kafka1, kafka2));
    Event event = getEvent("GE_ASSESS", "kafka3");

    esIndexerConfig.updateEsIndex(event);

    assertTrue(telemetry.read("metadata.index_name").isNull());
  }

  @Test
  public void shouldNotApplyWhenNoEventConfigMatches() {
    EventConfig geEventConfig = new EventConfig(Arrays.asList(oeRuleConfig), "oeIndex", 2.0, defaultEsIndexDateConfig, false, DEFAULT_INDEX_TYPE);
    EventConfig geEventConfig2 = new EventConfig(Arrays.asList(geRuleConfig), "geIndex2", 2.0, defaultEsIndexDateConfig, false, DEFAULT_INDEX_TYPE);
    TopicConfig kafka1 = new TopicConfig("kafka1", Arrays.asList(geEventConfig));
    TopicConfig kafka2 = new TopicConfig("kafka2", Arrays.asList(geEventConfig2));
    EsIndexerConfig esIndexerConfig = new EsIndexerConfig(Arrays.asList(kafka1, kafka2));
    Event event = getEvent("OE_ASSESS", "kafka2");

    esIndexerConfig.updateEsIndex(event);

    assertTrue(telemetry.read("metadata.index_name").isNull());
  }

  private Event getEvent(String eventType, String eventSource) {
    telemetry = new Telemetry(new HashMap<String, Object>());
    telemetry.add("ts","2017-07-21T05:24:47.105+0000");
    telemetry.add("eid", eventType);
    return new Event(telemetry, eventSource);
  }

}