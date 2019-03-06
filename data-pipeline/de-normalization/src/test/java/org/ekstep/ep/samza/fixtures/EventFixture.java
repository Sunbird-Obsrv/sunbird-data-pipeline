package org.ekstep.ep.samza.fixtures;

import com.google.gson.Gson;

import java.util.Map;

public class EventFixture {

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

	public static final String INTERACT_EVENT_WITH_DEVICEDATA = "{\n" +
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
			"    \"ets\": 1.550139834456E12,\n" +
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
			"}";

    public static final String INTERACT_EVENT_WITH_EMPTY_LOC = "{\n" +
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
            "    \"ets\": 1.550139834456E12,\n" +
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

	public static final String INTERACT_EVENT_WITHOUT_OBJECT = "{\n" +
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
			"    \"tags\": [\n" +
			"        \n" +
			"    ],\n" +
			"    \"syncts\": 1539846605341,\n" +
			"    \"@timestamp\": \"2018-10-18T07:10:05.341Z\"\n" +
			"}";

	public static final String SEARCH_EVENT_WITHOUT_DIALCODE = "{\n" +
			"    \"eid\": \"SEARCH\",\n" +
			"    \"ets\": 1543688467595,\n" +
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
			"  }";

	public static final String SEARCH_EVENT_WITH_DIALCODE_AS_STRING = "{\n" +
			"    \"eid\": \"SEARCH\",\n" +
			"    \"ets\": 1543688463694,\n" +
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
			"  }";

	public static final String SEARCH_EVENT_WITH_DIALCODE_AS_LIST = "{\n" +
			"    \"eid\": \"SEARCH\",\n" +
			"    \"ets\": 1543688463694,\n" +
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
			"  }";

	public static final String IMPRESSION_EVENT_WITH_DIALCODE_AS_OBJECT = "{\n" +
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
			"    \"ets\": 1551278751866,\n" +
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
			"}";

	public static final String SEARCH_EVENT_WITH_CAMELCASE_DIALCODE_AS_STRING = "{\n" +
			"    \"eid\": \"SEARCH\",\n" +
			"    \"ets\": 1543688463694,\n" +
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

	public static final String ANY_STRING = "Hey Samza, Whats Up?";
	public static final String EMPTY_JSON = "{}";

	public static Map<String, Object> getMap(String message) {
		return (Map<String, Object>) new Gson().fromJson(message, Map.class);
	}
	
}
