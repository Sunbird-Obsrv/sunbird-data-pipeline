package org.ekstep.ep.samza.fixtures;

import com.google.gson.Gson;
import org.joda.time.DateTime;

import java.util.Map;

public class EventFixture {

	public static Long current_ets = new DateTime().getMillis();

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

	public static final String INTERACT_EVENT = String.format("{\n" +
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
			"    \"ets\": %s,\n" +
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
			"}", current_ets);

	public static final String INTERACT_EVENT_WITH_DEVICEDATA = String.format("{\n" +
			"    \"actor\": {\n" +
			"        \"type\": \"User\",\n" +
			"        \"id\": \"60c02e78-64f9-400c-be3f-d91256d58cf1\"\n" +
			"    },\n" +
			"    \"eid\": \"INTERACT\",\n" +
			"    \"edata\": {\n" +
			"        \"id\": \"draftContentId\",\n" +
			"        \"type\": \"click\",\n" +
			"        \"pageid\": \"DraftContent\"\n" +
			"    },\n" +
			"    \"ver\": \"3.0\",\n" +
			"    \"ets\": %s,\n" +
			"    \"context\": {\n" +
			"        \"uid\": \"60c02e78-64f9-400c-be3f-d91256d58cf1\",\n" +
			"        \"pdata\": {\n" +
			"            \"ver\": \"1.11.0\",\n" +
			"            \"pid\": \"sunbird-portal\",\n" +
			"            \"id\": \"staging.diksha.portal\"\n" +
			"        },\n" +
			"        \"channel\": \"0123221617357783046602\",\n" +
			"        \"env\": \"workspace\",\n" +
			"        \"did\": \"68dfc64a7751ad47617ac1a4e0531fb761ebea6f\",\n" +
			"        \"sid\": \"7ALbNUfErYtkGqPdyK0kjfRSETJXdKp4\",\n" +
			"        \"cdata\": [\n" +
			"            \n" +
			"        ],\n" +
			"        \"rollup\": {\n" +
			"            \"l1\": \"012550822176260096119\"\n" +
			"        }\n" +
			"    },\n" +
			"    \"mid\": \"INTERACT:4b645cf3d2a587413603de219eece5ed\",\n" +
			"    \"object\": {\n" +
			"        \"ver\": \"1.0\",\n" +
			"        \"id\": \"do_21269871962088243212780\",\n" +
			"        \"type\": \"draft\",\n" +
			"        \"rollup\": {\n" +
			"            \n" +
			"        }\n" +
			"    },\n" +
			"    \"tags\": [\n" +
			"        \"012550822176260096119\"\n" +
			"    ],\n" +
			"    \"syncts\": 1.550140278251E12,\n" +
			"    \"@timestamp\": \"2019-02-14T10:31:18.251Z\",\n" +
			"    \"flags\": {\n" +
			"        \"tv_processed\": true,\n" +
			"        \"dd_processed\": true,\n" +
			"        \"device_location_retrieved\": true,\n" +
			"        \"user_location_retrieved\": false\n" +
			"    },\n" +
			"    \"type\": \"events\",\n" +
			"    \"ts\": \"2019-02-14T10:23:54.456+0000\",\n" +
			"    \"devicedata\": {\n" +
			"        \"country\": \"India\",\n" +
			"        \"city\": \"Bengaluru\",\n" +
			"        \"countrycode\": \"IN\",\n" +
			"        \"state\": \"Karnataka\",\n" +
			"        \"statecode\": \"KA\"\n" +
			"    },\n" +
			"    \"userdata\": {\n" +
			"        \"district\": \"\",\n" +
			"        \"state\": \"\"\n" +
			"    }\n" +
			"}", current_ets);

