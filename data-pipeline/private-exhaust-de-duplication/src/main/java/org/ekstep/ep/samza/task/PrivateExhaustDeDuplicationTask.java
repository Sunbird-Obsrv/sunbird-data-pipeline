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
import org.ekstep.ep.samza.service.PrivateExhaustDeDuplicationService;

import java.util.Map;

public class PrivateExhaustDeDuplicationTask implements StreamTask, InitableTask, WindowableTask {
    static Logger LOGGER = new Logger(PrivateExhaustDeDuplicationTask.class);
    private PrivateExhaustDeDuplicationConfig config;
    private JobMetrics metrics;
    private PrivateExhaustDeDuplicationService service;

    public PrivateExhaustDeDuplicationTask(Config config, TaskContext context,
                                           KeyValueStore<Object, Object> privateExhaustStore, DeDupEngine deDupEngine) {
        init(config, context, privateExhaustStore, deDupEngine);
    }

    public PrivateExhaustDeDuplicationTask() {

    }

    @Override
    public void init(Config config, TaskContext context) throws Exception {
        init(config, context,
                (KeyValueStore<Object, Object>) context.getStore("private-exhaust"), null);
    }

    private void init(Config config, TaskContext context,
                      KeyValueStore<Object, Object> privateExhaustStore,DeDupEngine deDupEngine) {
        this.config = new PrivateExhaustDeDuplicationConfig(config);
        metrics = new JobMetrics(context,this.config.jobName());
        deDupEngine = deDupEngine == null ? new DeDupEngine(privateExhaustStore) : deDupEngine;
        service = new PrivateExhaustDeDuplicationService(deDupEngine);
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector,
                        TaskCoordinator taskCoordinator) throws Exception {
        PrivateExhaustDeDuplicationSource source = new PrivateExhaustDeDuplicationSource(envelope);
        PrivateExhaustDeDuplicationSink sink = new PrivateExhaustDeDuplicationSink(collector, metrics, config);

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
