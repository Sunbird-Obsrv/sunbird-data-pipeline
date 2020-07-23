package org.ekstep.ep.samza.fixtures;

import com.google.gson.Gson;

import java.util.Map;

public class EventFixture {

	public static final String VALID_SHARE_EVENT = "{\n" +
			"  \"ver\": \"3.0\",\n" +
			"  \"eid\": \"SHARE\",\n" +
			"  \"ets\": 1577278681178,\n" +
			"  \"actor\": {\n" +
			"    \"type\": \"User\",\n" +
			"    \"id\": \"7c3ea1bb-4da1-48d0-9cc0-c4f150554149\"\n" +
			"  },\n" +
			"  \"context\": {\n" +
			"    \"cdata\": [\n" +
			"      {\n" +
			"        \"id\": \"1bfd99b0-2716-11ea-b7cc-13dec7acd2be\",\n" +
			"        \"type\": \"API\"\n" +
			"      },\n" +
			"      {\n" +
			"        \"id\": \"SearchResult\",\n" +
			"        \"type\": \"Section\"\n" +
			"      }\n" +
			"    ],\n" +
			"    \"channel\": \"505c7c48ac6dc1edc9b08f21db5a571d\",\n" +
			"    \"pdata\": {\n" +
			"      \"id\": \"prod.diksha.app\",\n" +
			"      \"pid\": \"sunbird.app\",\n" +
			"      \"ver\": \"2.3.162\"\n" +
			"    },\n" +
			"    \"env\": \"app\",\n" +
			"    \"sid\": \"82e41d87-e33f-4269-aeae-d56394985599\",\n" +
			"    \"did\": \"1b17c32bad61eb9e33df281eecc727590d739b2b\"\n" +
			"  },\n" +
			"  \"edata\": {\n" +
			"    \"dir\": \"In\",\n" +
			"    \"type\": \"File\",\n" +
			"    \"items\": [\n" +
			"      {\n" +
			"        \"origin\": {\n" +
			"          \"id\": \"1b17c32bad61eb9e33df281eecc727590d739b2b\",\n" +
			"          \"type\": \"Device\"\n" +
			"        },\n" +
			"        \"id\": \"do_312785709424099328114191\",\n" +
			"        \"type\": \"CONTENT\",\n" +
			"        \"ver\": \"1\",\n" +
			"        \"params\": [\n" +
			"          {\n" +
			"            \"transfers\": 0,\n" +
			"            \"size\": 21084308\n" +
			"          }\n" +
			"        ]\n" +
			"      },\n" +
			"      {\n" +
			"        \"origin\": {\n" +
			"          \"id\": \"1b17c32bad61eb9e33df281eecc727590d739b2b\",\n" +
			"          \"type\": \"Device\"\n" +
			"        },\n" +
			"        \"id\": \"do_31277435209002188818711\",\n" +
			"        \"type\": \"CONTENT\",\n" +
			"        \"ver\": \"18\",\n" +
			"        \"params\": [\n" +
			"          {\n" +
			"            \"transfers\": 12,\n" +
			"            \"size\": \"123\"\n" +
			"          }\n" +
			"        ]\n" +
			"      },\n" +
			"      {\n" +
			"        \"origin\": {\n" +
			"          \"id\": \"1b17c32bad61eb9e33df281eecc727590d739b2b\",\n" +
			"          \"type\": \"Device\"\n" +
			"        },\n" +
			"        \"id\": \"do_31278794857559654411554\",\n" +
			"        \"type\": \"TextBook\",\n" +
			"        \"ver\": \"1\"\n" +
			"      }\n" +
			"    ]\n" +
			"  },\n" +
			"  \"object\": {\n" +
			"    \"id\": \"do_312528116260749312248818\",\n" +
			"    \"type\": \"TextBook\",\n" +
			"    \"version\": \"10\",\n" +
			"    \"rollup\": {}\n" +
			"  },\n" +
			"  \"mid\": \"02ba33e5-15fe-4ec5-b32.1084308E760-3d03429fae84\",\n" +
			"  \"syncts\": 1577278682630,\n" +
			"  \"@timestamp\": \"2019-12-25T12:58:02.630Z\",\n" +
			"  \"flags\": {\n" +
			"    \"tv_processed\": true,\n" +
			"    \"dd_processed\": true\n" +
			"  },\n" +
			"  \"type\": \"events\"\n" +
			"}";
	public static final String VALID_SHARE_EVENT_WHEN_EMPTY_SIZE = "{\n" +
			"  \"eid\": \"SHARE\",\n" +
			"  \"ets\": 1578979395120,\n" +
			"  \"ver\": \"3.0\",\n" +
			"  \"mid\": \"SHARE:6a68490ac9c8ffcb8f8b8e1e6da1f09c\",\n" +
			"  \"actor\": {\n" +
			"    \"id\": \"373e0438854402794e7fc5c3e2feb5361b267fedcd6dfbd102b07a2882932e3a\",\n" +
			"    \"type\": \"User\"\n" +
			"  },\n" +
			"  \"context\": {\n" +
			"    \"channel\": \"01231711180382208027\",\n" +
			"    \"pdata\": {\n" +
			"      \"id\": \"staging.diksha.desktop\",\n" +
			"      \"ver\": \"1.0.3\",\n" +
			"      \"pid\": \"desktop.app\"\n" +
			"    },\n" +
			"    \"env\": \"Content\",\n" +
			"    \"sid\": \"d17137fd-224a-49bd-834c-0e9d7a21ca18\",\n" +
			"    \"did\": \"373e0438854402794e7fc5c3e2feb5361b267fedcd6dfbd102b07a2882932e3a\",\n" +
			"    \"cdata\": [],\n" +
			"    \"rollup\": {\n" +
			"      \"l1\": \"01231711180382208027\"\n" +
			"    }\n" +
			"  },\n" +
			"  \"object\": {},\n" +
			"  \"tags\": [\n" +
			"    \"01231711180382208027\"\n" +
			"  ],\n" +
			"  \"edata\": {\n" +
			"    \"dir\": \"Out\",\n" +
			"    \"type\": \"File\",\n" +
			"    \"items\": [\n" +
			"      {\n" +
			"        \"type\": \"TextBook\",\n" +
			"        \"ver\": \"1\",\n" +
			"        \"params\": [\n" +
			"          {\n" +
			"            \"transfers\": \"53\"\n" +
			"          },\n" +
			"          {\n" +
			"            \"size\": \"\"\n" +
			"          }\n" +
			"        ],\n" +
			"        \"origin\": {\n" +
			"          \"id\": \"373e0438854402794e7fc5c3e2feb5361b267fedcd6dfbd102b07a2882932e3a\",\n" +
			"          \"type\": \"Device\"\n" +
			"        }\n" +
			"      }\n" +
			"    ]\n" +
			"  },\n" +
			"  \"syncts\": 1578979437523,\n" +
			"  \"@timestamp\": \"2020-01-14T05:23:57.523Z\",\n" +
			"  \"flags\": {\n" +
			"    \"tv_processed\": true,\n" +
			"    \"dd_processed\": true\n" +
			"  },\n" +
			"  \"type\": \"events\"\n" +
			"}";


