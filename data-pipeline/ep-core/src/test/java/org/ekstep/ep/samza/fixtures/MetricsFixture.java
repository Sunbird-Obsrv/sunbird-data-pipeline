package org.ekstep.ep.samza.fixtures;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.samza.metrics.Counter;
import org.apache.samza.metrics.Metric;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MetricsFixture {

	public static final String METRIC_EVENT_STREAM1 = "{\n" +
			"  \"org.apache.samza.system.kafka.KafkaSystemConsumerMetrics\": {\n" +
			"    \"kafka-inputtopic-0-high-watermark\": {\n" +
			"      \"name\": \"kafka-inputtopic-0-high-watermark\",\n" +
			"      \"count\": {\n" +
			"        \"value\": 1000\n" +
			"      }\n" +
			"    }\n" +
			"  }, \n" +
			"  \"org.apache.samza.checkpoint.OffsetManagerMetrics\": {\n" +
			"    \"kafka-inputtopic-0-checkpointed-offset\": {\n" +
			"      \"name\": \"kafka-inputtopic-0-checkpointed-offset\",\n" +
			"      \"count\": {\n" +
			"        \"value\": 200\n" +
			"      }\n" +
			"    }\n" +
			"  }\n" +
			"}";

	public static final String METRIC_EVENT_STREAM2 = "{\n" +
			"  \"org.apache.samza.system.kafka.KafkaSystemConsumerMetrics\": {\n" +
			"    \"kafka-inputtopic-0-high-watermark\": {\n" +
			"      \"name\": \"kafka-inputtopic-0-high-watermark\",\n" +
			"      \"count\": {\n" +
			"        \"value\": 1000\n" +
			"      }\n" +
			"    }\n" +
			"  }, \n" +
			"  \"org.apache.samza.checkpoint.OffsetManagerMetrics\": {\n" +
			"    \"kafka-inputtopic-0-checkpointed-offset\": {\n" +
			"      \"name\": \"kafka-inputtopic-0-checkpointed-offset\",\n" +
			"      \"count\": {\n" +
			"        \"value\": 1000\n" +
			"      }\n" +
			"    }\n" +
			"  }\n" +
			"}";


	public static Map<String, ConcurrentHashMap<String, Metric>> getMetricMap(String message) {
		Type type = new TypeToken<Map<String, ConcurrentHashMap<String, Counter>>>() {}.getType();
		return (Map<String, ConcurrentHashMap<String, Metric>>) new Gson().fromJson(message, type);
	}


}
