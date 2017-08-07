package org.ekstep.ep.samza.config;

import org.ekstep.ep.samza.config.EsIndexDateConfig;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.reader.Telemetry;
import org.junit.Test;

import java.text.ParseException;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by aks on 01/08/17.
 */
public class EsIndexDateConfigTest {

  @Test
  public void shouldGetTheIndexBasedOnPrimaryDateField() throws ParseException {
    EsIndexDateConfig esIndexDateConfig = new EsIndexDateConfig("ts","string","ets","epoch", true);
    Telemetry telemetry = new Telemetry(new HashMap<String, Object>());
    telemetry.add("ts","2017-07-01T05:24:47.105+0000");
    Event event = new Event(telemetry,"kafkaSource");

    String esIndex = esIndexDateConfig.getIndex("ecosystem", event);

    assertEquals("ecosystem-2017.07",esIndex);
  }

  @Test
  public void shouldGetTheIndexBasedOnSecondaryDateField() throws ParseException {
    EsIndexDateConfig esIndexDateConfig = new EsIndexDateConfig("ts","string","ets","epoch", true);
    Telemetry telemetry = new Telemetry(new HashMap<String, Object>());
    telemetry.add("ets",1501565087105D);
    Event event = new Event(telemetry,"kafkaSource");

    String esIndex = esIndexDateConfig.getIndex("ecosystem", event);

    assertEquals("ecosystem-2017.08",esIndex);
  }

  //TODO: Need to mock calendar & date.
//  @Test
//  public void shouldGiveCurrentDateSecondaryDateFieldIsNotPresentAndPrimaryHasNoData() throws ParseException {
//    EsIndexDateConfig esIndexDate = new EsIndexDateConfig("ts","string","","", true);
//    Telemetry telemetry = new Telemetry(new HashMap<String, Object>());
//    telemetry.add("ets",1501565087105D);
//    Event event = new Event(telemetry,"kafkaSource");
//
//    String esIndex = esIndexDate.getIndex("ecosystem", event);
//
//    assertEquals("",esIndex);
//  }

}