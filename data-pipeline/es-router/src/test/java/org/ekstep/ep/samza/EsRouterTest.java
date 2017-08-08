package org.ekstep.ep.samza;

import org.ekstep.ep.es_router.EsRouter;
import org.ekstep.ep.samza.reader.Telemetry;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by aks on 01/08/17.
 */

public class EsRouterTest {
  private String additionalConfigFile = System.getProperty("user.dir") + "/src/main/resources/es-router-additional-config.json";

  @Test
  public void shouldIndexGeEventsBasedOnTs() throws IOException {
    EsRouter esRouter = new EsRouter(additionalConfigFile);
    esRouter.loadConfiguration();
    Telemetry telemetry = new Telemetry(new HashMap<String, Object>());
    telemetry.add("eid","GE_END");
    telemetry.add("ts","2017-07-21T05:24:47.105+0000");

    esRouter.updateEsIndex(telemetry,"telemetry.de_normalized");

    assertEquals("ecosystem-2017.07",telemetry.read("metadata.index_name").value());
    assertEquals("events_v1",telemetry.read("metadata.index_type").value());

  }

  @Test
  public void shouldIndexGeEventsBasedOnEts() throws IOException {
    EsRouter esRouter = new EsRouter(additionalConfigFile);
    esRouter.loadConfiguration();
    Telemetry telemetry = new Telemetry(new HashMap<String, Object>());
    telemetry.add("eid","GE_END");
    telemetry.add("ets",1501565087105D);

    esRouter.updateEsIndex(telemetry,"telemetry.de_normalized");

    assertEquals("ecosystem-2017.08",telemetry.read("metadata.index_name").value());
    assertEquals("events_v1",telemetry.read("metadata.index_type").value());

  }

  @Test
  public void shouldIndexOeEventsBasedOnTs() throws IOException {
    EsRouter esRouter = new EsRouter(additionalConfigFile);
    esRouter.loadConfiguration();
    Telemetry telemetry = new Telemetry(new HashMap<String, Object>());
    telemetry.add("eid","OE_INTERACT");
    telemetry.add("ts","2017-07-21T05:24:47.105+0000");

    esRouter.updateEsIndex(telemetry,"telemetry.de_normalized");

    assertEquals("ecosystem-2017.07",telemetry.read("metadata.index_name").value());
    assertEquals("events_v1",telemetry.read("metadata.index_type").value());

  }

  @Test
  public void shouldIndexOeEventsBasedOnEts() throws IOException {
    EsRouter esRouter = new EsRouter(additionalConfigFile);
    esRouter.loadConfiguration();
    Telemetry telemetry = new Telemetry(new HashMap<String, Object>());
    telemetry.add("eid","GE_INTERACT");
    telemetry.add("ets",1501565087105D);

    esRouter.updateEsIndex(telemetry,"telemetry.de_normalized");

    assertEquals("ecosystem-2017.08",telemetry.read("metadata.index_name").value());
    assertEquals("events_v1",telemetry.read("metadata.index_type").value());

  }

  @Test
  public void shouldIndexCpEventsBasedOnTs() throws IOException {
    EsRouter esRouter = new EsRouter(additionalConfigFile);
    esRouter.loadConfiguration();
    Telemetry telemetry = new Telemetry(new HashMap<String, Object>());
    telemetry.add("eid","CP_IMPRESSION");
    telemetry.add("ts","2017-07-21T05:24:47.105+0000");

    esRouter.updateEsIndex(telemetry,"telemetry.de_normalized");

    assertEquals("backend-2017.07",telemetry.read("metadata.index_name").value());
    assertEquals("backend",telemetry.read("metadata.index_type").value());

  }

  @Test
  public void shouldIndexCpEventsBasedOnEts() throws IOException {
    EsRouter esRouter = new EsRouter(additionalConfigFile);
    esRouter.loadConfiguration();
    Telemetry telemetry = new Telemetry(new HashMap<String, Object>());
    telemetry.add("eid","CP_SESSION_START");
    telemetry.add("ets",1501565087105D);

    esRouter.updateEsIndex(telemetry,"telemetry.de_normalized");

    assertEquals("backend-2017.08",telemetry.read("metadata.index_name").value());
    assertEquals("backend",telemetry.read("metadata.index_type").value());
  }

