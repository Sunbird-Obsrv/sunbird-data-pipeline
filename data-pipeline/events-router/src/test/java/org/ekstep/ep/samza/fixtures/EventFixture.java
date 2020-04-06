package org.ekstep.ep.samza.fixtures;

import com.google.gson.Gson;

import java.util.Map;

public class EventFixture {

	public static final String WORKFLOW_SUMMARY_EVENT = "{\n" +
			"  \"eid\": \"ME_WORKFLOW_SUMMARY\",\n" +
			"  \"ets\": 1548040103586,\n" +
			"  \"syncts\": 1547991371195,\n" +
			"  \"mid\": \"837C68F76FB02D338E39DFBEE1095E3B\",\n" +
			"  \"context\": {\n" +
			"    \"pdata\": {\n" +
			"      \"id\": \"AnalyticsDataPipeline\",\n" +
			"      \"ver\": \"1.0\",\n" +
			"      \"model\": \"WorkflowSummarizer\"\n" +
			"    },\n" +
			"    \"granularity\": \"SESSION\",\n" +
			"    \"date_range\": {\n" +
			"      \"from\": 1547991074190,\n" +
			"      \"to\": 1547991170529\n" +
			"    }\n" +
			"  },\n" +
			"  \"dimensions\": {\n" +
			"    \"did\": \"e57185808603966cb58e18647bfc5f0de4e4d820\",\n" +
			"    \"pdata\": {\n" +
			"      \"id\": \"prod.diksha.app\",\n" +
			"      \"ver\": \"2.1.8\",\n" +
			"      \"pid\": \"sunbird.app\"\n" +
			"    },\n" +
			"    \"sid\": \"f962116f-3f84-446b-964c-fae92a15fefd\",\n" +
			"    \"channel\": \"505c7c48ac6dc1edc9b08f21db5a571d\",\n" +
			"    \"type\": \"session\"\n" +
			"  },\n" +
			"  \"edata\": {\n" +
			"    \"eks\": {\n" +
			"      \"interact_events_per_min\": 3.74,\n" +
			"      \"start_time\": 1547991074190,\n" +
			"      \"end_time\": 1547991170529,\n" +
			"      \"events_summary\": [\n" +
			"        { \"id\": \"START\", \"count\": 3 },\n" +
			"        { \"id\": \"IMPRESSION\", \"count\": 5 },\n" +
			"        { \"id\": \"END\", \"count\": 3 }\n" +
			"      ],\n" +
			"      \"page_summary\": [\n" +
			"        {\n" +
			"          \"id\": \"collection-detail\",\n" +
			"          \"type\": \"detail\",\n" +
			"          \"env\": \"home\",\n" +
			"          \"time_spent\": 13.45,\n" +
			"          \"visit_count\": 1\n" +
			"        },\n" +
			"        {\n" +
			"          \"id\": \"library\",\n" +
			"          \"type\": \"search\",\n" +
			"          \"env\": \"home\",\n" +
			"          \"time_spent\": 28.09,\n" +
			"          \"visit_count\": 1\n" +
			"        }\n" +
			"      ],\n" +
			"      \"time_diff\": 96.34,\n" +
			"      \"telemetry_version\": \"3.0\",\n" +
			"      \"env_summary\": [\n" +
			"        {\n" +
			"          \"env\": \"home\",\n" +
			"          \"time_spent\": 41.54,\n" +
			"          \"count\": 1\n" +
			"        }\n" +
			"      ],\n" +
			"      \"time_spent\": 96.36\n" +
			"    }\n" +
			"  }\n" +
			"}";

