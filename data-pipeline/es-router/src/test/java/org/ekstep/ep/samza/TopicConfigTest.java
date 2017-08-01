package org.ekstep.ep.samza;

import org.ekstep.ep.samza.reader.Telemetry;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.ekstep.ep.samza.Constants.DEFAULT_INDEX_TYPE;
import static org.junit.Assert.*;

public class TopicConfigTest {

  private String DEFAULT_TIME = "2017-07-21T05:24:47.105+0000";
  EsIndexDate defaultEsIndexDate = new EsIndexDate("ts", "string", "", "", false);
  private Rule geRule;
  private Rule oeRule;
  private Rule defaultRule;

  @Before
  public void setup(){
    geRule = new Rule("eid", "GE.*");
    oeRule = new Rule("eid", "OE.*");
    defaultRule = new Rule("eid", ".*");

  }

  @Test
  public void shouldGetTheSpecificEsIndex() throws ParseException {
    EventConfig geEventConfig = new EventConfig(Arrays.asList(geRule), "geIndex", 2.0, defaultEsIndexDate, false, DEFAULT_INDEX_TYPE);
    EventConfig oeEventConfig = new EventConfig(Arrays.asList(oeRule), "oeIndex", 2.0, defaultEsIndexDate, false, DEFAULT_INDEX_TYPE);
    EventConfig defaultEventConfig = new EventConfig(Arrays.asList(defaultRule), "oeIndex", 1.0, defaultEsIndexDate, false, DEFAULT_INDEX_TYPE);
    TopicConfig topicConfig = new TopicConfig("topicName", Arrays.asList(geEventConfig, oeEventConfig, defaultEventConfig));
    Event event = getEvent("GE_INTERRUPT", "", "2017-06-15T05:24:47.105+0000");

    topicConfig.updateEsIndex(event);

    assertEquals("geIndex-2017.06",event.read("metadata.index_name").value());

  }

  @Test
  public void shouldGetTheDefaultEventEsIndex() throws ParseException {
    EventConfig geEventConfig = new EventConfig(Arrays.asList(geRule), "geIndex", 2.0, defaultEsIndexDate, false, DEFAULT_INDEX_TYPE);
    EventConfig oeEventConfig = new EventConfig(Arrays.asList(oeRule), "oeIndex", 2.0, defaultEsIndexDate, false, DEFAULT_INDEX_TYPE);
    EventConfig defaultEventConfig = new EventConfig(Arrays.asList(defaultRule), "default", 1.0, defaultEsIndexDate, false, DEFAULT_INDEX_TYPE);
    TopicConfig topicConfig = new TopicConfig("topicName", Arrays.asList(geEventConfig, oeEventConfig, defaultEventConfig));
    Event event = getEvent("BE_ASSESS", "", DEFAULT_TIME);

    topicConfig.updateEsIndex(event);

    assertEquals("default-2017.07",event.read("metadata.index_name").value());

  }


  @Test
  public void shouldBeApplicableWhenOriginatedFromTheTopic() {
    TopicConfig topicConfig = new TopicConfig("topicName", new ArrayList<EventConfig>());

    assertTrue("topic config should be applicable to the event",topicConfig.isApplicable(getEvent("OE_ASSESS", "topicName", DEFAULT_TIME)));
  }

  @Test
  public void shouldNotBeApplicableWhenOriginatedFromDifferentTopic() {
    TopicConfig topicConfig = new TopicConfig("topicName", new ArrayList<EventConfig>());

    assertFalse("topic config should be applicable to the event",topicConfig.isApplicable(getEvent("OE_ASSESS", "differentTopicName", DEFAULT_TIME)));
  }

  private Event getEvent(String eid, String source, String time) {
    HashMap<String, Object> eventMap = new HashMap<String, Object>();
    eventMap.put("eid", eid);
    Telemetry telemetry = new Telemetry(eventMap);
    telemetry.add("ts", time);

    return new Event(telemetry, source);
  }

}