    public static final String INTERACT_EVENT_WITH_EMPTY_LOC = String.format("{\n" +
            "    \"actor\": {\n" +
            "        \"type\": \"User\",\n" +
            "        \"id\": \"60c02e78-64f9-400c-be3f-d91256d58cf1\"\n" +
            "    },\n" +
            "    \"eid\": \"INTERACT\",\n" +
            "    \"edata\": {\n" +
            "        \"id\": \"draftContentId\",\n" +
            "        \"type\": \"click\",\n" +
            "        \"pageid\": \"DraftContent\"\n" +
            "    },\n" +
            "    \"ver\": \"3.0\",\n" +
            "    \"ets\": %s,\n" +
            "    \"context\": {\n" +
            "        \"uid\": \"60c02e78-64f9-400c-be3f-d91256d58cf1\",\n" +
            "        \"pdata\": {\n" +
            "            \"ver\": \"1.11.0\",\n" +
            "            \"pid\": \"sunbird-portal\",\n" +
            "            \"id\": \"staging.diksha.portal\"\n" +
            "        },\n" +
            "        \"channel\": \"0123221617357783046602\",\n" +
            "        \"env\": \"workspace\",\n" +
            "        \"did\": \"68dfc64a7751ad47617ac1a4e0531fb761ebea6f\",\n" +
            "        \"sid\": \"7ALbNUfErYtkGqPdyK0kjfRSETJXdKp4\",\n" +
            "        \"cdata\": [\n" +
            "            \n" +
            "        ],\n" +
            "        \"rollup\": {\n" +
            "            \"l1\": \"012550822176260096119\"\n" +
            "        }\n" +
            "    },\n" +
            "    \"mid\": \"INTERACT:4b645cf3d2a587413603de219eece5ed\",\n" +
            "    \"object\": {\n" +
            "        \"ver\": \"1.0\",\n" +
            "        \"id\": \"do_21269871962088243212780\",\n" +
            "        \"type\": \"draft\",\n" +
            "        \"rollup\": {\n" +
            "            \n" +
            "        }\n" +
            "    },\n" +
            "    \"tags\": [\n" +
            "        \"012550822176260096119\"\n" +
            "    ],\n" +
            "    \"syncts\": 1.550140278251E12,\n" +
            "    \"@timestamp\": \"2019-02-14T10:31:18.251Z\",\n" +
            "    \"flags\": {\n" +
            "        \"tv_processed\": true,\n" +
            "        \"dd_processed\": true,\n" +
            "        \"device_location_retrieved\": true,\n" +
            "        \"user_location_retrieved\": false\n" +
            "    },\n" +
            "    \"type\": \"events\",\n" +
            "    \"ts\": \"2019-02-14T10:23:54.456+0000\",\n" +
            "    \"devicedata\": {\n" +
            "        \"country\": \"\",\n" +
            "        \"city\": \"\",\n" +
            "        \"countrycode\": \"\",\n" +
            "        \"state\": \"\",\n" +
            "        \"statecode\": \"\"\n" +
            "    },\n" +
            "    \"userdata\": {\n" +
            "        \"district\": \"\",\n" +
            "        \"state\": \"\"\n" +
            "    }\n" +
            "}", current_ets);

	public static final String INTERACT_EVENT_WITHOUT_DID = String.format("{\n" +
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
			"    \"ets\": %s,\n" +
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
			"}", current_ets);

	public static final String INTERACT_EVENT_WITH_ACTOR_AS_SYSTEM = String.format("{\n" +
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
			"    \"ets\": %s,\n" +
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
			"}", current_ets);

	public static final String INTERACT_EVENT_WITHOUT_OBJECT = String.format("{\n" +
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
			"    \"ets\": %s,\n" +
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
			"    \"tags\": [\n" +
			"        \n" +
			"    ],\n" +
			"    \"syncts\": 1539846605341,\n" +
			"    \"@timestamp\": \"2018-10-18T07:10:05.341Z\"\n" +
			"}", current_ets);

	public static final String SEARCH_EVENT_WITHOUT_DIALCODE = String.format("{\n" +
			"    \"eid\": \"SEARCH\",\n" +
			"    \"ets\": %s,\n" +
			"    \"ver\": \"3.0\",\n" +
			"    \"mid\": \"LP.1543688467595.c66eb854-82c4-4b73-8902-82209be643ed\",\n" +
			"    \"actor\": {\n" +
			"      \"id\": \"org.ekstep.learning.platform\",\n" +
			"      \"type\": \"System\"\n" +
			"    },\n" +
			"    \"context\": {\n" +
			"      \"channel\": \"in.ekstep\",\n" +
			"      \"pdata\": {\n" +
			"        \"id\": \"prod.ntp.learning.platform\",\n" +
			"        \"pid\": \"search-service\",\n" +
			"        \"ver\": \"1.0\"\n" +
			"      },\n" +
			"      \"env\": \"search\"\n" +
			"    },\n" +
			"    \"edata\": {\n" +
			"      \"size\": 51,\n" +
			"      \"query\": \"\",\n" +
			"      \"filters\": {\n" +
			"        \"contentType\": \"Course\",\n" +
			"        \"objectType\": [\n" +
			"          \"Content\",\n" +
			"          \"ContentImage\"\n" +
			"        ],\n" +
			"        \"status\": [\n" +
			"          \"Live\"\n" +
			"        ],\n" +
			"        \"channel\": {\n" +
			"          \"ne\": [\n" +
			"            \"0124433024890224640\",\n" +
			"            \"0124446042259128320\",\n" +
			"            \"0124487522476933120\",\n" +
			"            \"0125840271570288640\",\n" +
			"            \"0124453662635048969\"\n" +
			"          ]\n" +
			"        },\n" +
			"        \"framework\": {},\n" +
			"        \"mimeType\": {},\n" +
			"        \"resourceType\": {}\n" +
			"      },\n" +
			"      \"sort\": {},\n" +
			"      \"type\": \"content\",\n" +
			"      \"topn\": [\n" +
			"        {\n" +
			"          \"identifier\": \"do_312461520193110016213245\"\n" +
			"        }\n" +
			"      ]\n" +
			"    },\n" +
			"    \"flags\": {\n" +
			"      \"tv_processed\": true,\n" +
			"      \"dd_processed\": true\n" +
			"    },\n" +
			"    \"type\": \"events\",\n" +
			"    \"syncts\": 1543688467885,\n" +
			"    \"@timestamp\": \"2018-12-01T18:21:07.885Z\",\n" +
			"    \"ts\": \"2018-12-01T18:21:07.595+0000\"\n" +
			"  }", current_ets);

