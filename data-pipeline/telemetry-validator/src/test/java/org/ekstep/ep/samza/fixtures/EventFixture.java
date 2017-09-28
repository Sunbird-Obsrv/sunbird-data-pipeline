package org.ekstep.ep.samza.fixtures;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

public class EventFixture {

	public static final String VALID_GE_ERROR_EVENT = "{\"did\":\"00b09a9e-6af9-4bb7-b102-57380b43ddc8\",\"mid\":\"43288930-e54a-230b-b56e-876gnm8712ok\",\"edata\":{\"eks\":{\"data\":\"\",\"err\":\"10\",\"eventId\":\"GE_SIGNUP\",\"id\":\"2131165210\",\"type\":\"GENIE\"}},\"eid\":\"GE_ERROR\",\"gdata\":{\"id\":\"genie.android\",\"ver\":\"2.2.15\"},\"pdata\":{\"id\":\"genie\",\"ver\":\"2.0\"},\"sid\":\"\",\"ets\":1454064092546,\"uid\":\"\",\"ver\":\"2.2\",\"channel\":\"in.ekstep\",\"cdata\":[{\"id\":\"correlationid\",\"type\":\"correlationtype\"}]}";
	public static final String INVALID_GE_ERROR_EVENT = "{\"did\":\"00b09a9e-6af9-4bb7-b102-57380b43ddc8\",\"mid\":\"43288930-e54a-230b-b56e-876gnm8712ok\",\"eid\":\"GE_ERROR\",\"gdata\":{\"id\":\"genie.android\",\"ver\":\"2.2.15\"},\"pdata\":{\"id\":\"genie\",\"ver\":\"2.0\"},\"sid\":\"\",\"ets\":1454064092546,\"uid\":\"\",\"ver\":\"2.2\",\"channel\":\"in.ekstep\",\"cdata\":[{\"id\":\"correlationid\",\"type\":\"correlationtype\"}]}";
	public static final String UNPARSABLE_GE_GENIE_UPDATE_EVENT = "{\"did\":\"c270f15d-5230-4954-92aa-d239e4281cc4\",\"mid\":\"43288930-e54a-230b-b56e-876gnm8712ok\",\"edata\":{\"eks\":{\"mode\":\"WIFI\",\"ver\":\"12\",\"size\":12.67,\"err\":\"\",\"referrer\":[{\"action\":\"INSTALL\",\"utmsource\":\"Ekstep\",\"utmmedium\":\"Portal\",\"utmterm\":\"December 2016\",\"utmcontent\":\"Ramayana\",\"utmcampaign\":\"Epics of India\"}]}},\"eid\":\"GE_GENIE_UPDATE\",\"gdata\":{\"id\":\"genie.android\",\"ver\":\"1.0\"},\"sid\":\"\",\"ets\":1454064092546,\"uid\":\"\",\"ver\":\"2.0\",\"cdata\":[{\"id\":\"correlationid\",\"type\":\"correlationtype";
	public static final String VALID_GE_INTERACT_EVENT = "{\"cdata\":[],\"channel\":\"in.ekstep\",\"did\":\"0427fedf56eea1c8a127d876fd1907ffb245684f\",\"edata\":{\"eks\":{\"extype\":\"\",\"id\":\"\",\"pos\":[],\"stageid\":\"Genie-TelemetrySync\",\"subtype\":\"ManualSync-Success\",\"tid\":\"\",\"type\":\"OTHER\",\"uri\":\"\",\"values\":[{\"SizeOfFileInKB\":\"0.81\"}]}},\"eid\":\"GE_INTERACT\",\"etags\":{\"app\":[]},\"ets\":1.506328824238E12,\"gdata\":{\"id\":\"org.ekstep.genieservices.qa\",\"ver\":\"6.5.localdev-debug\"},\"mid\":\"375f4921-bf7b-45b0-8cf1-ba92d876a815\",\"pdata\":{\"id\":\"genie\",\"ver\":\"6.5.localdev-debug\"},\"sid\":\"e9662bb5-cf08-42d6-ad11-700b23566961\",\"ts\":\"2017-09-25T08:40:24.238+0000\",\"uid\":\"03762014-f67b-466b-bf20-467f46542563\",\"ver\":\"2.2\",\"@version\":\"1\",\"@timestamp\":\"2017-09-25T08:35:44.037Z\",\"metadata\":{\"checksum\":\"375f4921-bf7b-45b0-8cf1-ba92d876a815\"},\"uuid\":\"7f2c9f88-cbf7-4527-9f8d-667d4dde0d1c1\",\"key\":\"03762014-f67b-466b-bf20-467f46542563\",\"type\":\"events\",\"flags\":{\"dd_processed\":true}}";
	
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
