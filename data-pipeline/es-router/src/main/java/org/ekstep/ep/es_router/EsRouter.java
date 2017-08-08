package org.ekstep.ep.es_router;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.ekstep.ep.es_router.domain.Event;
import org.ekstep.ep.es_router.config.EsIndexerConfig;
import org.ekstep.ep.samza.reader.Telemetry;
import java.io.FileReader;
import java.io.IOException;

public class EsRouter {
  private String configPath;
  private EsIndexerConfig config;

  public EsRouter(String configPath) {
    this.configPath = configPath;
  }

  public void loadConfiguration() throws IOException {
    String configJson = IOUtils.toString(new FileReader(configPath));
    config = new Gson().fromJson(configJson, EsIndexerConfig.class);
  }

  public void updateEsIndex(Telemetry telemetry, String source){
    Event event = new Event(telemetry, source);
    config.updateEsIndex(event);

  }
}
