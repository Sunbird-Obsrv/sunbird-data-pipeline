package org.ekstep.ep.samza.service;

import static java.text.MessageFormat.format;

import java.util.Map;

import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.task.TelemetryRedacterSink;
import org.ekstep.ep.samza.task.TelemetryRedacterSource;
import org.ekstep.ep.samza.util.QuestionDataCache;

public class TelemetryRedacterService {

  private static Logger LOGGER = new Logger(TelemetryRedacterService.class);
  private QuestionDataCache cache = null;
  private JobMetrics metrics = null;

  public TelemetryRedacterService(QuestionDataCache cache, JobMetrics metrics) {
    this.cache = cache;
    this.metrics = metrics;
  }

  public void process(TelemetryRedacterSource source, TelemetryRedacterSink sink) {
    Event event = null;
    try {
      boolean redact = false;
      event = source.getEvent();
      String questionId = event.questionId();
      if (null == questionId) {
        metrics.incSkippedCounter();
      } else {
        Map<String, Object> questionData = cache.getData(event.questionId());
        if (null == questionData) {
          metrics.incCacheMissCounter();
        } else {
          metrics.incCacheHitCounter();
          if (questionData.containsKey("questionType")
              && "Registration".equalsIgnoreCase((String) questionData.get("questionType"))) {
            redact = true;
          }
        }
      }
      if (redact) {
        sink.toNonRedactedRoute(event);
        event.clearUserInput();
      }
      sink.toRedactedRoute(event);
    } catch (Exception e) {
      LOGGER.error(null,
          format("EXCEPTION. PASSING EVENT THROUGH AND ADDING IT TO EXCEPTION TOPIC. EVENT: {0}, EXCEPTION:", event),
          e);
      sink.toErrorTopic(source.getMessage());
    }
  }
}