	public static final String VALID_SHARE_EVENT_WITHOU_SIZE = "{\n" +
			"  \"eid\": \"SHARE\",\n" +
			"  \"ets\": 1578979334609,\n" +
			"  \"ver\": \"3.0\",\n" +
			"  \"mid\": \"SHARE:336bd87993a20d2dc14a56d2ff197111\",\n" +
			"  \"actor\": {\n" +
			"    \"id\": \"373e0438854402794e7fc5c3e2feb5361b267fedcd6dfbd102b07a2882932e3a\",\n" +
			"    \"type\": \"User\"\n" +
			"  },\n" +
			"  \"context\": {\n" +
			"    \"channel\": \"01231711180382208027\",\n" +
			"    \"pdata\": {\n" +
			"      \"id\": \"staging.diksha.desktop\",\n" +
			"      \"ver\": \"1.0.3\",\n" +
			"      \"pid\": \"desktop.app\"\n" +
			"    },\n" +
			"    \"env\": \"Content\",\n" +
			"    \"sid\": \"d17137fd-224a-49bd-834c-0e9d7a21ca18\",\n" +
			"    \"did\": \"373e0438854402794e7fc5c3e2feb5361b267fedcd6dfbd102b07a2882932e3a\",\n" +
			"    \"cdata\": [],\n" +
			"    \"rollup\": {\n" +
			"      \"l1\": \"01231711180382208027\"\n" +
			"    }\n" +
			"  },\n" +
			"  \"object\": {},\n" +
			"  \"tags\": [\n" +
			"    \"01231711180382208027\"\n" +
			"  ],\n" +
			"  \"edata\": {\n" +
			"    \"dir\": \"Out\",\n" +
			"    \"type\": \"File\",\n" +
			"    \"items\": [\n" +
			"      {\n" +
			"        \"type\": \"TextBook\",\n" +
			"        \"ver\": \"2\",\n" +
			"        \"params\": [\n" +
			"          {\n" +
			"            \"transfers\": \"2\"\n" +
			"          },\n" +
			"          {\n" +
			"            \"size\": \"66438\"\n" +
			"          }\n" +
			"        ],\n" +
			"        \"origin\": {\n" +
			"          \"id\": \"373e0438854402794e7fc5c3e2feb5361b267fedcd6dfbd102b07a2882932e3a\",\n" +
			"          \"type\": \"Device\"\n" +
			"        }\n" +
			"      }\n" +
			"    ]\n" +
			"  },\n" +
			"  \"syncts\": 1578979371914,\n" +
			"  \"@timestamp\": \"2020-01-14T05:22:51.914Z\",\n" +
			"  \"flags\": {\n" +
			"    \"tv_processed\": true,\n" +
			"    \"dd_processed\": true\n" +
			"  },\n" +
			"  \"type\": \"events\"\n" +
			"}";