	public static final String SEARCH_EVENT_WITH_DIALCODE_AS_STRING = String.format("{\n" +
			"    \"eid\": \"SEARCH\",\n" +
			"    \"ets\": %s,\n" +
			"    \"ver\": \"3.0\",\n" +
			"    \"mid\": \"LP.1543688463694.670c6cf8-2cd2-45a7-b531-f212ac2847ec\",\n" +
			"    \"actor\": {\n" +
			"      \"id\": \"org.ekstep.learning.platform\",\n" +
			"      \"type\": \"System\"\n" +
			"    },\n" +
			"    \"context\": {\n" +
			"      \"channel\": \"in.ekstep\",\n" +
			"      \"pdata\": {\n" +
			"        \"id\": \"prod.ntp.learning.platform\",\n" +
			"        \"pid\": \"search-service\",\n" +
			"        \"ver\": \"1.0\"\n" +
			"      },\n" +
			"      \"env\": \"search\"\n" +
			"    },\n" +
			"    \"edata\": {\n" +
			"      \"size\": 1,\n" +
			"      \"query\": \"\",\n" +
			"      \"filters\": {\n" +
			"        \"dialcodes\": \"8ZEDTP\",\n" +
			"        \"channel\": {\n" +
			"          \"ne\": [\n" +
			"            \"0124433024890224640\",\n" +
			"            \"0124446042259128320\",\n" +
			"            \"0124487522476933120\",\n" +
			"            \"0125840271570288640\",\n" +
			"            \"0124453662635048969\"\n" +
			"          ]\n" +
			"        },\n" +
			"        \"framework\": {},\n" +
			"        \"contentType\": {},\n" +
			"        \"mimeType\": {},\n" +
			"        \"resourceType\": {},\n" +
			"        \"objectType\": [\n" +
			"          \"Content\",\n" +
			"          \"ContentImage\"\n" +
			"        ]\n" +
			"      },\n" +
			"      \"sort\": {},\n" +
			"      \"type\": \"content\",\n" +
			"      \"topn\": [\n" +
			"        {\n" +
			"          \"identifier\": \"do_312531599251210240213439\"\n" +
			"        }\n" +
			"      ]\n" +
			"    },\n" +
			"    \"flags\": {\n" +
			"      \"tv_processed\": true,\n" +
			"      \"dd_processed\": true\n" +
			"    },\n" +
			"    \"type\": \"events\",\n" +
			"    \"syncts\": 1543688463882,\n" +
			"    \"@timestamp\": \"2018-12-01T18:21:03.882Z\",\n" +
			"    \"ts\": \"2018-12-01T18:21:03.694+0000\"\n" +
			"  }", current_ets);

