package org.ekstep.ep.samza.indexerDate;

import org.ekstep.ep.samza.Event;
import org.ekstep.ep.samza.reader.Telemetry;
import org.junit.Test;

import java.sql.Time;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by aks on 01/08/17.
 */
public class StringTimeParserTest {

  @Test
  public void shouldBeAbleToParseEventWithStringTime() {
    Telemetry telemetry = new Telemetry(new HashMap<String, Object>());
    telemetry.add("ts","2017-07-21T05:24:47.105+0000");
    Event event = new Event(telemetry,"kafkaSource");
    TimeParser timeParser = new StringTimeParser(event, "ts", "string");

    assertTrue("Should be able to parse string Time", timeParser.canParse());
  }

  @Test
  public void shouldNotBeAbleToParseEventWithEpochTime() {
    Telemetry telemetry = new Telemetry(new HashMap<String, Object>());
    telemetry.add("ets",1501565087105D);
    Event event = new Event(telemetry,"kafkaSource");
    TimeParser timeParser = new StringTimeParser(event, "ets", "epoch");

    assertFalse("Should not be able to parse epoch Time", timeParser.canParse());
  }

  @Test
  public void shouldNotBeAbleToParseEventWhenFieldIsMissing() {
    Telemetry telemetry = new Telemetry(new HashMap<String, Object>());
    Event event = new Event(telemetry,"kafkaSource");
    TimeParser timeParser = new StringTimeParser(event, "ts", "string");

    assertFalse("Should not throw exception when field is missing", timeParser.canParse());
  }

  @Test
  public void shouldGetDateFromEvent() throws ParseException {
    Telemetry telemetry = new Telemetry(new HashMap<String, Object>());
    telemetry.add("ts","2017-07-21T05:24:47.105+0000");
    Event event = new Event(telemetry,"kafkaSource");

    TimeParser timeParser = new StringTimeParser(event, "ts", "string");

    assertEquals(telemetry.getTime("ts","yyyy-MM-dd'T'HH:mm:ss.SSSZ"),timeParser.parse());
  }

  //TODO: mock date
//  @Test
//  public void shouldGetTodayDateWhenEventDoesNotHaveTime() throws ParseException {
//    Telemetry telemetry = new Telemetry(new HashMap<String, Object>());
//    Event event = new Event(telemetry,"kafkaSource");
//
//    TimeParser timeParser = new StringTimeParser(event, "ets", "epoch");
//
//    assertEquals(new Date(),timeParser.parse());
//  }

}