  @Test
  public void shouldIndexCeEventsBasedOnTs() throws IOException {
    EsRouter esRouter = new EsRouter(additionalConfigFile);
    esRouter.loadConfiguration();
    Telemetry telemetry = new Telemetry(new HashMap<String, Object>());
    telemetry.add("eid","CE_INTERACT");
    telemetry.add("ts","2017-07-21T05:24:47.105+0000");

    esRouter.updateEsIndex(telemetry,"telemetry.de_normalized");

    assertEquals("backend-2017.07",telemetry.read("metadata.index_name").value());
    assertEquals("backend",telemetry.read("metadata.index_type").value());
  }

  @Test
  public void shouldIndexCeEventsBasedOnEts() throws IOException {
    EsRouter esRouter = new EsRouter(additionalConfigFile);
    esRouter.loadConfiguration();
    Telemetry telemetry = new Telemetry(new HashMap<String, Object>());
    telemetry.add("eid","CE_API_CALL");
    telemetry.add("ets",1501565087105D);

    esRouter.updateEsIndex(telemetry,"telemetry.de_normalized");

    assertEquals("backend-2017.08",telemetry.read("metadata.index_name").value());
    assertEquals("backend",telemetry.read("metadata.index_type").value());
  }

  @Test
  public void shouldIndexBeEventsBasedOnTs() throws IOException {
    EsRouter esRouter = new EsRouter(additionalConfigFile);
    esRouter.loadConfiguration();
    Telemetry telemetry = new Telemetry(new HashMap<String, Object>());
    telemetry.add("eid","BE_OBJECT_LIFECYCLE");
    telemetry.add("ts","2017-07-21T05:24:47.105+0000");

    esRouter.updateEsIndex(telemetry,"telemetry.de_normalized");

    assertEquals("backend-2017.07",telemetry.read("metadata.index_name").value());
    assertEquals("backend",telemetry.read("metadata.index_type").value());
  }

  @Test
  public void shouldIndexBeEventsBasedOnEts() throws IOException {
    EsRouter esRouter = new EsRouter(additionalConfigFile);
    esRouter.loadConfiguration();
    Telemetry telemetry = new Telemetry(new HashMap<String, Object>());
    telemetry.add("eid","BE_OBJECT_LIFECYCLE");
    telemetry.add("ets",1501565087105D);

    esRouter.updateEsIndex(telemetry,"telemetry.de_normalized");

    assertEquals("backend-2017.08",telemetry.read("metadata.index_name").value());
    assertEquals("backend",telemetry.read("metadata.index_type").value());
  }

  @Test
  public void shouldIndexMeEventsBasedOnContextDateRangeTo() throws IOException {
    EsRouter esRouter = new EsRouter(additionalConfigFile);
    esRouter.loadConfiguration();
    Telemetry telemetry = new Telemetry(new HashMap<String, Object>());
    telemetry.add("learning","true");
    telemetry.add("context", new HashMap<String, Object>());
    telemetry.add("context.date_range",new HashMap<String,Object>());
    telemetry.add("context.date_range.to",1501565087105D);

    esRouter.updateEsIndex(telemetry,"telemetry.de_normalized");

    assertEquals("learning-2017.08",telemetry.read("metadata.index_name").value());
    assertEquals("events_v1",telemetry.read("metadata.index_type").value());
  }

  @Test
  public void shouldIndexMeEventsBasedOnCumulativeGranularity() throws IOException {
    EsRouter esRouter = new EsRouter(additionalConfigFile);
    esRouter.loadConfiguration();
    Telemetry telemetry = new Telemetry(new HashMap<String, Object>());
    telemetry.add("eid","ME_DEVICE_USAGE_SUMMARY");
    telemetry.add("learning","true");
    telemetry.add("context",new HashMap<String,Object>());
    telemetry.add("context.granularity","CUMULATIVE");

    esRouter.updateEsIndex(telemetry,"telemetry.de_normalized");

    assertEquals("learning-cumulative",telemetry.read("metadata.index_name").value());
    assertEquals("events_v1",telemetry.read("metadata.index_type").value());
  }
}