	public static final String SEARCH_EVENT_WITH_DIALCODE_AS_LIST = String.format("{\n" +
			"    \"eid\": \"SEARCH\",\n" +
			"    \"ets\": %s,\n" +
			"    \"ver\": \"3.0\",\n" +
			"    \"mid\": \"LP.1543688463694.670c6cf8-2cd2-45a7-b531-f212ac2847ec\",\n" +
			"    \"actor\": {\n" +
			"      \"id\": \"org.ekstep.learning.platform\",\n" +
			"      \"type\": \"System\"\n" +
			"    },\n" +
			"    \"context\": {\n" +
			"      \"channel\": \"in.ekstep\",\n" +
			"      \"pdata\": {\n" +
			"        \"id\": \"prod.ntp.learning.platform\",\n" +
			"        \"pid\": \"search-service\",\n" +
			"        \"ver\": \"1.0\"\n" +
			"      },\n" +
			"      \"env\": \"search\"\n" +
			"    },\n" +
			"    \"edata\": {\n" +
			"      \"size\": 1,\n" +
			"      \"query\": \"\",\n" +
			"      \"filters\": {\n" +
			"        \"dialcodes\": [\"8ZEDTP\", \"4ZEDTP\"],\n" +
			"        \"channel\": {\n" +
			"          \"ne\": [\n" +
			"            \"0124433024890224640\",\n" +
			"            \"0124446042259128320\",\n" +
			"            \"0124487522476933120\",\n" +
			"            \"0125840271570288640\",\n" +
			"            \"0124453662635048969\"\n" +
			"          ]\n" +
			"        },\n" +
			"        \"framework\": {},\n" +
			"        \"contentType\": {},\n" +
			"        \"mimeType\": {},\n" +
			"        \"resourceType\": {},\n" +
			"        \"objectType\": [\n" +
			"          \"Content\",\n" +
			"          \"ContentImage\"\n" +
			"        ]\n" +
			"      },\n" +
			"      \"sort\": {},\n" +
			"      \"type\": \"content\",\n" +
			"      \"topn\": [\n" +
			"        {\n" +
			"          \"identifier\": \"do_312531599251210240213439\"\n" +
			"        }\n" +
			"      ]\n" +
			"    },\n" +
			"    \"flags\": {\n" +
			"      \"tv_processed\": true,\n" +
			"      \"dd_processed\": true\n" +
			"    },\n" +
			"    \"type\": \"events\",\n" +
			"    \"syncts\": 1543688463882,\n" +
			"    \"@timestamp\": \"2018-12-01T18:21:03.882Z\",\n" +
			"    \"ts\": \"2018-12-01T18:21:03.694+0000\"\n" +
			"  }", current_ets);

	public static final String IMPRESSION_EVENT_WITH_DIALCODE_AS_OBJECT = String.format("{\n" +
			"    \"actor\": {\n" +
			"        \"type\": \"User\",\n" +
			"        \"id\": \"anonymous\"\n" +
			"    },\n" +
			"    \"eid\": \"IMPRESSION\",\n" +
			"    \"edata\": {\n" +
			"        \"visits\": [\n" +
			"            \n" +
			"        ],\n" +
			"        \"type\": \"view\",\n" +
			"        \"pageid\": \"dialcode\",\n" +
			"        \"subtype\": \"pageexit\",\n" +
			"        \"uri\": \"https://play.diksha.gov.in/dialpage/index.html?dialcode=977D3I\"\n" +
			"    },\n" +
			"    \"ver\": \"3.0\",\n" +
			"    \"ets\": %s,\n" +
			"    \"context\": {\n" +
			"        \"uid\": \"anonymous\",\n" +
			"        \"pdata\": {\n" +
			"            \"ver\": \"1.7.1\",\n" +
			"            \"pid\": \"sunbird-portal\",\n" +
			"            \"id\": \"prod.diksha.portal\"\n" +
			"        },\n" +
			"        \"channel\": \"505c7c48ac6dc1edc9b08f21db5a571d\",\n" +
			"        \"env\": \"public\",\n" +
			"        \"did\": \"a49cfadff97c698c1766c71a42779d4e\",\n" +
			"        \"sid\": \"5fd1cea0-3a9e-11e9-bed5-2f34fab96d07\",\n" +
			"        \"cdata\": [\n" +
			"            \n" +
			"        ],\n" +
			"        \"rollup\": {\n" +
			"            \"l1\": \"505c7c48ac6dc1edc9b08f21db5a571d\"\n" +
			"        }\n" +
			"    },\n" +
			"    \"mid\": \"IMPRESSION:bfd4026a4099370da57e3519cd3368c0\",\n" +
			"    \"object\": {\n" +
			"        \"ver\": \"1.0\",\n" +
			"        \"id\": \"977D3I\",\n" +
			"        \"type\": \"dialcode\",\n" +
			"        \"rollup\": {\n" +
			"            \n" +
			"        }\n" +
			"    },\n" +
			"    \"tags\": [\n" +
			"        \"505c7c48ac6dc1edc9b08f21db5a571d\"\n" +
			"    ],\n" +
			"    \"syncts\": 1550501698819,\n" +
			"    \"@timestamp\": \"2019-02-18T14:54:58.819Z\",\n" +
			"    \"flags\": {\n" +
			"        \"tv_processed\": true,\n" +
			"        \"dd_processed\": true,\n" +
			"        \"ldata_retrieved\": false\n" +
			"    },\n" +
			"    \"type\": \"events\",\n" +
			"    \"ts\": \"2019-02-27T14:45:51.866+0000\",\n" +
			"    \"ldata\": {\n" +
			"        \"country_code\": \"\",\n" +
			"        \"country\": \"\",\n" +
			"        \"city\": \"\",\n" +
			"        \"state\": \"\",\n" +
			"        \"state_code\": \"\"\n" +
			"    }  \n" +
			"}", current_ets);

