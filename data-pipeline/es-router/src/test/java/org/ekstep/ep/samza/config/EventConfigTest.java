package org.ekstep.ep.samza.config;

import org.ekstep.ep.es_router.config.EsIndexDateConfig;
import org.ekstep.ep.es_router.config.EventConfig;
import org.ekstep.ep.es_router.config.RuleConfig;
import org.ekstep.ep.es_router.domain.Event;
import org.ekstep.ep.samza.reader.Telemetry;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.ekstep.ep.es_router.util.Constants.DEFAULT_INDEX_TYPE;
import static org.junit.Assert.*;

public class EventConfigTest {

  private EsIndexDateConfig defaultIndexDate;
  private RuleConfig geRuleConfig;

  @Before
  public void setup(){
    defaultIndexDate = new EsIndexDateConfig("ts", "string", "", "", false);
    geRuleConfig = new RuleConfig("eid", "GE.*");
  }

  @Test
  public void shouldSortObjectInDecreasingWeight() {
    EventConfig firstConfig = new EventConfig(Arrays.asList(geRuleConfig), "ecosystem", 2.0, defaultIndexDate, false, DEFAULT_INDEX_TYPE);
    EventConfig secondConfig = new EventConfig(Arrays.asList(geRuleConfig), "ecosystem", 1.0, defaultIndexDate, false, DEFAULT_INDEX_TYPE);
    EventConfig thirdConfig = new EventConfig(Arrays.asList(geRuleConfig), "ecosystem", 3.0, defaultIndexDate, false, DEFAULT_INDEX_TYPE);
    List<EventConfig> configs = Arrays.asList(firstConfig, secondConfig, thirdConfig);

    Collections.sort(configs);

    assertEquals(thirdConfig,configs.get(0));
    assertEquals(firstConfig,configs.get(1));
    assertEquals(secondConfig,configs.get(2));
  }

  @Test
  public void shouldBeApplicableToEventWhenPatternMatches() {
    EventConfig config = new EventConfig(Arrays.asList(geRuleConfig), "ecosystem", 2.0, defaultIndexDate, false, DEFAULT_INDEX_TYPE);
    HashMap<String, Object> eventMap = new HashMap<String,Object>();
    eventMap.put("eid","GE_START");
    Telemetry telemetry = new Telemetry(eventMap);

    assertTrue("Event should be filtered",config.isApplicable(new Event(telemetry, "")));
  }

  @Test
  public void shouldNotBeApplicableToEventWhenPatternDoesNotMatches() {
    EventConfig config = new EventConfig(Arrays.asList(geRuleConfig), "ecosystem", 2.0, defaultIndexDate, false, DEFAULT_INDEX_TYPE);
    HashMap<String, Object> eventMap = new HashMap<String,Object>();
    eventMap.put("eid","OE_START");
    Telemetry telemetry = new Telemetry(eventMap);

    assertFalse("Event should not be filtered",config.isApplicable(new Event(telemetry, "")));
  }

}