package org.ekstep.ep.samza.fixtures;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

public class EventFixture {

	public static final String VALID_IMPRESSION_EVENT = "{\"actor\":{\"type\":\"User\",\"id\":\"f:5a8a3f2b-3409-42e0-9001-f913bc0fde31:874ed8a5-782e-4f6c-8f36-e0288455901e\"},\"eid\":\"IMPRESSION\",\"edata\":{\"type\":\"detail\",\"pageid\":\"content-detail\",\"uri\":\"content-detail\"},\"ver\":\"3.0\",\"ets\":1.592591395144E12,\"context\":{\"pdata\":{\"pid\":\"sunbird.app\",\"ver\":\"2.10.307\",\"id\":\"prod.diksha.app\"},\"channel\":\"0126684405014528002\",\"env\":\"home\",\"did\":\"77f505f41501d86a0ccaec9cf3b9ec99bec5af06\",\"cdata\":[],\"sid\":\"57b8bc17-1705-4cdd-885b-99db23f4b36b\",\"rollup\":{\"l1\":\"0126684405014528002\"}},\"mid\":\"f5114fee-ab6b-42da-afbe-13bd2bdb0476\",\"object\":{\"id\":\"do_3130160314974781441206\",\"type\":\"ExplanationResource\",\"version\":\"1\",\"rollup\":{\"l1\":\"do_3130160314974781441206\"}},\"syncts\":1592591400233,\"@timestamp\":\"2020-06-19T18:30:00.233Z\"}";

	public static final String UNPARSEABLE_EVENT = "{\n" +
			"  \"actor\": {\n" +
			"    \"type\": \"User\",\n" +
			"    \"id\": \"5a75030f-79be-4a08-a6b2-eb27769a23f5\"\n" +
			"  },\n" +
			"  \"eid\": \"IMPRESSION\",\n" +
			"  \"edata\": {\n" +
			"    \"type\": \"detail\",\n" +
			"    \"pageid\": \"content-detail\",\n" +
			"    \"uri\": \"content-detail\"\n" +
			"  },\n" +
			"  \"ver\": \"3.0\",\n" +
			"  \"ets\": 1.592591395144E12,\n" +
			"  \"context\": {\n" +
			"    \"pdata\": {\n" +
			"      \"pid\": \"sunbird.app\",\n" +
			"      \"ver\": \"2.10.307\",\n" +
			"      \"id\": \"prod.diksha.app\"\n" +
			"    },\n" +
			"    \"channel\": \"0126684405014528002\",\n" +
			"    \"env\": \"home\",\n" +
			"    \"did\": \"77f505f41501d86a0ccaec9cf3b9ec99bec5af06\",\n" +
			"    \"cdata\": [\n" +
			"      \n" +
			"    ],\n" +
			"    \"sid\": \"57b8bc17-1705-4cdd-885b-99db23f4b36b\",\n" +
			"    \"rollup\": {\n" +
			"      \"l1\": \"0126684405014528002\"\n" +
			"    }\n" +
			"  \"mid\": \"f5114fee-ab6b-42da-afbe-13bd2bdb0476\",\n" +
			"  \"object\": {\n" +
			"    \"id\": \"do_3130160314974781441206\",\n" +
			"    \"type\": \"ExplanationResource\",\n" +
			"    \"version\": \"1\",\n" +
			"    \"rollup\": {\n" +
			"      \"l1\": \"do_3130160314974781441206\"\n" +
			"    }\n" +
			"  },\n" +
			"  \"syncts\": 1592591400233,\n" +
			"  \"@timestamp\": \"2020-06-19T18:30:00.233Z\"\n" +
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

	public static final String EVENT_WITH_NO_VALIDATION_SCHEMA = "{\n" +
			"    \"actor\": {\n" +
			"        \"type\": \"User\",\n" +
			"        \"id\": \"anonymous\"\n" +
			"    },\n" +
			"    \"eid\": \"NO_SCHEMA_DEFINED\",\n" +
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

	public static final String EVENT_WITH_DIFFERENT_SCHEMA_VERSION = "{\"actor\":{\"type\":\"User\",\"id\":\"4ba2bb28-16c6-483d-9d94-58c6364e5e3c\"},\"eid\":\"ASSESS\",\"edata\":{\"resvalues\":[{\"3\":{\"text\":\"24n\"}}],\"duration\":21.0,\"score\":0.0,\"item\":{\"mmc\":[],\"mc\":[],\"exlength\":0.0,\"id\":\"do_31296568773394432011706\",\"maxscore\":1.0,\"type\":\"mcq\",\"params\":[{\"1\":{\"text\":\"22n\"}},{\"2\":{\"text\":\"23n\"}},{\"answer\":{\"correct\":[\"4\"]}}],\"title\":\"Test title\",\"uri\":\"\",\"desc\":\"\"},\"pass\":\"No\",\"index\":1.0},\"ver\":\"3.1\",\"ets\":1.592982195439E12,\"context\":{\"pdata\":{\"ver\":\"3.0.0\",\"pid\":\"sunbird-portal.contentplayer\",\"id\":\"prod.diksha.portal\"},\"channel\":\"0126684405014528002\",\"env\":\"contentplayer\",\"did\":\"6798fa5a2d8335c43ba64d5b96a944b9\",\"sid\":\"lRc_DKH5C4034jqv8q1R4O3jxBACvd7e\",\"cdata\":[{\"type\":\"course\",\"id\":\"do_31296569862596198411604\"},{\"type\":\"batch\",\"id\":\"0129657011429376000\"},{\"type\":\"Source\",\"id\":[\"0126087586736619522\",\"0125196274181898243\"]}],\"rollup\":{\"l1\":\"0126684405014528002\"}},\"mid\":\"ASSESS:4ca1ce884b8eeb679abe8ec58c0ac763\",\"object\":{\"ver\":\"1\",\"id\":\"do_3129656882081382401299\",\"type\":\"Content\",\"rollup\":{\"l1\":\"do_3129655631697100801195\",\"l2\":\"do_31296569862596198411604\",\"l3\":\"do_3129656882081382401299\"}},\"tags\":[\"0126684405014528002\"],\"syncts\":1.592982225289E12,\"@timestamp\":\"2020-06-24T07:03:45.289Z\",\"flags\":{\"tv_skipped\":true,\"dd_processed\":true},\"type\":\"events\"}";

	public static final String ANY_STRING = "Hey Samza, Whats Up?";
	public static final String EMPTY_JSON = "{}";

}
