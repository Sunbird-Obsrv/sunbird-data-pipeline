package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.config.EsIndexDateConfig;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.reader.Telemetry;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.HashMap;

import static org.ekstep.ep.samza.util.Constants.DEFAULT_INDEX_TYPE;
import static org.junit.Assert.*;

/**
 * Created by aks on 31/07/17.
 */
public class EventTest {

  private EsIndexDateConfig indexDate;

  @Before
  public void setup(){
    indexDate = new EsIndexDateConfig("ts", "string", "", "", false);
  }

  @Test
  public void shouldAddMonthAndYearForNonCumulativeIndex() throws ParseException {
    Telemetry telemetry = new Telemetry(new HashMap<String, Object>());
    telemetry.add("ts","2017-07-21T05:24:47.105+0000");
    Event event = new Event(telemetry, "source");

    event.addEsIndex("ecosystem",false, indexDate, DEFAULT_INDEX_TYPE);

    assertEquals("ecosystem-2017.07",event.read("metadata.index_name").value());
    assertEquals("events_v1",event.read("metadata.index_type").value());
  }

  @Test
  public void shouldNotAddMonthAndYearForCumulativeIndex() throws ParseException {
    Telemetry telemetry = new Telemetry(new HashMap<String, Object>());
    telemetry.add("ts","2017-07-21T05:24:47.105+0000");
    Event event = new Event(telemetry, "source");

    event.addEsIndex("learning-cumulative",true, indexDate, DEFAULT_INDEX_TYPE);

    assertEquals("learning-cumulative",event.read("metadata.index_name").value());
    assertEquals("events_v1",event.read("metadata.index_type").value());
  }
}