	public static final String SEARCH_EVENT_WITH_CAMELCASE_DIALCODE_AS_STRING = String.format("{\n" +
			"    \"eid\": \"SEARCH\",\n" +
			"    \"ets\": %s,\n" +
			"    \"ver\": \"3.0\",\n" +
			"    \"mid\": \"LP.1543688463694.670c6cf8-2cd2-45a7-b531-f212ac2847ec\",\n" +
			"    \"actor\": {\n" +
			"      \"id\": \"org.ekstep.learning.platform\",\n" +
			"      \"type\": \"System\"\n" +
			"    },\n" +
			"    \"context\": {\n" +
			"      \"channel\": \"in.ekstep\",\n" +
			"      \"pdata\": {\n" +
			"        \"id\": \"prod.ntp.learning.platform\",\n" +
			"        \"pid\": \"search-service\",\n" +
			"        \"ver\": \"1.0\"\n" +
			"      },\n" +
			"      \"env\": \"search\"\n" +
			"    },\n" +
			"    \"edata\": {\n" +
			"      \"size\": 1,\n" +
			"      \"query\": \"\",\n" +
			"      \"filters\": {\n" +
			"        \"dialCodes\": \"8ZEDTP\",\n" +
			"        \"channel\": {\n" +
			"          \"ne\": [\n" +
			"            \"0124433024890224640\",\n" +
			"            \"0124446042259128320\",\n" +
			"            \"0124487522476933120\",\n" +
			"            \"0125840271570288640\",\n" +
			"            \"0124453662635048969\"\n" +
			"          ]\n" +
			"        },\n" +
			"        \"framework\": {},\n" +
			"        \"contentType\": {},\n" +
			"        \"mimeType\": {},\n" +
			"        \"resourceType\": {},\n" +
			"        \"objectType\": [\n" +
			"          \"Content\",\n" +
			"          \"ContentImage\"\n" +
			"        ]\n" +
			"      },\n" +
			"      \"sort\": {},\n" +
			"      \"type\": \"content\",\n" +
			"      \"topn\": [\n" +
			"        {\n" +
			"          \"identifier\": \"do_312531599251210240213439\"\n" +
			"        }\n" +
			"      ]\n" +
			"    },\n" +
			"    \"flags\": {\n" +
			"      \"tv_processed\": true,\n" +
			"      \"dd_processed\": true\n" +
			"    },\n" +
			"    \"type\": \"events\",\n" +
			"    \"syncts\": 1543688463882,\n" +
			"    \"@timestamp\": \"2018-12-01T18:21:03.882Z\",\n" +
			"    \"ts\": \"2018-12-01T18:21:03.694+0000\"\n" +
			"  }", current_ets);

