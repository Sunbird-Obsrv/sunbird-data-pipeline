package org.ekstep.ep.samza.fixtures;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

public class EventFixture {

	public static final String LOG_EVENT = "{\n" +
            "  \"did\": \"00b09a9e-6af9-4bb7-b102-57380b43ddc8\",\n" +
            "  \"mid\": \"43288930-e54a-230b-b56e-876gnm8712ok\",\n" +
            "  \"edata\": {\n" +
            "    \"eks\": {\n" +
            "      \"data\": \"\",\n" +
            "      \"err\": \"10\",\n" +
            "      \"eventId\": \"GE_SIGNUP\",\n" +
            "      \"id\": \"2131165210\",\n" +
            "      \"type\": \"GENIE\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"eid\": \"LOG\",\n" +
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
            "  \"cdata\": [],\n" +
            "  \"channel\": \"in.ekstep\",\n" +
            "  \"did\": \"0427fedf56eea1c8a127d876fd1907ffb245684f\",\n" +
            "  \"edata\": {\n" +
            "    \"eks\": {\n" +
            "      \"extype\": \"\",\n" +
            "      \"id\": \"\",\n" +
            "      \"pos\": [],\n" +
            "      \"stageid\": \"Genie-TelemetrySync\",\n" +
            "      \"subtype\": \"ManualSync-Success\",\n" +
            "      \"tid\": \"\",\n" +
            "      \"type\": \"OTHER\",\n" +
            "      \"uri\": \"\",\n" +
            "      \"values\": [\n" +
            "        {\n" +
            "          \"SizeOfFileInKB\": \"0.81\"\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  },\n" +
            "  \"eid\": \"GE_INTERACT\",\n" +
            "  \"etags\": {\n" +
            "    \"app\": []\n" +
            "  },\n" +
            "  \"ets\": 1506328824238,\n" +
            "  \"gdata\": {\n" +
            "    \"id\": \"org.ekstep.genieservices.qa\",\n" +
            "    \"ver\": \"6.5.localdev-debug\"\n" +
            "  },\n" +
            "  \"mid\": \"375f4921-bf7b-45b0-8cf1-ba92d876a815\",\n" +
            "  \"pdata\": {\n" +
            "    \"id\": \"genie\",\n" +
            "    \"ver\": \"6.5.localdev-debug\"\n" +
            "  },\n" +
            "  \"sid\": \"e9662bb5-cf08-42d6-ad11-700b23566961\",\n" +
            "  \"ts\": \"2017-09-25T08:40:24.238+0000\",\n" +
            "  \"uid\": \"03762014-f67b-466b-bf20-467f46542563\",\n" +
            "  \"ver\": \"2.2\",\n" +
            "  \"@version\": \"1\",\n" +
            "  \"@timestamp\": \"2017-09-25T08:35:44.037Z\",\n" +
            "  \"metadata\": {\n" +
            "    \"checksum\": \"375f4921-bf7b-45b0-8cf1-ba92d876a815\"\n" +
            "  },\n" +
            "  \"uuid\": \"7f2c9f88-cbf7-4527-9f8d-667d4dde0d1c1\",\n" +
            "  \"key\": \"03762014-f67b-466b-bf20-467f46542563\",\n" +
            "  \"type\": \"events\",\n" +
            "  \"flags\": {\n" +
            "    \"dd_processed\": true\n" +
            "  }\n" +
            "}";

	public static final String AUDIT_EVENT ="{\n" +
			"  \"actor\": {\n" +
			"    \"type\": \"Consumer\",\n" +
			"    \"id\": \"89490534-126f-4f0b-82ac-3ff3e49f3468\"\n" +
			"  },\n" +
			"  \"eid\": \"AUDIT\",\n" +
			"  \"edata\": {\n" +
			"    \"state\": \"Create\",\n" +
			"    \"props\": [\n" +
			"      \"firstName\",\n" +
			"      \"email\",\n" +
			"      \"emailVerified\",\n" +
			"      \"id\",\n" +
			"      \"userId\",\n" +
			"      \"createdBy\",\n" +
			"      \"rootOrgId\",\n" +
			"      \"channel\",\n" +
			"      \"userType\",\n" +
			"      \"roles\",\n" +
			"      \"phoneVerified\",\n" +
			"      \"isDeleted\",\n" +
			"      \"createdDate\",\n" +
			"      \"status\",\n" +
			"      \"userName\",\n" +
			"      \"loginId\",\n" +
			"      \"externalIds\"\n" +
			"    ]\n" +
			"  },\n" +
			"  \"ver\": \"3.0\",\n" +
			"  \"ets\": 1561739260405,\n" +
			"  \"context\": {\n" +
			"    \"channel\": \"0126684405014528002\",\n" +
			"    \"pdata\": {\n" +
			"      \"pid\": \"learner-service\",\n" +
			"      \"ver\": \"2.0.0\",\n" +
			"      \"id\": \"prod.diksha.learning.service\"\n" +
			"    },\n" +
			"    \"env\": \"User\",\n" +
			"    \"cdata\": [\n" +
			"      {\n" +
			"        \"type\": \"User\",\n" +
			"        \"id\": \"cf2bdab7-0778-4b8e-bfa5-fa7df36a5d19\"\n" +
			"      },\n" +
			"      {\n" +
			"        \"type\": \"SignupType\",\n" +
			"        \"id\": \"google\"\n" +
			"      },\n" +
			"      {\n" +
			"        \"type\": \"Source\",\n" +
			"        \"id\": \"android\"\n" +
			"      },\n" +
			"      {\n" +
			"        \"type\": \"Request\",\n" +
			"        \"id\": \"a5f58660-99c1-11e9-8ef2-cdfa267be9f0\"\n" +
			"      }\n" +
			"    ],\n" +
			"    \"rollup\": {\n" +
			"      \"l1\": \"0126684405014528002\"\n" +
			"    }\n" +
			"  },\n" +
			"  \"mid\": \"1561739260405.1ab659ef-25b5-4a26-bd94-ff2daef86b62\",\n" +
			"  \"object\": {\n" +
			"    \"type\": \"User\",\n" +
			"    \"id\": \"cf2bdab7-0778-4b8e-bfa5-fa7df36a5d19\"\n" +
			"  },\n" +
			"  \"syncts\": 1561739269966,\n" +
			"  \"@timestamp\": \"2019-06-28T16:27:49.966Z\",\n" +
			"  \"flags\": {\n" +
			"    \"tv_processed\": true\n" +
			"  },\n" +
			"  \"type\": \"events\"\n" +
			"}";
	
	public static final String ANY_STRING = "Hey Samza, Whats Up?";
	public static final String EMPTY_JSON = "{}";
	
	public static Map<String, Object> getMap(String message) {
		return (Map<String, Object>) new Gson().fromJson(message, Map.class);
	}
	
}