    public static final String VALID_START_EVENT = "{\n" +
            "  \"ver\": \"3.0\",\n" +
            "  \"eid\": \"START\",\n" +
            "  \"ets\": 1577278681178,\n" +
            "  \"actor\": {\n" +
            "    \"type\": \"User\",\n" +
            "    \"id\": \"7c3ea1bb-4da1-48d0-9cc0-c4f150554149\"\n" +
            "  },\n" +
            "  \"context\": {\n" +
            "    \"cdata\": [\n" +
            "      {\n" +
            "        \"id\": \"1bfd99b0-2716-11ea-b7cc-13dec7acd2be\",\n" +
            "        \"type\": \"API\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"SearchResult\",\n" +
            "        \"type\": \"Section\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"channel\": \"505c7c48ac6dc1edc9b08f21db5a571d\",\n" +
            "    \"pdata\": {\n" +
            "      \"id\": \"prod.diksha.app\",\n" +
            "      \"pid\": \"sunbird.app\",\n" +
            "      \"ver\": \"2.3.162\"\n" +
            "    },\n" +
            "    \"env\": \"app\",\n" +
            "    \"sid\": \"82e41d87-e33f-4269-aeae-d56394985599\",\n" +
            "    \"did\": \"1b17c32bad61eb9e33df281eecc727590d739b2b\"\n" +
            "  },\n" +
            "  \"edata\": {\n" +
            "    \"dir\": \"In\",\n" +
            "    \"type\": \"File\",\n" +
            "    \"items\": [\n" +
            "      {\n" +
            "        \"origin\": {\n" +
            "          \"id\": \"1b17c32bad61eb9e33df281eecc727590d739b2b\",\n" +
            "          \"type\": \"Device\"\n" +
            "        },\n" +
            "        \"id\": \"do_312785709424099328114191\",\n" +
            "        \"type\": \"CONTENT\",\n" +
            "        \"ver\": \"1\",\n" +
            "        \"params\": [\n" +
            "          {\n" +
            "            \"transfers\": 0,\n" +
            "            \"size\": 21084308\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"origin\": {\n" +
            "          \"id\": \"1b17c32bad61eb9e33df281eecc727590d739b2b\",\n" +
            "          \"type\": \"Device\"\n" +
            "        },\n" +
            "        \"id\": \"do_31277435209002188818711\",\n" +
            "        \"type\": \"CONTENT\",\n" +
            "        \"ver\": \"18\",\n" +
            "        \"params\": [\n" +
            "          {\n" +
            "            \"transfers\": 12,\n" +
            "            \"size\": \"123\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"origin\": {\n" +
            "          \"id\": \"1b17c32bad61eb9e33df281eecc727590d739b2b\",\n" +
            "          \"type\": \"Device\"\n" +
            "        },\n" +
            "        \"id\": \"do_31278794857559654411554\",\n" +
            "        \"type\": \"TextBook\",\n" +
            "        \"ver\": \"1\"\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"object\": {\n" +
            "    \"id\": \"do_312528116260749312248818\",\n" +
            "    \"type\": \"TextBook\",\n" +
            "    \"version\": \"10\",\n" +
            "    \"rollup\": {}\n" +
            "  },\n" +
            "  \"mid\": \"02ba33e5-15fe-4ec5-b360-3d03429fae84\",\n" +
            "  \"syncts\": 1577278682630,\n" +
            "  \"@timestamp\": \"2019-12-25T12:58:02.630Z\",\n" +
            "  \"flags\": {\n" +
            "    \"tv_processed\": true,\n" +
            "    \"dd_processed\": true\n" +
            "  },\n" +
            "  \"type\": \"events\"\n" +
            "}";