	public static final String SEARCH_EVENT_WITH_FUTURE_ETS = "{\n" +
			"    \"eid\": \"SEARCH\",\n" +
			"    \"ets\": 2530937155000,\n" +
			"    \"ver\": \"2.1\",\n" +
			"    \"mid\": \"LP.1543688463694.670c6cf8-2cd2-45a7-b531-f212ac2847ec\",\n" +
			"    \"actor\": {\n" +
			"      \"id\": \"org.ekstep.learning.platform\",\n" +
			"      \"type\": \"System\"\n" +
			"    },\n" +
			"    \"context\": {\n" +
			"      \"channel\": \"in.ekstep\",\n" +
			"      \"pdata\": {\n" +
			"        \"id\": \"prod.ntp.learning.platform\",\n" +
			"        \"pid\": \"search-service\",\n" +
			"        \"ver\": \"1.0\"\n" +
			"      },\n" +
			"      \"env\": \"search\"\n" +
			"    },\n" +
			"    \"edata\": {\n" +
			"      \"size\": 1,\n" +
			"      \"query\": \"\",\n" +
			"      \"filters\": {\n" +
			"        \"dialCodes\": \"8ZEDTP\",\n" +
			"        \"channel\": {\n" +
			"          \"ne\": [\n" +
			"            \"0124433024890224640\",\n" +
			"            \"0124446042259128320\",\n" +
			"            \"0124487522476933120\",\n" +
			"            \"0125840271570288640\",\n" +
			"            \"0124453662635048969\"\n" +
			"          ]\n" +
			"        },\n" +
			"        \"framework\": {},\n" +
			"        \"contentType\": {},\n" +
			"        \"mimeType\": {},\n" +
			"        \"resourceType\": {},\n" +
			"        \"objectType\": [\n" +
			"          \"Content\",\n" +
			"          \"ContentImage\"\n" +
			"        ]\n" +
			"      },\n" +
			"      \"sort\": {},\n" +
			"      \"type\": \"content\",\n" +
			"      \"topn\": [\n" +
			"        {\n" +
			"          \"identifier\": \"do_312531599251210240213439\"\n" +
			"        }\n" +
			"      ]\n" +
			"    },\n" +
			"    \"flags\": {\n" +
			"      \"tv_processed\": true,\n" +
			"      \"dd_processed\": true\n" +
			"    },\n" +
			"    \"type\": \"events\",\n" +
			"    \"syncts\": 1543688463882,\n" +
			"    \"@timestamp\": \"2018-12-01T18:21:03.882Z\",\n" +
			"    \"ts\": \"2018-12-01T18:21:03.694+0000\"\n" +
			"  }";

	public static final String SEARCH_EVENT_WITH_OLDER_ETS = "{\n" +
			"    \"eid\": \"SEARCH\",\n" +
			"    \"ets\": 1520753300000,\n" +
			"    \"ver\": \"3.0\",\n" +
			"    \"mid\": \"LP.1543688463694.670c6cf8-2cd2-45a7-b531-f212ac2847ec\",\n" +
			"    \"actor\": {\n" +
			"      \"id\": \"org.ekstep.learning.platform\",\n" +
			"      \"type\": \"System\"\n" +
			"    },\n" +
			"    \"context\": {\n" +
			"      \"channel\": \"in.ekstep\",\n" +
			"      \"pdata\": {\n" +
			"        \"id\": \"prod.ntp.learning.platform\",\n" +
			"        \"pid\": \"search-service\",\n" +
			"        \"ver\": \"1.0\"\n" +
			"      },\n" +
			"      \"env\": \"search\"\n" +
			"    },\n" +
			"    \"edata\": {\n" +
			"      \"size\": 1,\n" +
			"      \"query\": \"\",\n" +
			"      \"filters\": {\n" +
			"        \"dialCodes\": \"8ZEDTP\",\n" +
			"        \"channel\": {\n" +
			"          \"ne\": [\n" +
			"            \"0124433024890224640\",\n" +
			"            \"0124446042259128320\",\n" +
			"            \"0124487522476933120\",\n" +
			"            \"0125840271570288640\",\n" +
			"            \"0124453662635048969\"\n" +
			"          ]\n" +
			"        },\n" +
			"        \"framework\": {},\n" +
			"        \"contentType\": {},\n" +
			"        \"mimeType\": {},\n" +
			"        \"resourceType\": {},\n" +
			"        \"objectType\": [\n" +
			"          \"Content\",\n" +
			"          \"ContentImage\"\n" +
			"        ]\n" +
			"      },\n" +
			"      \"sort\": {},\n" +
			"      \"type\": \"content\",\n" +
			"      \"topn\": [\n" +
			"        {\n" +
			"          \"identifier\": \"do_312531599251210240213439\"\n" +
			"        }\n" +
			"      ]\n" +
			"    },\n" +
			"    \"flags\": {\n" +
			"      \"tv_processed\": true,\n" +
			"      \"dd_processed\": true\n" +
			"    },\n" +
			"    \"type\": \"events\",\n" +
			"    \"syncts\": 1543688463882,\n" +
			"    \"@timestamp\": \"2018-12-01T18:21:03.882Z\",\n" +
			"    \"ts\": \"2018-12-01T18:21:03.694+0000\"\n" +
			"  }";

