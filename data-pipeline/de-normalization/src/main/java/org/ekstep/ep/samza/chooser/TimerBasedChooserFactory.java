package org.ekstep.ep.samza.chooser;


import org.apache.samza.config.Config;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.system.chooser.*;
import scala.Int;

public class TimerBasedChooserFactory implements MessageChooserFactory {
    @Override
    public MessageChooser getChooser(Config config, MetricsRegistry registry) {
        RoundRobinChooserMetrics metrics = new RoundRobinChooserMetrics(registry);
        String stream = config.get("task.consumer.preferred");
        int delayInMinutes = Integer.parseInt(config.get("task.consumer.delayInMinutes", "5"));
        int retryTimeInMinutes = Integer.parseInt(config.get("task.consumer.retryTimeInMinutes", "5"));
        return new TimerBasedChooser(delayInMinutes,retryTimeInMinutes,stream);
    }
}
