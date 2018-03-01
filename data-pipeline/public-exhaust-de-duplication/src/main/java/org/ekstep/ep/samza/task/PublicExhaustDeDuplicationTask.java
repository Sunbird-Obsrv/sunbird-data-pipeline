package org.ekstep.ep.samza.task;

import com.google.gson.Gson;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.dedup.DeDupEngine;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.metrics.JobMetrics;
import org.ekstep.ep.samza.service.PublicExhaustDeDuplicationService;

import java.util.Map;

public class PublicExhaustDeDuplicationTask implements StreamTask, InitableTask, WindowableTask {
    static Logger LOGGER = new Logger(PublicExhaustDeDuplicationTask.class);
    private PublicExhaustDeDuplicationConfig config;
    private JobMetrics metrics;
    private PublicExhaustDeDuplicationService service;

    public PublicExhaustDeDuplicationTask(Config config, TaskContext context,
                                          KeyValueStore<Object, Object> publicExhaustStore, DeDupEngine deDupEngine) {
        init(config, context, publicExhaustStore, deDupEngine);
    }

    public PublicExhaustDeDuplicationTask() {

    }

    @Override
    public void init(Config config, TaskContext context) throws Exception {
        init(config, context,
                (KeyValueStore<Object, Object>) context.getStore("public-exhaust"), null);
    }

    private void init(Config config, TaskContext context,
                      KeyValueStore<Object, Object> publicExhaustStore, DeDupEngine deDupEngine) {
        this.config = new PublicExhaustDeDuplicationConfig(config);
        metrics = new JobMetrics(context, this.config.jobName());
        deDupEngine = deDupEngine == null ? new DeDupEngine(publicExhaustStore) : deDupEngine;
        service = new PublicExhaustDeDuplicationService(deDupEngine);
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector,
                        TaskCoordinator taskCoordinator) throws Exception {
        PublicExhaustDeDuplicationSource source = new PublicExhaustDeDuplicationSource(envelope);
        PublicExhaustDeDuplicationSink sink = new PublicExhaustDeDuplicationSink(collector, metrics, config);

        service.process(source, sink);
    }

    @Override
    public void window(MessageCollector collector, TaskCoordinator taskCoordinator) throws Exception {
        String mEvent = metrics.collect();
        Map<String,Object> mEventMap = new Gson().fromJson(mEvent,Map.class);
        collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", config.metricsTopic()), mEventMap));
        metrics.clear();
    }
}