	public static final String WFS_EVENT = String.format("{\n" +
			"    \"eid\": \"ME_WORKFLOW_SUMMARY\",\n" +
			"    \"ets\": %s,\n" +
			"    \"syncts\": 1.553952011044E12,\n" +
			"    \"ver\": \"1.1\",\n" +
			"    \"mid\": \"8D7C477F841B6F82937BBC6F61E7C2FD\",\n" +
			"    \"uid\": \"393407b1-66b1-4c86-9080-b2bce9842886\",\n" +
			"    \"context\": {\n" +
			"        \"pdata\": {\n" +
			"            \"id\": \"AnalyticsDataPipeline\",\n" +
			"            \"ver\": \"1.0\",\n" +
			"            \"model\": \"WorkflowSummarizer\"\n" +
			"        },\n" +
			"        \"granularity\": \"SESSION\",\n" +
			"        \"date_range\": {\n" +
			"            \"from\": 1.553951969817E12,\n" +
			"            \"to\": 1.553951999398E12\n" +
			"        },\n" +
			"        \"cdata\": [\n" +
			"            \n" +
			"        ]\n" +
			"    },\n" +
			"    \"dimensions\": {\n" +
			"        \"did\": \"68dfc64a7751ad47617ac1a4e0531fb761ebea6f\",\n" +
			"        \"pdata\": {\n" +
			"            \"id\": \"prod.diksha.app\",\n" +
			"            \"ver\": \"2.1.45\",\n" +
			"            \"pid\": \"sunbird.app\"\n" +
			"        },\n" +
			"        \"sid\": \"4a58927b-ad4d-491e-a9e2-73b592a60a22\",\n" +
			"        \"channel\": \"0123221617357783046602\",\n" +
			"        \"type\": \"session\",\n" +
			"        \"mode\": \"\"\n" +
			"    },\n" +
			"    \"edata\": {\n" +
			"        \"eks\": {\n" +
			"            \"interact_events_per_min\": 5.0,\n" +
			"            \"start_time\": 1.553951969817E12,\n" +
			"            \"interact_events_count\": 5.0,\n" +
			"            \"item_responses\": [\n" +
			"                \n" +
			"            ],\n" +
			"            \"end_time\": 1.553951999398E12,\n" +
			"            \"events_summary\": [\n" +
			"                {\n" +
			"                    \"id\": \"START\",\n" +
			"                    \"count\": 4.0\n" +
			"                },\n" +
			"                {\n" +
			"                    \"id\": \"IMPRESSION\",\n" +
			"                    \"count\": 4.0\n" +
			"                },\n" +
			"                {\n" +
			"                    \"id\": \"INTERACT\",\n" +
			"                    \"count\": 12.0\n" +
			"                },\n" +
			"                {\n" +
			"                    \"id\": \"SHARE\",\n" +
			"                    \"count\": 1.0\n" +
			"                }\n" +
			"            ],\n" +
			"            \"page_summary\": [\n" +
			"                {\n" +
			"                    \"id\": \"dial-code-scan-result\",\n" +
			"                    \"type\": \"view\",\n" +
			"                    \"env\": \"home\",\n" +
			"                    \"time_spent\": 4.46,\n" +
			"                    \"visit_count\": 1.0\n" +
			"                },\n" +
			"                {\n" +
			"                    \"id\": \"user-type-selection\",\n" +
			"                    \"type\": \"search\",\n" +
			"                    \"env\": \"home\",\n" +
			"                    \"time_spent\": 6.08,\n" +
			"                    \"visit_count\": 1.0\n" +
			"                },\n" +
			"                {\n" +
			"                    \"id\": \"content-detail\",\n" +
			"                    \"type\": \"detail\",\n" +
			"                    \"env\": \"home\",\n" +
			"                    \"time_spent\": 11.73,\n" +
			"                    \"visit_count\": 1.0\n" +
			"                },\n" +
			"                {\n" +
			"                    \"id\": \"qr-code-scanner\",\n" +
			"                    \"type\": \"view\",\n" +
			"                    \"env\": \"home\",\n" +
			"                    \"time_spent\": 7.6,\n" +
			"                    \"visit_count\": 1.0\n" +
			"                }\n" +
			"            ],\n" +
			"            \"time_diff\": 29.58,\n" +
			"            \"telemetry_version\": \"3.0\",\n" +
			"            \"env_summary\": [\n" +
			"                {\n" +
			"                    \"env\": \"home\",\n" +
			"                    \"time_spent\": 29.87,\n" +
			"                    \"count\": 1.0\n" +
			"                }\n" +
			"            ],\n" +
			"            \"time_spent\": 29.6\n" +
			"        }\n" +
			"    },\n" +
			"    \"tags\": [\n" +
			"        \n" +
			"    ],\n" +
			"    \"object\": {\n" +
			"        \"id\": \"\",\n" +
			"        \"type\": \"\"\n" +
			"    },\n" +
			"    \"ts\": \"2019-03-31T03:07:04.736+0000\",\n" +
			"    \"devicedata\": {\n" +
			"        \"country\": \"\",\n" +
			"        \"city\": \"\",\n" +
			"        \"countrycode\": \"\",\n" +
			"        \"state\": \"\",\n" +
			"        \"statecode\": \"\"\n" +
			"    },\n" +
			"    \"flags\": {\n" +
			"        \"dv_processed\": false\n" +
			"    }\n" +
			"}", current_ets);

