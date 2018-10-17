package org.ekstep.ep.samza.task;

import org.apache.samza.task.MessageCollector;
import org.ekstep.ep.samza.core.BaseSink;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Event;

import static java.text.MessageFormat.format;

public class TelemetryValidatorSink extends BaseSink {
    
    private JobMetrics metrics;
    private TelemetryValidatorConfig config;
    static Logger LOGGER = new Logger(TelemetryValidatorSink.class);

    public TelemetryValidatorSink(MessageCollector collector, JobMetrics metrics,
                                  TelemetryValidatorConfig config) {
        
    	super(collector);
        this.metrics = metrics;
        this.config = config;
    }

    public void toSuccessTopic(Event event) {
        toTopic(config.successTopic(), event.mid(), event.getJson());
        metrics.incSuccessCounter();
    }

    public void toFailedTopic(Event event, String failedMessage) {
    	event.markFailure(failedMessage, config);
        try {
            toTopic(config.failedTopic(), event.mid(), event.getJson());
        } catch (Exception ex) {
            LOGGER.error(null, format(
                    "EXCEPTION. PASSING EVENT MID {0} THROUGH FROM FAILED TOPIC. EXCEPTION: ",
                    event.mid()), ex.getMessage());
        }
        metrics.incFailedCounter();
    }

    public void toErrorTopic(Event event, String errorMessage) {
    	event.markFailure(errorMessage, config);
    	try {
            toTopic(config.failedTopic(), event.mid(), event.getJson());
        } catch (Exception ex) {
            LOGGER.error(null, format(
                    "EXCEPTION. PASSING EVENT MID {0} THROUGH FROM FAILED TOPIC. EXCEPTION: ",
                    event.mid()), ex.getMessage());
        }
        metrics.incErrorCounter();
    }

    public void toMalformedEventsTopic(String message) {
        toTopic(config.malformedTopic(), null, message);
        metrics.incFailedCounter();
    }
}
