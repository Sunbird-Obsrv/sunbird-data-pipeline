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

    public static final String INVALID_EVENT = "{\n" +
            "  \"actor\": {\n" +
            "    \"type\": \"User\",\n" +
            "    \"id\": \"393407b1-66b1-4c86-9080-b2bce9842886\"\n" +
            "  },\n" +
            "  \"eid\": \"LOG\",\n" +
            "  \"edata\": {\n" +
            "    \"id\": \"ContentDetail\",\n" +
            "    \"pageid\": \"ContentDetail\",\n" +
            "    \"type\": \"TOUCH\",\n" +
            "    \"subtype\": \"ContentDownload-Initiate\"\n" +
            "  },\n" +
            "  \"ver\": \"3.0\",\n" +
            "  \"ets\": \"1541574545180\",\n" +
            "  \"context\": {\n" +
            "    \"pdata\": {\n" +
            "      \"ver\": \"2.1.8\",\n" +
            "      \"pid\": \"sunbird.app\",\n" +
            "      \"id\": \"prod.diksha.app\"\n" +
            "    },\n" +
            "    \"channel\": \"0123221617357783046602\",\n" +
            "    \"env\": \"sdk\",\n" +
            "    \"did\": \"68dfc64a7751ad47617ac1a4e0531fb761ebea6f\",\n" +
            "    \"cdata\": [\n" +
            "      {\n" +
            "        \"type\": \"qr\",\n" +
            "        \"id\": \"K4KCXE\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"API\",\n" +
            "        \"id\": \"f3ac6610-d218-11e8-b2bb-1598ac1fcb99\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"sid\": \"70ea93d0-e521-4030-934f-276e7194c225\"\n" +
            "  },\n" +
            "  \"mid\": \"e6a3bcd3-eb78-457b-8fc0-4acc94642ebf\",\n" +
            "  \"object\": {\n" +
            "    \"id\": \"do_31249561779090227216256\",\n" +
            "    \"type\": \"Content\",\n" +
            "    \"version\": \"\"\n" +
            "  },\n" +
            "  \"tags\": [],\n" +
            "  \"syncts\": 1539846605341,\n" +
            "  \"@timestamp\": \"2018-10-18T07:10:05.341Z\",\n" +
            "  \"derivedlocationdata\": {\n" +
            "    \"district\": \"Bengaluru\",\n" +
            "    \"state\": 1234,\n" +
            "    \"from\": \"user-profile\"\n" +
            "  },\n" +
            "  \"devicedata\": {\n" +
            "    \"statecustomcode\": \"KA-Custom\",\n" +
            "    \"country\": \"India\",\n" +
            "    \"iso3166statecode\": \"IN-KA\",\n" +
            "    \"city\": \"Bangalore\",\n" +
            "    \"countrycode\": \"IN\",\n" +
            "    \"statecode\": \"KA\",\n" +
            "    \"devicespec\": {\n" +
            "      \"os\": \"Android 6.0\",\n" +
            "      \"make\": \"Motorola XT1706\"\n" +
            "    },\n" +
            "    \"districtcustom\": \"Banglore-Custom\",\n" +
            "    \"firstaccess\": 1559484698000,\n" +
            "    \"uaspec\": {\n" +
            "      \"agent\": \"Mozilla\",\n" +
            "      \"ver\": \"5.0\",\n" +
            "      \"system\": \"iPad\",\n" +
            "      \"raw\": \"Mozilla/5.0 (X11 Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko)\",\n" +
            "      \"platform\": \"AppleWebKit/531.21.10\"\n" +
            "    },\n" +
            "    \"state\": \"Karnataka\",\n" +
            "    \"statecustomname\": \"Karnatak-Custom\",\n" +
            "    \"userdeclared\": {\n" +
            "      \"district\": \"Bangalore\",\n" +
            "      \"state\": \"Karnataka\"\n" +
            "    }\n" +
            "  }\n" +
            "}";

    public static final String  VALID_LOG_EVENT = "{\n" +
            "  \"eid\": \"LOG\",\n" +
            "  \"ets\": 1570817279146,\n" +
            "  \"ver\": \"3.0\",\n" +
            "  \"mid\": \"LOG:2a210813fac274656f194f9807ad8abf\",\n" +
            "  \"actor\": {\n" +
            "    \"id\": \"1\",\n" +
            "    \"type\": \"service\"\n" +
            "  },\n" +
            "  \"context\": {\n" +
            "    \"channel\": \"505c7c48ac6dc1edc9b08f21db5a571d\",\n" +
            "    \"pdata\": {\n" +
            "      \"id\": \"prod.diksha.content-service\",\n" +
            "      \"ver\": \"1.0\",\n" +
            "      \"pid\": \"sunbird-content-service\"\n" +
            "    },\n" +
            "    \"env\": \"content\",\n" +
            "    \"sid\": \"\",\n" +
            "    \"did\": \"1e465bed2138b8f2764d946492293e2b2f628789\",\n" +
            "    \"cdata\": [],\n" +
            "    \"rollup\": {}\n" +
            "  },\n" +
            "  \"object\": {\n" +
            "    \"id\": \"do_312776645813747712111638\",\n" +
            "    \"type\": \"content\",\n" +
            "    \"ver\": \"\",\n" +
            "    \"rollup\": {}\n" +
            "  },\n" +
            "  \"tags\": [],\n" +
            "  \"edata\": {\n" +
            "    \"type\": \"api_access\",\n" +
            "    \"level\": \"INFO\",\n" +
            "    \"message\": \"successful\",\n" +
            "    \"params\": [\n" +
            "      {\n" +
            "        \"rid\": \"0ece8640-ec52-11e9-a3d6-697ee5684e40\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"title\": \"Content read api\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"category\": \"contentread\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"url\": \"content/read\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"method\": \"GET\"\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"syncts\": 1570817431399,\n" +
            "  \"@timestamp\": \"2019-10-11T18:10:31.399Z\",\n" +
            "  \"flags\": {\n" +
            "    \"tv_processed\": true,\n" +
            "    \"dd_processed\": true\n" +
            "  },\n" +
            "  \"type\": \"events\"\n" +
            "}";

    public static final String VALID_LOG_WITH_MISSING_CHANNEL="{\n" +
            "  \"eid\": \"LOG\",\n" +
            "  \"ets\": 1570817279146,\n" +
            "  \"ver\": \"3.0\",\n" +
            "  \"mid\": \"LOG:2a210813fac274656f194f9807ad8abf\",\n" +
            "  \"actor\": {\n" +
            "    \"id\": \"1\",\n" +
            "    \"type\": \"service\"\n" +
            "  },\n" +
            "  \"context\": {\n" +
            "    \"pdata\": {\n" +
            "      \"id\": \"prod.diksha.content-service\",\n" +
            "      \"ver\": \"1.0\",\n" +
            "      \"pid\": \"sunbird-content-service\"\n" +
            "    },\n" +
            "    \"env\": \"content\",\n" +
            "    \"sid\": \"\",\n" +
            "    \"did\": \"1e465bed2138b8f2764d946492293e2b2f628789\",\n" +
            "    \"cdata\": [],\n" +
            "    \"rollup\": {}\n" +
            "  },\n" +
            "  \"object\": {\n" +
            "    \"id\": \"do_312776645813747712111638\",\n" +
            "    \"type\": \"content\",\n" +
            "    \"ver\": \"\",\n" +
            "    \"rollup\": {}\n" +
            "  },\n" +
            "  \"tags\": [],\n" +
            "  \"edata\": {\n" +
            "    \"type\": \"api_access\",\n" +
            "    \"level\": \"INFO\",\n" +
            "    \"message\": \"successful\",\n" +
            "    \"params\": [\n" +
            "      {\n" +
            "        \"rid\": \"0ece8640-ec52-11e9-a3d6-697ee5684e40\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"title\": \"Content read api\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"category\": \"contentread\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"url\": \"content/read\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"method\": \"GET\"\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"syncts\": 1570817431399,\n" +
            "  \"@timestamp\": \"2019-10-11T18:10:31.399Z\",\n" +
            "  \"flags\": {\n" +
            "    \"tv_processed\": true,\n" +
            "    \"dd_processed\": true\n" +
            "  },\n" +
            "  \"type\": \"events\"\n" +
            "}";

    public static final String VALID_LOG_WITH_MISSING_SYNCTS="{\n" +
            "  \"eid\": \"LOG\",\n" +
            "  \"ets\": 1570817279146,\n" +
            "  \"ver\": \"3.0\",\n" +
            "  \"mid\": \"LOG:2a210813fac274656f194f9807ad8abf\",\n" +
            "  \"actor\": {\n" +
            "    \"id\": \"1\",\n" +
            "    \"type\": \"service\"\n" +
            "  },\n" +
            "  \"context\": {\n" +
            "    \"channel\": \"505c7c48ac6dc1edc9b08f21db5a571d\",\n" +
            "    \"pdata\": {\n" +
            "      \"id\": \"prod.diksha.content-service\",\n" +
            "      \"ver\": \"1.0\",\n" +
            "      \"pid\": \"sunbird-content-service\"\n" +
            "    },\n" +
            "    \"env\": \"content\",\n" +
            "    \"sid\": \"\",\n" +
            "    \"did\": \"1e465bed2138b8f2764d946492293e2b2f628789\",\n" +
            "    \"cdata\": [],\n" +
            "    \"rollup\": {}\n" +
            "  },\n" +
            "  \"object\": {\n" +
            "    \"id\": \"do_312776645813747712111638\",\n" +
            "    \"type\": \"content\",\n" +
            "    \"ver\": \"\",\n" +
            "    \"rollup\": {}\n" +
            "  },\n" +
            "  \"tags\": [],\n" +
            "  \"edata\": {\n" +
            "    \"type\": \"api_access\",\n" +
            "    \"level\": \"INFO\",\n" +
            "    \"message\": \"successful\",\n" +
            "    \"params\": [\n" +
            "      {\n" +
            "        \"rid\": \"0ece8640-ec52-11e9-a3d6-697ee5684e40\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"title\": \"Content read api\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"category\": \"contentread\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"url\": \"content/read\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"method\": \"GET\"\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"flags\": {\n" +
            "    \"tv_processed\": true,\n" +
            "    \"dd_processed\": true\n" +
            "  },\n" +
            "  \"type\": \"events\"\n" +
            "}";

    public static final String IMPRESSION_EVENT_WITH_DIALCODE_OBJECT_IN_LOWERCASE = "{\n" +
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
            "    \"ets\": 1570817279146,\n" +
            "    \"context\": {\n" +
            "        \"uid\": \"anonymous\",\n" +
            "        \"pdata\": {\n" +
            "            \"ver\": \"1.7.1\",\n" +
            "            \"pid\": \"sunbird-portal\",\n" +
            "            \"id\": \"dev.sunbird.portal\"\n" +
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
            "        \"id\": \"977d3i\",\n" +
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

    public static final String IMPRESSION_EVENT_WITH_DIALCODE_OBJECT_IN_UPPERCASE = "{\n" +
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
            "    \"ets\": 1570817279146,\n" +
            "    \"context\": {\n" +
            "        \"uid\": \"anonymous\",\n" +
            "        \"pdata\": {\n" +
            "            \"ver\": \"1.7.1\",\n" +
            "            \"pid\": \"sunbird-portal\",\n" +
            "            \"id\": \"dev.sunbird.portal\"\n" +
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
            "        \"type\": \"qr\",\n" +
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

    public static final String EVENT_WITHOUT_EID = "{\n" +
			"    \"@timestamp\": \"2020-06-14T18:31:18.487Z\",\n" +
			"    \"@version\": \"1\",\n" +
			"    \"context\": {\n" +
			"        \"channel\": \"505c7c48ac6dc1edc9b08f21db5a571d\"\n" +
			"    },\n" +
			"    \"message\": \"478097.fdedf464-72ca-45ae-8100-9a92fac0c46b\\\",\\\"actor\\\":{\\\"id\\\":\\\"org.ekstep.learning.platform\\\",\\\"type\\\":\\\"System\\\"},\\\"context\\\":{\\\"channel\\\":\\\"in.ekstep\\\",\\\"pdata\\\":{\\\"id\\\":\\\"prod.ntp.learning.platform\\\",\\\"pid\\\":\\\"search-service\\\",\\\"ver\\\":\\\"1.0\\\"},\\\"env\\\":\\\"search\\\"},\\\"edata\\\":{\\\"size\\\":0,\\\"query\\\":\\\"\\\",\\\"filters\\\":{\\\"status\\\":[\\\"Live\\\",\\\"Draft\\\",\\\"Review\\\"],\\\"contentType\\\":[\\\"Resource\\\",\\\"Collection\\\"],\\\"createdFor\\\":\\\"01241408242723225614\\\",\\\"createdOn\\\":{\\\">=\\\":\\\"2020-06-08T00:00:00.000+0000\\\",\\\"<\\\":\\\"2020-06-15T00:00:00.000+0000\\\"}},\\\"sort\\\":{},\\\"type\\\":\\\"all\\\",\\\"topn\\\":[]},\\\"syncts\\\":1592159478097}\",\n" +
			"    \"tags\": [\n" +
			"        \"_jsonparsefailure\"\n" +
			"    ]\n" +
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
