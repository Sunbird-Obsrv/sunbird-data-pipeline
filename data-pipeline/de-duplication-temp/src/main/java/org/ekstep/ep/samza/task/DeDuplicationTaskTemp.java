/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.dedup.DeDupEngine;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.metrics.JobMetrics;
import org.ekstep.ep.samza.service.DeDuplicationServiceTemp;

public class DeDuplicationTaskTemp implements StreamTask, InitableTask, WindowableTask {


    static Logger LOGGER = new Logger(DeDuplicationTaskTemp.class);
    private DeDuplicationConfigTemp config;
    private JobMetrics metrics;
    private DeDuplicationServiceTemp service;

    public DeDuplicationTaskTemp(Config config, TaskContext context,
                                 KeyValueStore<Object, Object> deDuplicationStore, DeDupEngine deDupEngine) {
        init(config, context, deDuplicationStore, deDupEngine);
    }

    public DeDuplicationTaskTemp() {

    }

    @Override
    public void init(Config config, TaskContext context) {
        init(config, context,
                (KeyValueStore<Object, Object>) context.getStore("de-duplication"), null);
    }

    private void init(Config config, TaskContext context,
                      KeyValueStore<Object, Object> deDuplicationStore, DeDupEngine deDupEngine) {
        this.config = new DeDuplicationConfigTemp(config);
        metrics = new JobMetrics(context, this.config.jobName());
        deDupEngine = deDupEngine == null ? new DeDupEngine(deDuplicationStore) : deDupEngine;
        service = new DeDuplicationServiceTemp(deDupEngine,this.config);
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector,
                        TaskCoordinator taskCoordinator) {
        try {
            DeDuplicationSourceTemp source = new DeDuplicationSourceTemp(envelope);
            DeDuplicationSinkTemp sink = new DeDuplicationSinkTemp(collector, metrics, config);

            service.process(source, sink);
        }
        catch (Exception ex) {
            LOGGER.error("", "Deduplication failed: " + ex.getMessage());
            Object event = envelope.getMessage();
            if (event != null) {
                LOGGER.info("", "FAILED_EVENT: " + event);
            }
        }
    }

    @Override
    public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        String mEvent = metrics.collect();
        collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", config.metricsTopic()), mEvent));
        metrics.clear();
    }
}
