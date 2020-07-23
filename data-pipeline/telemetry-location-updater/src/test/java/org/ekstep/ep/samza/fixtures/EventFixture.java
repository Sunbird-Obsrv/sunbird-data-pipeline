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

	public static final String INTERACT_EVENT = "{\n" +
			"    \"actor\": {\n" +
			"        \"type\": \"User\",\n" +
			"        \"id\": \"393407b1-66b1-4c86-9080-b2bce9842886\"\n" +
			"    },\n" +
			"    \"eid\": \"INTERACT\",\n" +
			"    \"edata\": {\n" +
			"		 \"loc\":\"xyz\",\n" +
			"        \"id\": \"ContentDetail\",\n" +
			"        \"pageid\": \"ContentDetail\",\n" +
			"        \"type\": \"TOUCH\",\n" +
			"        \"subtype\": \"ContentDownload-Initiate\"\n" +
			"    },\n" +
			"    \"ver\": \"3.0\",\n" +
			"    \"ets\": 1541574545180,\n" +
			"    \"context\": {\n" +
			"        \"pdata\": {\n" +
			"            \"ver\": \"2.1.8\",\n" +
			"            \"pid\": \"sunbird.app\",\n" +
			"            \"id\": \"prod.diksha.app\"\n" +
			"        },\n" +
			"        \"channel\": \"0123221617357783046602\",\n" +
			"        \"env\": \"sdk\",\n" +
			"        \"did\": \"68dfc64a7751ad47617ac1a4e0531fb761ebea6f\",\n" +
			"        \"cdata\": [\n" +
			"            {\n" +
			"                \"type\": \"qr\",\n" +
			"                \"id\": \"K4KCXE\"\n" +
			"            },\n" +
			"            {\n" +
			"                \"type\": \"API\",\n" +
			"                \"id\": \"f3ac6610-d218-11e8-b2bb-1598ac1fcb99\"\n" +
			"            }\n" +
			"        ],\n" +
			"        \"sid\": \"70ea93d0-e521-4030-934f-276e7194c225\"\n" +
			"    },\n" +
			"    \"mid\": \"e6a3bcd3-eb78-457b-8fc0-4acc94642ebf\",\n" +
			"    \"object\": {\n" +
			"        \"id\": \"do_31249561779090227216256\",\n" +
			"        \"type\": \"Content\",\n" +
			"        \"version\": \"\"\n" +
			"    },\n" +
			"    \"tags\": [\n" +
			"        \n" +
			"    ],\n" +
			"    \"syncts\": 1539846605341,\n" +
			"    \"@timestamp\": \"2018-10-18T07:10:05.341Z\"\n" +
			"}";

	public static final String INTERACT_EVENT_WITHOUT_DID = "{\n" +
			"    \"actor\": {\n" +
			"        \"type\": \"User\",\n" +
			"        \"id\": \"393407b1-66b1-4c86-9080-b2bce9842886\"\n" +
			"    },\n" +
			"    \"eid\": \"INTERACT\",\n" +
			"    \"edata\": {\n" +
			"		 \"loc\":\"xyz\",\n" +
			"        \"id\": \"ContentDetail\",\n" +
			"        \"pageid\": \"ContentDetail\",\n" +
			"        \"type\": \"TOUCH\",\n" +
			"        \"subtype\": \"ContentDownload-Initiate\"\n" +
			"    },\n" +
			"    \"ver\": \"3.0\",\n" +
			"    \"ets\": 1541574545180,\n" +
			"    \"context\": {\n" +
			"        \"pdata\": {\n" +
			"            \"ver\": \"2.1.8\",\n" +
			"            \"pid\": \"sunbird.app\",\n" +
			"            \"id\": \"prod.diksha.app\"\n" +
			"        },\n" +
			"        \"channel\": \"0123221617357783046602\",\n" +
			"        \"env\": \"sdk\",\n" +
			"        \"cdata\": [\n" +
			"            {\n" +
			"                \"type\": \"qr\",\n" +
			"                \"id\": \"K4KCXE\"\n" +
			"            },\n" +
			"            {\n" +
			"                \"type\": \"API\",\n" +
			"                \"id\": \"f3ac6610-d218-11e8-b2bb-1598ac1fcb99\"\n" +
			"            }\n" +
			"        ],\n" +
			"        \"sid\": \"70ea93d0-e521-4030-934f-276e7194c225\"\n" +
			"    },\n" +
			"    \"mid\": \"e6a3bcd3-eb78-457b-8fc0-4acc94642ebf\",\n" +
			"    \"object\": {\n" +
			"        \"id\": \"do_31249561779090227216256\",\n" +
			"        \"type\": \"Content\",\n" +
			"        \"version\": \"\"\n" +
			"    },\n" +
			"    \"tags\": [\n" +
			"        \n" +
			"    ],\n" +
			"    \"syncts\": 1539846605341,\n" +
			"    \"@timestamp\": \"2018-10-18T07:10:05.341Z\"\n" +
			"}";

	public static final String INTERACT_EVENT_WITH_ACTOR_AS_SYSTEM = "{\n" +
			"    \"actor\": {\n" +
			"        \"type\": \"System\",\n" +
			"        \"id\": \"393407b1-66b1-4c86-9080-b2bce9842886\"\n" +
			"    },\n" +
			"    \"eid\": \"INTERACT\",\n" +
			"    \"edata\": {\n" +
			"		 \"loc\":\"xyz\",\n" +
			"        \"id\": \"ContentDetail\",\n" +
			"        \"pageid\": \"ContentDetail\",\n" +
			"        \"type\": \"TOUCH\",\n" +
			"        \"subtype\": \"ContentDownload-Initiate\"\n" +
			"    },\n" +
			"    \"ver\": \"3.0\",\n" +
			"    \"ets\": 1541574545180,\n" +
			"    \"context\": {\n" +
			"        \"pdata\": {\n" +
			"            \"ver\": \"2.1.8\",\n" +
			"            \"pid\": \"sunbird.app\",\n" +
			"            \"id\": \"prod.diksha.app\"\n" +
			"        },\n" +
			"        \"channel\": \"0123221617357783046602\",\n" +
			"        \"env\": \"sdk\",\n" +
			"        \"cdata\": [\n" +
			"            {\n" +
			"                \"type\": \"qr\",\n" +
			"                \"id\": \"K4KCXE\"\n" +
			"            },\n" +
			"            {\n" +
			"                \"type\": \"API\",\n" +
			"                \"id\": \"f3ac6610-d218-11e8-b2bb-1598ac1fcb99\"\n" +
			"            }\n" +
			"        ],\n" +
			"        \"sid\": \"70ea93d0-e521-4030-934f-276e7194c225\"\n" +
			"    },\n" +
			"    \"mid\": \"e6a3bcd3-eb78-457b-8fc0-4acc94642ebf\",\n" +
			"    \"object\": {\n" +
			"        \"id\": \"do_31249561779090227216256\",\n" +
			"        \"type\": \"Content\",\n" +
			"        \"version\": \"\"\n" +
			"    },\n" +
			"    \"tags\": [\n" +
			"        \n" +
			"    ],\n" +
			"    \"syncts\": 1539846605341,\n" +
			"    \"@timestamp\": \"2018-10-18T07:10:05.341Z\"\n" +
			"}";

	public static final String ANY_STRING = "Hey Samza, Whats Up?";
	public static final String EMPTY_JSON = "{}";

	public static final String CHANNEL_RESPONSE_BODY = "{\n" +
			"    \"id\": \"api.org.search\",\n" +
			"    \"ver\": \"v1\",\n" +
			"    \"ts\": \"2019-01-08 06:40:12:316+0000\",\n" +
			"    \"params\": {\n" +
			"        \"resmsgid\": null,\n" +
			"        \"msgid\": \"0f93ff40-582f-4599-a718-7bd898cd8b5d\",\n" +
			"        \"err\": null,\n" +
			"        \"status\": \"success\",\n" +
			"        \"errmsg\": null\n" +
			"    },\n" +
			"    \"responseCode\": \"OK\",\n" +
			"    \"result\": {\n" +
			"        \"response\": {\n" +
			"            \"count\": 1,\n" +
			"            \"content\": [\n" +
			"                {\n" +
			"                    \"dateTime\": null,\n" +
			"                    \"preferredLanguage\": \"English\",\n" +
			"                    \"approvedBy\": null,\n" +
			"                    \"channel\": \"ROOT_ORG\",\n" +
			"                    \"description\": \"Andhra State Boardsssssss\",\n" +
			"                    \"updatedDate\": \"2018-11-28 10:00:08:675+0000\",\n" +
			"                    \"addressId\": null,\n" +
			"                    \"provider\": null,\n" +
			"                    \"locationId\": null,\n" +
			"                    \"orgCode\": \"sunbird\",\n" +
			"                    \"theme\": null,\n" +
			"                    \"id\": \"ORG_001\",\n" +
			"                    \"communityId\": null,\n" +
			"                    \"isApproved\": null,\n" +
			"                    \"email\": \"support_dev@sunbird.org\",\n" +
			"                    \"slug\": \"sunbird\",\n" +
			"                    \"identifier\": \"ORG_001\",\n" +
			"                    \"thumbnail\": null,\n" +
			"                    \"orgName\": \"Sunbird\",\n" +
			"                    \"updatedBy\": \"1d7b85b0-3502-4536-a846-d3a51fd0aeea\",\n" +
			"                    \"locationIds\": [\n" +
			"                        \"969dd3c1-4e98-4c17-a994-559f2dc70e18\"\n" +
			"                    ],\n" +
			"                    \"externalId\": null,\n" +
			"                    \"isRootOrg\": true,\n" +
			"                    \"rootOrgId\": \"ORG_001\",\n" +
			"                    \"approvedDate\": null,\n" +
			"                    \"imgUrl\": null,\n" +
			"                    \"homeUrl\": null,\n" +
			"                    \"orgTypeId\": null,\n" +
			"                    \"isDefault\": true,\n" +
			"                    \"contactDetail\": \"[{\\\"phone\\\":\\\"213124234234\\\",\\\"email\\\":\\\"test@test.com\\\"},{\\\"phone\\\":\\\"+91213124234234\\\",\\\"email\\\":\\\"test1@test.com\\\"}]\",\n" +
			"                    \"createdDate\": null,\n" +
			"                    \"createdBy\": null,\n" +
			"                    \"parentOrgId\": null,\n" +
			"                    \"hashTagId\": \"b00bc992ef25f1a9a8d63291e20efc8d\",\n" +
			"                    \"noOfMembers\": 5,\n" +
			"                    \"status\": 1\n" +
			"                }\n" +
			"            ]\n" +
			"        }\n" +
			"    }\n" +
			"}";

	public static final String LOCATION_SEARCH_RESPONSE_BODY = "{\n" +
			"    \"id\": \"api.location.search\",\n" +
			"    \"ver\": \"v1\",\n" +
			"    \"ts\": \"2019-01-08 06:10:57:676+0000\",\n" +
			"    \"params\": {\n" +
			"        \"resmsgid\": null,\n" +
			"        \"msgid\": \"bddca208-34b5-4054-9a05-24eecfc12c99\",\n" +
			"        \"err\": null,\n" +
			"        \"status\": \"success\",\n" +
			"        \"errmsg\": null\n" +
			"    },\n" +
			"    \"responseCode\": \"OK\",\n" +
			"    \"result\": {\n" +
			"        \"response\": [\n" +
			"            {\n" +
			"                \"code\": \"29\",\n" +
			"                \"name\": \"Karnataka\",\n" +
			"                \"id\": \"969dd3c1-4e98-4c17-a994-559f2dc70e18\",\n" +
			"                \"type\": \"state\"\n" +
			"            }\n" +
			"        ]\n" +
			"    }\n" +
			"}";

	public static final String LOCATION_SEARCH_UNSUCCESSFUL_RESPONSE = "{\n"+
			"    \"id\": \"api.location.search\",\n"+
			"    \"ver\": \"v1\",\n"+
			"    \"ts\": \"2019-01-08 07:22:25:891+0000\",\n"+
			"    \"params\": {\n"+
			"        \"resmsgid\": null,\n"+
			"        \"msgid\": \"15b23224-d7b5-4645-bbbe-3fd1172b4112\",\n"+
			"        \"err\": null,\n"+
			"        \"status\": \"success\",\n"+
			"        \"errmsg\": null\n"+
			"    },\n"+
			"    \"responseCode\": \"OK\",\n"+
			"    \"result\": {\n"+
			"        \"response\": []\n"+
			"    }\n"+
			"}";

	public static final String DEVICE_SUMMARY = "{\"eid\":\"ME_DEVICE_SUMMARY\",\"ets\":1573607175904,\"syncts\":1573557231120,\"ver\":\"1.0\",\"mid\":\"2D03E6A051F77F4EEE1F48E4AD7FE575\",\"context\":{\"pdata\":{\"id\":\"AnalyticsDataPipeline\",\"ver\":\"1.0\",\"model\":\"DeviceSummary\"},\"granularity\":\"DAY\",\"date_range\":{\"from\":1573196386089,\"to\":1573196390938},\"channel\":\"b00bc992ef25f1a9a8d63291e20efc8d\"},\"dimensions\":{\"did\":\"099988ce86c4dbb9a4057ff611d38427\",\"channel\":\"b00bc992ef25f1a9a8d63291e20efc8d\"},\"edata\":{\"eks\":{\"firstAccess\":1573196380482,\"dial_stats\":{\"total_count\":0,\"success_count\":0,\"failure_count\":0},\"content_downloads\":0,\"contents_played\":0,\"total_ts\":4.85,\"total_launches\":1,\"unique_contents_played\":0}},\"flags\":{\"derived_dd_processed\":true},\"type\":\"events\",\"ts\":\"2019-11-13T01:06:15.904+0000\"}";

	public static final String CHANNEL_SEARCH_EMPTY_LOCATIONIDS_RESPONSE = "{\n" +
			"    \"id\": \"api.org.search\",\n" +
			"    \"ver\": \"v1\",\n" +
			"    \"ts\": \"2019-01-08 08:57:47:192+0000\",\n" +
			"    \"params\": {\n" +
			"        \"resmsgid\": null,\n" +
			"        \"msgid\": \"7b1cf31f-0b9f-4d88-b405-4d9c61be8081\",\n" +
			"        \"err\": null,\n" +
			"        \"status\": \"success\",\n" +
			"        \"errmsg\": null\n" +
			"    },\n" +
			"    \"responseCode\": \"OK\",\n" +
			"    \"result\": {\n" +
			"        \"response\": {\n" +
			"            \"count\": 1,\n" +
			"            \"content\": [\n" +
			"                {\n" +
			"                    \"dateTime\": null,\n" +
			"                    \"preferredLanguage\": \"English\",\n" +
			"                    \"approvedBy\": null,\n" +
			"                    \"channel\": \"sunbird-staging\",\n" +
			"                    \"description\": \"default user will be associated with this\",\n" +
			"                    \"updatedDate\": null,\n" +
			"                    \"addressId\": null,\n" +
			"                    \"orgType\": null,\n" +
			"                    \"provider\": null,\n" +
			"                    \"locationId\": null,\n" +
			"                    \"orgCode\": \"defaultRootOrg\",\n" +
			"                    \"theme\": null,\n" +
			"                    \"id\": \"0125134851644620800\",\n" +
			"                    \"communityId\": null,\n" +
			"                    \"isApproved\": null,\n" +
			"                    \"slug\": \"sunbird-staging\",\n" +
			"                    \"identifier\": \"0125134851644620800\",\n" +
			"                    \"thumbnail\": null,\n" +
			"                    \"orgName\": \"defaultRootOrg\",\n" +
			"                    \"updatedBy\": null,\n" +
			"                    \"locationIds\": [],\n" +
			"                    \"externalId\": null,\n" +
			"                    \"isRootOrg\": true,\n" +
			"                    \"rootOrgId\": \"0125134851644620800\",\n" +
			"                    \"approvedDate\": null,\n" +
			"                    \"imgUrl\": null,\n" +
			"                    \"homeUrl\": null,\n" +
			"                    \"orgTypeId\": null,\n" +
			"                    \"isDefault\": null,\n" +
			"                    \"contactDetail\": [],\n" +
			"                    \"createdDate\": \"2018-05-28 16:23:38:330+0000\",\n" +
			"                    \"createdBy\": \"8217108a-6931-491c-9009-1ae95cb0477f\",\n" +
			"                    \"parentOrgId\": null,\n" +
			"                    \"hashTagId\": \"0125134851644620800\",\n" +
			"                    \"noOfMembers\": null,\n" +
			"                    \"status\": 1\n" +
			"                }\n" +
			"            ]\n" +
			"        }\n" +
			"    }\n" +
			"}";

	public static Map<String, Object> getMap(String message) {
		return (Map<String, Object>) new Gson().fromJson(message, Map.class);
	}
	
}
