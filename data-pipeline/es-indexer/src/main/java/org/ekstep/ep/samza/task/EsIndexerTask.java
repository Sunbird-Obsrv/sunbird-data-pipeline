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
import org.apache.samza.task.*;
import org.ekstep.ep.samza.dedup.DeDupEngine;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.metrics.JobMetrics;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;

import java.io.IOException;
import java.util.Map;

public class EsIndexerTask implements StreamTask, InitableTask, WindowableTask {


    static Logger LOGGER = new Logger(EsIndexerTask.class);
    private EsIndexerConfig config;
    private JobMetrics metrics;

    public EsIndexerTask(Config config, TaskContext context,
                         KeyValueStore<Object, Object> deDuplicationStore, DeDupEngine deDupEngine) {
        init(config, context, deDuplicationStore, deDupEngine);
    }

    public EsIndexerTask(){

    }

    @Override
    public void init(Config config, TaskContext context) throws Exception{
        init(config, context,
                (KeyValueStore<Object, Object>) context.getStore("es-indexer"), null);
    }

    private void init(Config config, TaskContext context,
                      KeyValueStore<Object, Object> deDuplicationStore, DeDupEngine deDupEngine) {
//        metrics = new JobMetrics(context);
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector,
                        TaskCoordinator taskCoordinator) throws Exception {

        Telemetry telemetry = new Telemetry((Map<String, Object>) envelope.getMessage());
        NullableValue<String> mid = telemetry.read("mid");
        NullableValue<String> eid = telemetry.read("eid");
        dumpToFile(mid,eid);
    }

    private void dumpToFile(NullableValue<String> mid, NullableValue<String> eid) throws IOException {
        String midValue = mid.isNull()? "mid is missing": mid.value();
        String eidValue = eid.isNull()? "eid is missing": eid.value();
        LOGGER.info(eidValue,String.format("MID:%s",midValue));
    }

    @Override
    public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
//        metrics.clear();
    }
}
