package org.ekstep.ep.samza.fixtures;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

public class EventFixture {

	public static final String VALID_GE_ERROR_EVENT = "{\n" +
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
            "  \"eid\": \"GE_ERROR\",\n" +
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
	public static final String INVALID_GE_ERROR_EVENT = "{\n" +
            "  \"did\": \"00b09a9e-6af9-4bb7-b102-57380b43ddc8\",\n" +
            "  \"mid\": \"43288930-e54a-230b-b56e-876gnm8712ok\",\n" +
            "  \"eid\": \"GE_ERROR\",\n" +
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
	public static final String UNPARSABLE_GE_GENIE_UPDATE_EVENT = "{\n" +
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
            "  \"eid\": \"GE_GENIE_UPDATE\",\n" +
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
	public static final String VALID_GE_INTERACT_EVENT = "{\n" +
            "\"actor\": {\n" +
            "   \"type\": \"User\",\n" +
            "   \"id\": \"f:5a8a3f2b-3409-42e0-9001-f913bc0fde31:874ed8a5-782e-4f6c-8f36-e0288455901e\"\n" +
            "  }, \n" +
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

	public static final String SEARCH_EVENT_WITH_INCORRECT_DIALCODES_KEY = "{\n" +
            "  \"eid\": \"SEARCH\",\n" +
            "  \"ets\": 1.554241415598E12,\n" +
            "  \"ver\": \"3.0\",\n" +
            "  \"mid\": \"LP.1554241415598.bff9398b-a827-41ec-b1c9-e569546174b1\",\n" +
            "  \"actor\": {\n" +
            "    \"id\": \"org.ekstep.learning.platform\",\n" +
            "    \"type\": \"System\"\n" +
            "  },\n" +
            "  \"context\": {\n" +
            "    \"channel\": \"in.ekstep\",\n" +
            "    \"pdata\": {\n" +
            "      \"id\": \"prod.ntp.learning.platform\",\n" +
            "      \"pid\": \"search-service\",\n" +
            "      \"ver\": \"1.0\"\n" +
            "    },\n" +
            "    \"env\": \"search\"\n" +
            "  },\n" +
            "  \"edata\": {\n" +
            "    \"size\": 499413.0,\n" +
            "    \"query\": \"\",\n" +
            "    \"filters\": {\n" +
            "      \"dialCodes\": [\n" +
            "        \"ATAZRI\",\n" +
            "        \"CGSX7D\"\n" +
            "      ]\n" +
            "    },\n" +
            "    \"sort\": {\n" +
            "      \n" +
            "    },\n" +
            "    \"type\": \"all\",\n" +
            "    \"topn\": [\n" +
            "      {\n" +
            "        \"identifier\": \"do_31268582767737241615189\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"identifier\": \"do_31269107959395942417491\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"identifier\": \"do_31269108472948326417493\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"identifier\": \"do_31269113788995174417318\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"identifier\": \"do_31270597860728832015700\"\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"flags\": {\n" +
            "    \"tv_processed\": true,\n" +
            "    \"dd_processed\": true\n" +
            "  },\n" +
            "  \"type\": \"events\",\n" +
            "  \"syncts\": 1.55424141664E12,\n" +
            "  \"@timestamp\": \"2019-04-02T21:43:36.640Z\"\n" +
            "}";

	public static final String EVENT_WITH_EID_MISSING = "{\n" +
            "  \"ver\":\"3.0\",\n" +
            "  \"syncts\":0.0,\n" +
            "  \"ets\":0.0,\n" +
            "  \"tags\":[\n" +
            "\n" +
            "  ],\n" +
            "  \"context\":{\n" +
            "    \"channel\":\"01250894314817126443\"\n" +
            "  },\n" +
            "  \"ts\":\"1970-01-01T00:00:00.000+0000\",\n" +
            "  \"metadata\":{\n" +
            "    \"checksum\":\"10b890f9783ddcae406e7d6514d073eed4327941\",\n" +
            "    \"odn_status\":\"failed\",\n" +
            "    \"odn_error\":null\n" +
            "  }\n" +
            "}";
	
	public static final String ANY_STRING = "Hey Samza, Whats Up?";
	public static final String EMPTY_JSON = "{}";
	
    public static Map<String, Object> validGeErrorEventMap() {
        return new Gson().fromJson(VALID_GE_ERROR_EVENT, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static Map<String, Object> invalidGeErrorEventMap() {
        return new Gson().fromJson(INVALID_GE_ERROR_EVENT, new TypeToken<Map<String, Object>>() {
        }.getType());
    }
}
