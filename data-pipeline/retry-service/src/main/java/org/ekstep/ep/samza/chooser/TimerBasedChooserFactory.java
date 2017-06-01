package org.ekstep.ep.samza.chooser;


import org.apache.samza.config.Config;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.system.chooser.MessageChooser;
import org.apache.samza.system.chooser.MessageChooserFactory;
import org.apache.samza.system.chooser.RoundRobinChooserMetrics;

public class TimerBasedChooserFactory implements MessageChooserFactory {
    @Override
    public MessageChooser getChooser(Config config, MetricsRegistry registry) {
        RoundRobinChooserMetrics metrics = new RoundRobinChooserMetrics(registry);
        String stream = config.get("task.consumer.preferred");
        int delayInMilliSeconds = Integer.parseInt(config.get("task.consumer.delayInMilliSeconds", "5"));
        int retryTimeInMilliSeconds = Integer.parseInt(config.get("task.consumer.retryTimeInMilliSeconds", "5"));
        return new TimerBasedChooser(delayInMilliSeconds, retryTimeInMilliSeconds, stream);
    }
}