	public static final String ERROR_EVENT = "{\n" +
			"  \"did\": \"00b09a9e-6af9-4bb7-b102-57380b43ddc8\",\n" +
			"  \"mid\": \"43288930-e54a-230b-b56e-876gnm8712ok\",\n" +
			"  \"eid\": \"ERROR\",\n" +
			"  \"gdata\": {\n" +
			"    \"id\": \"genie.android\",\n" +
			"    \"ver\": \"2.2.15\"\n" +
			"  },\n" +
			"  \"pdata\": {\n" +
			"    \"id\": \"genie\",\n" +
			"    \"ver\": \"2.0\"\n" +
			"  },\n" +
			"  \"sid\": \"\",\n" +
			"  \"ets\": 1454064092546,\n" +
			"  \"uid\": \"\",\n" +
			"  \"ver\": \"2.2\",\n" +
			"  \"channel\": \"in.ekstep\",\n" +
			"  \"cdata\": [\n" +
			"    {\n" +
			"      \"id\": \"correlationid\",\n" +
			"      \"type\": \"correlationtype\"\n" +
			"    }\n" +
			"  ]\n" +
			"}";
	public static final String UNPARSABLE_START_EVENT = "{\n" +
			"  \"did\": \"c270f15d-5230-4954-92aa-d239e4281cc4\",\n" +
			"  \"mid\": \"43288930-e54a-230b-b56e-876gnm8712ok\",\n" +
			"  \"edata\": {\n" +
			"    \"eks\": {\n" +
			"      \"mode\": \"WIFI\",\n" +
			"      \"ver\": \"12\",\n" +
			"      \"size\": 12.67,\n" +
			"      \"err\": \"\",\n" +
			"      \"referrer\": [\n" +
			"        {\n" +
			"          \"action\": \"INSTALL\",\n" +
			"          \"utmsource\": \"Ekstep\",\n" +
			"          \"utmmedium\": \"Portal\",\n" +
			"          \"utmterm\": \"December 2016\",\n" +
			"          \"utmcontent\": \"Ramayana\",\n" +
			"          \"utmcampaign\": \"Epics of India\"\n" +
			"        }\n" +
			"      ]\n" +
			"    }\n" +
			"  },\n" +
			"  \"eid\": \"START\",\n" +
			"  \"gdata\": {\n" +
			"    \"id\": \"genie.android\",\n" +
			"    \"ver\": \"1.0\"\n" +
			"  },\n" +
			"  \"sid\": \"\",\n" +
			"  \"ets\": 1454064092546,\n" +
			"  \"uid\": \"\",\n" +
			"  \"ver\": \"2.0\",\n" +
			"  \"cdata\": [\n" +
			"    {\n" +
			"      \"id\": \"correlationid\",\n" +
			"      \"type\": \"correlationtype\"\n" +
			"    ";
	public static final String START_EVENT = "{\n" +
			"  \"actor\": {\n" +
			"    \"type\": \"User\",\n" +
			"    \"id\": \"3ae5dc29-17ea-4465-800e-a4361b9d9604\"\n" +
			"  },\n" +
			"  \"edata\": {\n" +
			"    \"type\": \"app\"\n" +
			"  },\n" +
			"  \"eid\": \"START\",\n" +
			"  \"ver\": \"3.0\",\n" +
			"  \"ets\": 1548720004773,\n" +
			"  \"context\": {\n" +
			"    \"pdata\": {\n" +
			"      \"ver\": \"2.1.30\",\n" +
			"      \"pid\": \"sunbird.app\",\n" +
			"      \"id\": \"prod.diksha.app\"\n" +
			"    },\n" +
			"    \"channel\": \"505c7c48ac6dc1edc9b08f21db5a571d\",\n" +
			"    \"env\": \"home\",\n" +
			"    \"did\": \"28b3576420784da6a7b556c1966c759fc793ac0d\",\n" +
			"    \"sid\": \"042d7f61-225b-4e3f-9fe4-93f373368d8f\",\n" +
			"    \"cdata\": []\n" +
			"  },\n" +
			"  \"mid\": \"b8b0f2ca-ce0a-427d-9c47-195eb5424071\",\n" +
			"  \"object\": {\n" +
			"    \"id\": \"\",\n" +
			"    \"type\": \"\",\n" +
			"    \"version\": \"\"\n" +
			"  },\n" +
			"  \"tags\": [],\n" +
			"  \"syncts\": 1548720010819,\n" +
			"  \"@timestamp\": \"2019-01-29T00:00:10.819Z\",\n" +
			"  \"flags\": {\n" +
			"    \"tv_processed\": true,\n" +
			"    \"dd_processed\": true\n" +
			"  },\n" +
			"  \"type\": \"events\"\n" +
			"}";
	public static final String EVENT_WITH_NULL_EID = "{\n" +
			"  \"actor\": {\n" +
			"    \"type\": \"User\",\n" +
			"    \"id\": \"3ae5dc29-17ea-4465-800e-a4361b9d9604\"\n" +
			"  },\n" +
			"  \"edata\": {\n" +
			"    \"type\": \"app\"\n" +
			"  },\n" +
			"  \"ver\": \"3.0\",\n" +
			"  \"ets\": 1548720004773,\n" +
			"  \"context\": {\n" +
			"    \"pdata\": {\n" +
			"      \"ver\": \"2.1.30\",\n" +
			"      \"pid\": \"sunbird.app\",\n" +
			"      \"id\": \"prod.diksha.app\"\n" +
			"    },\n" +
			"    \"channel\": \"505c7c48ac6dc1edc9b08f21db5a571d\",\n" +
			"    \"env\": \"home\",\n" +
			"    \"did\": \"28b3576420784da6a7b556c1966c759fc793ac0d\",\n" +
			"    \"sid\": \"042d7f61-225b-4e3f-9fe4-93f373368d8f\",\n" +
			"    \"cdata\": []\n" +
			"  },\n" +
			"  \"mid\": \"b8b0f2ca-ce0a-427d-9c47-195eb5424071\",\n" +
			"  \"object\": {\n" +
			"    \"id\": \"\",\n" +
			"    \"type\": \"\",\n" +
			"    \"version\": \"\"\n" +
			"  },\n" +
			"  \"tags\": [],\n" +
			"  \"syncts\": 1548720010819,\n" +
			"  \"@timestamp\": \"2019-01-29T00:00:10.819Z\",\n" +
			"  \"flags\": {\n" +
			"    \"tv_processed\": true,\n" +
			"    \"dd_processed\": true\n" +
			"  },\n" +
			"  \"type\": \"events\"\n" +
			"}";

