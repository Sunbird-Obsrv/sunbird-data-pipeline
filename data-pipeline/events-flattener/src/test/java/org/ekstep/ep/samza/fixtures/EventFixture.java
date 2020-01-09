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
			"  \"mid\": \"02ba33e5-15fe-4ec5-b360-3d03429fae84\",\n" +
			"  \"syncts\": 1577278682630,\n" +
			"  \"@timestamp\": \"2019-12-25T12:58:02.630Z\",\n" +
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
			"  \"actor\": \n" +
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

	public static Map<String, Object> getMap(String message) {
		return (Map<String, Object>) new Gson().fromJson(message, Map.class);
	}
	
}
