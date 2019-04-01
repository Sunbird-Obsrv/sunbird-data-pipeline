package org.ekstep.ep.samza.fixtures;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.samza.metrics.Counter;
import org.apache.samza.metrics.Metric;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MetricsFixture {
	public static final String METRIC_EVENT = "{\n" +
			"  \"org.apache.samza.system.kafka.KafkaSystemConsumerMetrics\": {\n" +
			"    \"kafka-inputtopic1-1-offset-change\": {\n" +
			"      \"name\": \"kafka-inputtopic1-1-offset-change\",\n" +
			"      \"count\": {\n" +
			"        \"value\": 0\n" +
			"      }\n" +
			"    }\n" +
			"  }\n" +
			"}";

	public static final String METRIC_EVENT_STREAM = "{\n" +
			"  \"org.apache.samza.system.kafka.KafkaSystemConsumerMetrics\": {\n" +
			"    \"kafka-inputtopic1-1-offset-change\": {\n" +
			"      \"name\": \"kafka-inputtopic1-1-offset-change\",\n" +
			"      \"count\": {\n" +
			"        \"value\": 6\n" +
			"      }\n" +
			"    },\n" +
			"    \"kafka-inputtopic2-1-offset-change\": {\n" +
			"      \"name\": \"kafka-inputtopic2-1-offset-change\",\n" +
			"      \"count\": {\n" +
			"        \"value\": 5\n" +
			"      }\n" +
			"    }\n" +
			"  }\n" +
			"}";


	public static Map<String, ConcurrentHashMap<String, Metric>> getMetricMap(String message) {
		Type type = new TypeToken<Map<String, ConcurrentHashMap<String, Counter>>>() {
		}.getType();
		return (Map<String, ConcurrentHashMap<String, Metric>>) new Gson().fromJson(message, type);
	}


}