    public static final String INVALID_SHARE_EVENT = "{\n" +
			"  \"ver\": \"3.0\",\n" +
			"  \"eid\": \"SHARE\",\n" +
			"  \"ets\": 1577278681178,\n" +
			"  \"actor\": {\n" +
			"    \"type\": \"User\",\n" +
			"    \"id\": \"7c3ea1bb-4da1-48d0-9cc0-c4f150554149\"\n" +
			"  },\n" +
			"  \"context\": {\n" +
			"    \"cdata\": [\n" +
			"      {\n" +
			"        \"id\": \"1bfd99b0-2716-11ea-b7cc-13dec7acd2be\",\n" +
			"        \"type\": \"API\"\n" +
			"      },\n" +
			"      {\n" +
			"        \"id\": \"SearchResult\",\n" +
			"        \"type\": \"Section\"\n" +
			"      }\n" +
			"    ],\n" +
			"    \"channel\": \"505c7c48ac6dc1edc9b08f21db5a571d\",\n" +
			"    \"pdata\": {\n" +
			"      \"id\": \"prod.diksha.app\",\n" +
			"      \"pid\": \"sunbird.app\",\n" +
			"      \"ver\": \"2.3.162\"\n" +
			"    },\n" +
			"    \"env\": \"app\",\n" +
			"    \"sid\": \"82e41d87-e33f-4269-aeae-d56394985599\",\n" +
			"    \"did\": \"1b17c32bad61eb9e33df281eecc727590d739b2b\"\n" +
			"  },\n" +
			"  \"edata\": {\n" +
			"    \"dir\": \"In\",\n" +
			"    \"type\": \"File\",\n" +
			"    \"items\": \"test\"\n" +
			"  },\n" +
			"  \"object\": {\n" +
			"    \"id\": \"do_312528116260749312248818\",\n" +
			"    \"type\": \"TextBook\",\n" +
			"    \"version\": \"10\",\n" +
			"    \"rollup\": {}\n" +
			"  },\n" +
			"  \"mid\": \"02ba33e5-15fe-4ec5-b360-3d03429fae84\",\n" +
			"  \"syncts\": 1577278682630,\n" +
			"  \"@timestamp\": \"2019-12-25T12:58:02.630Z\",\n" +
			"  \"flags\": {\n" +
			"    \"tv_processed\": true,\n" +
			"    \"dd_processed\": true\n" +
			"  },\n" +
			"  \"type\": \"events\"\n" +
			"}";

