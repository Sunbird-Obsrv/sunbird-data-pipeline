package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;

public class TelemetryConverterTaskTest {
    @Test
    public void process() throws Exception {
        TaskContext context = mock(TaskContext.class);
        Config config = mock(Config.class);
        IncomingMessageEnvelope envelope = mock(IncomingMessageEnvelope.class);
        MessageCollector collector = mock(MessageCollector.class);
        TaskCoordinator coordinator = mock(TaskCoordinator.class);
        Counter counter = mock(Counter.class);
        MetricsRegistry metricsRegistry = mock(MetricsRegistry.class);
        stub(context.getMetricsRegistry()).toReturn(metricsRegistry);
        stub(metricsRegistry.newCounter(anyString(), anyString()))
                .toReturn(counter);
        TelemetryConverterTask task = new TelemetryConverterTask(config, context);
        task.process(envelope, collector, coordinator);
    }

}