	public static final String DEVICE_SUMMARY_EVENT = String.format("{\n" +
			"    \"eid\": \"ME_DEVICE_SUMMARY\",\n" +
			"    \"ets\": %s,\n" +
			"    \"syncts\": 1554101904592,\n" +
			"    \"ver\": \"1.0\",\n" +
			"    \"mid\": \"3ACBFD49BAFC74832A44347FF4F1E611\",\n" +
			"    \"context\": {\n" +
			"      \"pdata\": {\n" +
			"        \"id\": \"AnalyticsDataPipeline\",\n" +
			"        \"ver\": \"1.0\",\n" +
			"        \"model\": \"DeviceSummary\"\n" +
			"      },\n" +
			"      \"granularity\": \"DAY\",\n" +
			"      \"date_range\": {\n" +
			"        \"from\": 1554101897620,\n" +
			"        \"to\": 1554101904209\n" +
			"      }\n" +
			"    },\n" +
			"    \"dimensions\": {\n" +
			"      \"did\": \"3f2b155788bef740fe741c92b8d80cac\",\n" +
			"      \"channel\": \"01231711180382208027\"\n" +
			"    },\n" +
			"    \"edata\": {\n" +
			"      \"eks\": {\n" +
			"        \"firstAccess\": 1553574785315,\n" +
			"        \"dial_stats\": {\n" +
			"          \"total_count\": 0,\n" +
			"          \"success_count\": 0,\n" +
			"          \"failure_count\": 0\n" +
			"        },\n" +
			"        \"content_downloads\": 0,\n" +
			"        \"contents_played\": 0,\n" +
			"        \"total_ts\": 6,\n" +
			"        \"total_launches\": 1,\n" +
			"        \"unique_contents_played\": 0\n" +
			"      }\n" +
			"    },\n" +
			"    \"ts\": \"2019-04-02T03:07:09.447+0000\",\n" +
			"    \"devicedata\": {\n" +
			"      \"statecustomcode\": \"\",\n" +
			"      \"country\": \"\",\n" +
			"      \"city\": \"\",\n" +
			"      \"countrycode\": \"\",\n" +
			"      \"state\": \"\",\n" +
			"      \"statecode\": \"\",\n" +
			"      \"districtcustom\": \"\",\n" +
			"      \"statecustomname\": \"\"\n" +
			"    },\n" +
			"    \"flags\": {\n" +
			"      \"device_location_retrieved\": true\n" +
			"    }\n" +
			"  }", current_ets);

	public static final String ANY_STRING = "Hey Samza, Whats Up?";
	public static final String EMPTY_JSON = "{}";

	public static final String LOG_EVENT = String.format("{\n"+
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
			"\"type\":\"api_access\"\n"+
			"},\n"+
			"\"eid\":\"LOG\",\n"+
			"\"ets\":%s2,\n"+
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
			"      }", current_ets);

	public static final String ERROR_EVENT = String.format("{"+
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
			"}", current_ets);

    public static final String TEST_LOG_EVENT = String.format("{\n"+
            "   \"@timestamp\":\"2019-03-20T00:00:01.176Z\",\n"+
            "\"actor\":{\n"+
            "\"id\":\"0b251080-3230-415e-a593-ab7c1fac7ae3\",\n"+
            "\"type\":\"User\"\n"+
            "},\n"+
            "\"context\":{\n"+
            "\"cdata\":[\n"+

            "],\n"+
            "\"channel\":\"505c7c48ac6dc1edc9b08f21db5a571d\",\n"+
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
            "\"ets\":%s,\n"+
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
            "      }", current_ets);

	public static Map<String, Object> getMap(String message) {
		return (Map<String, Object>) new Gson().fromJson(message, Map.class);
	}
	
}
