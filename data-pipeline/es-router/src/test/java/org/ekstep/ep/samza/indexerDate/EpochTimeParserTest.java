package org.ekstep.ep.samza.indexerDate;

import org.ekstep.ep.es_router.domain.Event;
import org.ekstep.ep.es_router.indexerDate.EpochTimeParser;
import org.ekstep.ep.samza.reader.Telemetry;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by aks on 01/08/17.
 */
public class EpochTimeParserTest {

  @Test
  public void shouldBeAbleToParseEventWithEpochTime() {
    Telemetry telemetry = new Telemetry(new HashMap<String, Object>());
    telemetry.add("ets",1501565087105D);
    Event event = new Event(telemetry,"kafkaSource");
    EpochTimeParser timeParser = new EpochTimeParser(event, "ets", "epoch");

    assertTrue("Should be able to parse epoch Time", timeParser.canParse());
  }

  @Test
  public void shouldNotBeAbleToParseEventWithoutEpochTime() {
    Telemetry telemetry = new Telemetry(new HashMap<String, Object>());
    telemetry.add("ts","2017-07-21T05:24:47.105+0000");
    Event event = new Event(telemetry,"kafkaSource");
    EpochTimeParser timeParser = new EpochTimeParser(event, "ts", "string");

    assertFalse("Should not be able to parse string Time", timeParser.canParse());
  }

  @Test
  public void shouldNotBeAbleToParseEventWhenFieldIsMissing() {
    Telemetry telemetry = new Telemetry(new HashMap<String, Object>());
    Event event = new Event(telemetry,"kafkaSource");
    EpochTimeParser timeParser = new EpochTimeParser(event, "ts", "string");

    assertFalse("Should not throw exception when field is missing", timeParser.canParse());
  }

  @Test
  public void shouldGetDateFromEvent() throws ParseException {
    Telemetry telemetry = new Telemetry(new HashMap<String, Object>());
    telemetry.add("ets",1501565087105D);
    Event event = new Event(telemetry,"kafkaSource");

    EpochTimeParser timeParser = new EpochTimeParser(event, "ets", "epoch");

    assertEquals(new Date(1501565087105L),timeParser.parse());
  }

  @Test
  public void shouldGetLongDateFromEvent() throws ParseException {
    Telemetry telemetry = new Telemetry(new HashMap<String, Object>());
    telemetry.add("ets",1501565087105L);
    Event event = new Event(telemetry,"kafkaSource");

    EpochTimeParser timeParser = new EpochTimeParser(event, "ets", "epoch");

    assertEquals(new Date(1501565087105L),timeParser.parse());
  }

  //TODO: mock Date
//  @Test
//  public void shouldGetTodayDateWhenEventDoesNotHaveTime() throws ParseException {
//    Telemetry telemetry = new Telemetry(new HashMap<String, Object>());
//    Event event = new Event(telemetry,"kafkaSource");
//
//    EpochTimeParser timeParser = new EpochTimeParser(event, "ets", "epoch");
//
//    assertEquals(new Date(),timeParser.parse());
//  }

}