	public static final String ANY_STRING = "Hey Samza, Whats Up?";
	public static final String EMPTY_JSON = "{}";

	public static final String LOG_EVENT = "{\n"+
			"   \"@timestamp\":\"2019-03-20T00:00:01.176Z\",\n"+
			"\"actor\":{\n"+
			"\"id\":\"0b251080-3230-415e-a593-ab7c1fac7ae3\",\n"+
			"\"type\":\"User\"\n"+
			"},\n"+
			"\"context\":{\n"+
			"\"cdata\":[\n"+

			"],\n"+
			"\"channel\":\"505c7c48ac6dc1edc9b08f21db5a571d\",\n"+
			/*"\"did\":\"923c675274cfbf19fd0402fe4d2c37afd597f0ab\",\n"+*/
			"\"env\":\"home\",\n"+
			"\"pdata\":{\n"+
			"\"id\":\"prod.diksha.app\",\n"+
			"\"pid\":\"sunbird.app\",\n"+
			"\"ver\":\"2.1.45\"\n"+
			"},\n"+
			"\"sid\":\"57b5b7ea-93c5-48d6-ba51-f5f9a3570ffe\"\n"+
			"},\n"+
			"\"edata\":{\n"+
			"\"level\":\"INFO\",\n"+
			"\"message\":\"content-detail\",\n"+
			"\"params\":[\n"+
			"{\n"+
			"\"PopupType\":\"automatic\"\n"+
			"}\n"+
			"],\n"+
			"\"type\":\"view\"\n"+
			"},\n"+
			"\"eid\":\"LOG\",\n"+
			"\"ets\":1.553039987481E12,\n"+
			"\"flags\":{\n"+
			"\"dd_processed\":true,\n"+
			"\"tv_processed\":true\n"+
			"},\n"+
			"\"mid\":\"ca17e5bd-71d4-487a-92cd-0fb377e7a591\",\n"+
			"\"syncts\":1.553040001176E12,\n"+
			"\"tags\":[\n"+
			"],\n"+
			"\"type\":\"events\",\n"+
			"\"ver\":\"3.0\"\n"+
			"      }";
	public static final String TELEMETRY_ERROR_EVENT = "{"+
			"   \"eid\":\"ERROR\","+
			"   \"ets\":%s,"+
			"   \"ver\":\"3.0\","+
			"   \"mid\":\"LP.1553040097857.bf0e4e15-014e-4a22-ba00-e02ff3c38784\","+
			"   \"actor\":{"+
			"      \"id\":\"e85bcfb5-a8c2-4e65-87a2-0ebb43b45f01\","+
			"      \"type\":\"System\""+
			"   },"+
			"   \"context\":{"+
			"      \"channel\":\"01235953109336064029450\","+
			"      \"pdata\":{"+
			"         \"id\":\"prod.ntp.learning.platform\","+
			"         \"pid\":\"learning-service\","+
			"         \"ver\":\"1.0\""+
			"      },"+
			"      \"env\":\"framework\""+
			"   },"+
			"   \"edata\":{"+
			"      \"err\":\"ERR_DATA_NOT_FOUND\","+
			"      \"stacktrace\":\"ERR_DATA_NOT_FOUND: Data not found with id : null\n\tat\","+
			"      \"errtype\":\"system\""+
			"   },"+
			"   \"flags\":{"+
			"      \"tv_processed\":true,"+
			"      \"dd_processed\":true"+
			"   },"+
			"   \"type\":\"events\","+
			"   \"syncts\":1.553040098435E12,"+
			"   \"@timestamp\":\"2019-03-20T00:01:38.435Z\""+
			"}";