    public static final String INVALID_EVENT = "{\n" +
			"  \"ver\": \"3.0\",\n" +
			"  \"eid\": \"SHARE\",\n" +
			"  \"ets\": 1577278681178,\n" +
			"  \"actor\": {\n" +
			"    \"type\": \"User\",\n" +
			"    \"id\": \"7c3ea1bb-4da1-48d0-9cc0-c4f150554149\"\n" +
			"  },\n" +
			"  \"context\": {\n" +
			"    \"cdata\": [\n" +
			"      {\n" +
			"        \"id\": \"1bfd99b0-2716-11ea-b7cc-13dec7acd2be\",\n" +
			"        \"type\": \"API\"\n" +
			"      },\n" +
			"      {\n" +
			"        \"id\": \"SearchResult\",\n" +
			"        \"type\": \"Section\"\n" +
			"      }\n" +
			"    ],\n" +
			"    \"channel\": \"505c7c48ac6dc1edc9b08f21db5a571d\",\n" +
			"    \"pdata\": {\n" +
			"      \"id\": \"prod.diksha.app\",\n" +
			"      \"pid\": \"sunbird.app\",\n" +
			"      \"ver\": \"2.3.162\"\n" +
			"    },\n" +
			"    \"env\": \"app\",\n" +
			"    \"sid\": \"82e41d87-e33f-4269-aeae-d56394985599\",\n" +
			"    \"did\": \"1b17c32bad61eb9e33df281eecc727590d739b2b\"\n" +
			"  },\n" +
			"  \"edata\": {\n" +
			"    \"dir\": \"In\",\n" +
			"    \"type\": \"File\",\n" +
			"    \"items\": [\n" +
			"      {\n" +
			"        \"origin\": {\n" +
			"          \"id\": \"1b17c32bad61eb9e33df281eecc727590d739b2b\",\n" +
			"          \"type\": \"Device\"\n" +
			"        },\n" +
			"        \"id\": \"do_312785709424099328114191\",\n" +
			"        \"type\": \"CONTENT\",\n" +
			"        \"ver\": \"1\",\n" +
			"        \"params\": [\n" +
			"          {\n" +
			"            \"size\": 21084308\n" +
			"          }\n" +
			"        ]\n" +
			"      },\n" +
			"      {\n" +
			"        \"origin\": {\n" +
			"          \"id\": \"1b17c32bad61eb9e33df281eecc727590d739b2b\",\n" +
			"          \"type\": \"Device\"\n" +
			"        },\n" +
			"        \"id\": \"do_31277435209002188818711\",\n" +
			"        \"type\": \"CONTENT\",\n" +
			"        \"ver\": \"18\",\n" +
			"        \"params\": [\n" +
			"          {\n" +
			"            \"transfers\": 12,\n" +
			"            \"size\": \"123\"\n" +
			"          }\n" +
			"        ]\n" +
			"      },\n" +
			"      {\n" +
			"        \"origin\": {\n" +
			"          \"id\": \"1b17c32bad61eb9e33df281eecc727590d739b2b\",\n" +
			"          \"type\": \"Device\"\n" +
			"        },\n" +
			"        \"id\": \"do_31278794857559654411554\",\n" +
			"        \"type\": \"TextBook\",\n" +
			"        \"ver\": \"1\"\n" +
			"      }\n" +
			"    ]\n" +
			"  },\n" +
			"  \"object\": {\n" +
			"    \"id\": \"do_312528116260749312248818\",\n" +
			"    \"type\": \"TextBook\",\n" +
			"    \"version\": \"10\",\n" +
			"    \"rollup\": {}\n" +
			"  },\n" +
			"  \"mid\": \"02ba33e5-15fe-4ec5-b360-3d03429fae84\",\n" +
			"  \"syncts\": 1577278682630,\n" +
			"  \"@timestamp\": \"2019-12-25T12:58:02.630Z\",\n" +
			"  \"flags\": {\n" +
			"    \"tv_processed\": true,\n" +
			"    \"dd_processed\": true\n" +
			"  },\n" +
			"  \"type\": \"events\"\n" +
			"}";

	public static Map<String, Object> getMap(String message) {
		return (Map<String, Object>) new Gson().fromJson(message, Map.class);
	}
	
}