	public static final String TRACE_EVENT = "{"+
			"   \"eid\":\"TRACE\","+
			"   \"ets\":%s,"+
			"   \"ver\":\"3.0\","+
			"   \"mid\":\"TRACE.bf0e4e15-014e-4a22-ba00-e02ff3c38784\","+
			"   \"actor\":{"+
			"      \"id\":\"e85bcfb5-a8c2-4e65-87a2-0ebb43b45f01\","+
			"      \"type\":\"System\""+
			"   },"+
			"   \"context\":{"+
			"      \"channel\":\"01235953109336064029450\","+
			"      \"pdata\":{"+
			"         \"id\":\"prod.ntp.learning.platform\","+
			"         \"pid\":\"learning-service\","+
			"         \"ver\":\"1.0\""+
			"      },"+
			"      \"env\":\"framework\""+
			"   },"+
			"   \"edata\":{"+
			"      \"err\":\"ERR_DATA_NOT_FOUND\","+
			"      \"stacktrace\":\"ERR_DATA_NOT_FOUND: Data not found with id : null\n\tat\","+
			"      \"errtype\":\"system\""+
			"   },"+
			"   \"flags\":{"+
			"      \"tv_processed\":true,"+
			"      \"dd_processed\":true"+
			"   },"+
			"   \"type\":\"events\","+
			"   \"syncts\":1.553040098435E12,"+
			"   \"@timestamp\":\"2019-03-20T00:01:38.435Z\""+
			"}";

	public static final String WORKFLOW_USAGE_EVENT = "{\"eid\":\"ME_WORKFLOW_USAGE_SUMMARY\",\"ets\":1580174594135,\"syncts\":1580112438526,\"ver\":\"2.1\",\"mid\":\"7FA1DEB21833F00EE8AD66FF8AFF4589\",\"uid\":\"\",\"channel\":\"\",\"context\":{\"pdata\":{\"id\":\"AnalyticsDataPipeline\",\"ver\":\"1.0\",\"model\":\"WorkFlowUsageSummarizer\"},\"granularity\":\"DAY\",\"date_range\":{\"from\":1580108039040,\"to\":1580112356069}},\"dimensions\":{\"uid\":\"04b88827-87b1-40fd-a662-b1b97068d127\",\"did\":\"all\",\"pdata\":{\"id\":\"prod.diksha.app\",\"ver\":\"2.3.162\",\"pid\":\"sunbird.app\"},\"tag\":\"all\",\"period\":20200127,\"content_id\":\"all\",\"channel\":\"505c7c48ac6dc1edc9b08f21db5a571d\",\"type\":\"resource\",\"mode\":\"play\",\"content_type\":\"all\"},\"edata\":{\"eks\":{\"total_users_count\":0,\"total_devices_count\":1,\"total_content_count\":4,\"avg_ts_session\":4.88,\"total_sessions\":5,\"avg_interactions_min\":24.58,\"total_interactions\":10,\"avg_pageviews\":0.0,\"total_ts\":24.41,\"total_pageviews_count\":0}}}";
	public static Map<String, Object> getMap(String message) {
		return (Map<String, Object>) new Gson().fromJson(message, Map.class);
	}

}