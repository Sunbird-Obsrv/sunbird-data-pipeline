package org.ekstep.ep.samza.service.Fixtures;

public class EventFixture {

    public static final String AUDIT_EVENT = "{\"eid\":\"AUDIT\",\"ets\":1.573121861118E12,\"ver\":\"3.0\",\"mid\":\"1573121861118.40f9136b-1cc3-458d-a04a-4459606dfdd6\",\"actor\":{\"id\":\"627a431d-4f5c-4adc-812d-1f01c5588555\",\"type\":\"User\"},\"context\":{\"channel\":\"01285019302823526477\",\"pdata\":{\"id\":\"dev.sunbird.portal\",\"pid\":\"learner-service\",\"ver\":\"2.5.0\"},\"env\":\"User\",\"did\":\"2bcfc645e27e64625f7bad6ce282f9d0\",\"cdata\":[{\"id\":\"25cb0530-7c52-ecb1-cff2-6a14faab7910\",\"type\":\"SignupType\"}],\"rollup\":{\"l1\":\"01285019302823526477\"}},\"object\":{\"id\":\"627a431d-4f5c-4adc-812d-1f01c5588555\",\"type\":\"User\"},\"edata\":{\"state\":\"Update\",\"props\":[\"recoveryEmail\",\"recoveryPhone\",\"userId\",\"id\",\"externalIds\",\"updatedDate\",\"updatedBy\"]},\"syncts\":1.573121861125E12,\"@timestamp\":\"2019-11-07T10:17:41.125Z\",\"flags\":{\"tv_processed\":true,\"dd_processed\":true},\"type\":\"events\",\"ts\":\"2019-11-07T10:17:41.118+0000\"}";
    public static final String AUDIT_EVENT2 = "{\"eid\":\"AUDIT\",\"ets\":1.573121861118E12,\"ver\":\"3.0\",\"mid\":\"1573121861118.40f9136b-1cc3-458d-a04a-4459606df\",\"actor\":{\"id\":\"5609876543234567890987654345678\",\"type\":\"Request\"},\"context\":{\"channel\":\"01285019302823526477\",\"pdata\":{\"id\":\"dev.sunbird.portal\",\"pid\":\"learner-service\",\"ver\":\"2.5.0\"},\"env\":\"User\",\"did\":\"2bcfc645e27e64625f7bad6ce282f9d0\",\"cdata\":[{\"id\":\"25cb0530-7c52-ecb1-cff2-6a14faab7910\",\"type\":\"SignupType\"}],\"rollup\":{\"l1\":\"01285019302823526477\"}},\"object\":{\"id\":\"5609876543h2fd34h5678jf909876af54345678\"},\"edata\":{\"state\":\"Update\",\"props\":[\"recoveryEmail\",\"recoveryPhone\",\"userId\",\"id\",\"externalIds\",\"updatedDate\",\"updatedBy\"]},\"syncts\":1.573121861125E12,\"@timestamp\":\"2019-11-07T10:17:41.125Z\",\"flags\":{\"tv_processed\":true,\"dd_processed\":true},\"type\":\"events\",\"ts\":\"2019-11-07T10:17:41.118+0000\"}";
    public static final String AUDIT_EVENT3 = "{\"eid\":\"AUDIT\",\"ets\":1.573121861118E12,\"ver\":\"3.0\",\"mid\":\"1573121861118.40f9136b-1cc3-458d-a04a-4459606df\",\"actor\":{\"id\":\"5609876543234567890987654345678\",\"type\":\"Request\"},\"context\":{\"channel\":\"01285019302823526477\",\"pdata\":{\"id\":\"dev.sunbird.portal\",\"pid\":\"learner-service\",\"ver\":\"2.5.0\"},\"env\":\"User\",\"did\":\"2bcfc645e27e64625f7bad6ce282f9d0\",\"rollup\":{\"l1\":\"01285019302823526477\"}},\"object\":{\"type\":\"User\",\"id\":\"5609876543h2fd34h5678jf909876af54345678\"},\"edata\":{\"state\":\"Create\",\"props\":[\"recoveryEmail\",\"recoveryPhone\",\"userId\",\"id\",\"externalIds\",\"updatedDate\",\"updatedBy\"]},\"syncts\":1.573121861125E12,\"@timestamp\":\"2019-11-07T10:17:41.125Z\",\"flags\":{\"tv_processed\":true,\"dd_processed\":true},\"type\":\"events\",\"ts\":\"2019-11-07T10:17:41.118+0000\"}";

    public static final String AUDIT_EVENT_SIGINTYPE="{\n" +
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
            "  \"ets\": 1561739226844,\n" +
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
            "        \"id\": \"34881c3a-8b92-4a3c-a982-7f946137cb09\"\n" +
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
            "        \"id\": \"91f3c280-99c1-11e9-956e-6b6ef71ed575\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"rollup\": {\n" +
            "      \"l1\": \"0126684405014528002\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"mid\": \"1561739226844.e0048ef8-a01e-4780-8c83-e571f28c53c8\",\n" +
            "  \"object\": {\n" +
            "    \"type\": \"User\",\n" +
            "    \"id\": \"89490534-126f-4f0b-82ac-3ff3e49f3468\"\n" +
            "  },\n" +
            "  \"syncts\": 1561739243532,\n" +
            "  \"@timestamp\": \"2019-06-28T16:27:23.532Z\",\n" +
            "  \"flags\": {\n" +
            "    \"tv_processed\": true\n" +
            "  },\n" +
            "  \"type\": \"events\"\n" +
            "}";

    public static final String AUDIT_EVENT_SIGIN_TYPE="{\n" +
            "  \"actor\": {\n" +
            "    \"type\": \"Consumer\",\n" +
            "    \"id\": \"89490534-126f-4f0b-82ac-3ff3e49f3468\"\n" +
            "  },\n" +
            "  \"eid\": \"AUDIT\",\n" +
            "  \"edata\": {\n" +
            "    \"state\": \"Created\",\n" +
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
            "  \"ets\": 1561739226844,\n" +
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
            "        \"id\": \"34881c3a-8b92-4a3c-a982-7f946137cb09\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"SignupType\",\n" +
            "        \"id\": \"sso\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Source\",\n" +
            "        \"id\": \"android\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Request\",\n" +
            "        \"id\": \"91f3c280-99c1-11e9-956e-6b6ef71ed575\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"rollup\": {\n" +
            "      \"l1\": \"0126684405014528002\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"mid\": \"1561739226844.e0048ef8-a01e-4780-8c83-e571f28c53c8\",\n" +
            "  \"object\": {\n" +
            "    \"type\": \"User\",\n" +
            "    \"id\": \"89490534-126f-4f0b-82ac-3ff3e49f3468\"\n" +
            "  },\n" +
            "  \"syncts\": 1561739243532,\n" +
            "  \"@timestamp\": \"2019-06-28T16:27:23.532Z\",\n" +
            "  \"flags\": {\n" +
            "    \"tv_processed\": true\n" +
            "  },\n" +
            "  \"type\": \"events\"\n" +
            "}";

    public static final String AUDIT_EVENT_NULL_USERID ="{\n" +
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
            "  \"ets\": 1561739226844,\n" +
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
            "        \"id\": \"34881c3a-8b92-4a3c-a982-7f946137cb09\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"SignupType\",\n" +
            "        \"id\": \"sso\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Source\",\n" +
            "        \"id\": \"android\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Request\",\n" +
            "        \"id\": \"91f3c280-99c1-11e9-956e-6b6ef71ed575\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"rollup\": {\n" +
            "      \"l1\": \"0126684405014528002\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"mid\": \"1561739226844.e0048ef8-a01e-4780-8c83-e571f28c53c8\",\n" +
            "  \"object\": {\n" +
            "    \"type\": \"User\"\n" +
            "  },\n" +
            "  \"syncts\": 1561739243532,\n" +
            "  \"@timestamp\": \"2019-06-28T16:27:23.532Z\",\n" +
            "  \"flags\": {\n" +
            "    \"tv_processed\": true\n" +
            "  },\n" +
            "  \"type\": \"events\"\n" +
            "}";

    public static final String AUDIT_EVENT_SIGN_IN="{\n" +
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
            "  \"ets\": 1561739226844,\n" +
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
            "        \"id\": \"34881c3a-8b92-4a3c-a982-7f946137cb09\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"SignupType\",\n" +
            "        \"id\": \"sso\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Source\",\n" +
            "        \"id\": \"android\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Request\",\n" +
            "        \"id\": \"91f3c280-99c1-11e9-956e-6b6ef71ed575\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"rollup\": {\n" +
            "      \"l1\": \"0126684405014528002\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"mid\": \"1561739226844.e0048ef8-a01e-4780-8c83-e571f28c53c8\",\n" +
            "  \"object\": {\n" +
            "    \"type\": \"User\",\n" +
            "    \"id\": \"89490534-126f-4f0b-82ac-3ff3e49f3468\"\n" +
            "  },\n" +
            "  \"syncts\": 1561739243532,\n" +
            "  \"@timestamp\": \"2019-06-28T16:27:23.532Z\",\n" +
            "  \"flags\": {\n" +
            "    \"tv_processed\": true\n" +
            "  },\n" +
            "  \"type\": \"events\"\n" +
            "}";
    public static final String AUDIT_EVENT_LOGININTYPE ="{\n" +
            "  \"actor\": {\n" +
            "    \"type\": \"System\",\n" +
            "    \"id\": \"3b46b4c9-3a10-439a-a2cb-feb5435b3a0d\"\n" +
            "  },\n" +
            "  \"eid\": \"AUDIT\",\n" +
            "  \"edata\": {\n" +
            "    \"state\": \"Update\",\n" +
            "    \"props\": [\n" +
            "      \"medium\",\n" +
            "      \"board\",\n" +
            "      \"grade\",\n" +
            "      \"syllabus\",\n" +
            "      \"gradeValue\"\n" +
            "    ]\n" +
            "  },\n" +
            "  \"ver\": \"3.0\",\n" +
            "  \"ets\": 1561739240727,\n" +
            "  \"context\": {\n" +
            "    \"pdata\": {\n" +
            "      \"pid\": \"sunbird.app\",\n" +
            "      \"ver\": \"2.1.92\",\n" +
            "      \"id\": \"prod.diksha.app\"\n" +
            "    },\n" +
            "    \"channel\": \"505c7c48ac6dc1edc9b08f21db5a571d\",\n" +
            "    \"env\": \"sdk\",\n" +
            "    \"did\": \"010612971a80a7677d0a3e849ab35cb4a83157de\",\n" +
            "    \"cdata\": [\n" +
            "      {\n" +
            "        \"type\": \"UserRole\",\n" +
            "        \"id\": \"student\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"sid\": \"ea68a05e-0843-4c06-9a84-9b98cd974724\"\n" +
            "  },\n" +
            "  \"mid\": \"0268860e-76b0-4b4e-b99b-ebf543e7a9d8\",\n" +
            "  \"object\": {\n" +
            "    \"id\": \"3b46b4c9-3a10-439a-a2cb-feb5435b3a0d\",\n" +
            "    \"type\": \"User\",\n" +
            "    \"version\": \"\",\n" +
            "    \"rollup\": {}\n" +
            "  },\n" +
            "  \"syncts\": 1561739245463,\n" +
            "  \"@timestamp\": \"2019-06-28T16:27:25.463Z\",\n" +
            "  \"flags\": {\n" +
            "    \"tv_processed\": true\n" +
            "  },\n" +
            "  \"type\": \"events\"\n" +
            "}";

    public static final String AUDIT_EVENT_METADATA_UPDATED = "{\n" +
            "  \"eid\": \"AUDIT\",\n" +
            "  \"ets\": 1571297660511,\n" +
            "  \"ver\": \"3.0\",\n" +
            "  \"mid\": \"1571297660511.32f5024a-aa30-4c82-abd8-bb8d8914ed2d\",\n" +
            "  \"actor\": {\n" +
            "    \"id\": \"ef70da5a-bb99-4785-b970-1d6d6ee75aad\",\n" +
            "    \"type\": \"User\"\n" +
            "  },\n" +
            "  \"context\": {\n" +
            "    \"channel\": \"01285019302823526477\",\n" +
            "    \"pdata\": {\n" +
            "      \"id\": \"dev.sunbird.portal\",\n" +
            "      \"pid\": \"learner-service\",\n" +
            "      \"ver\": \"2.4.0\"\n" +
            "    },\n" +
            "    \"env\": \"User\",\n" +
            "    \"did\": \"bfaf115f65a2086b062735885f4d2f1a\",\n" +
            "    \"cdata\": [\n" +
            "      {\n" +
            "        \"id\": \"1b1392bc-39d8-6e47-d7d6-5781a2f1481a\",\n" +
            "        \"type\": \"Request\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"rollup\": {\n" +
            "      \"l1\": \"01285019302823526477\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"object\": {\n" +
            "    \"id\": \"52226956-61d8-4c1b-b115-c660111866d3\",\n" +
            "    \"type\": \"User\"\n" +
            "  },\n" +
            "  \"edata\": {\n" +
            "    \"state\": \"Updated\",\n" +
            "    \"props\": [\n" +
            "      \"firstName\",\n" +
            "      \"userId\",\n" +
            "      \"id\",\n" +
            "      \"externalIds\",\n" +
            "      \"locationIds\",\n" +
            "      \"updatedDate\",\n" +
            "      \"updatedBy\"\n" +
            "    ]\n" +
            "  },\n" +
            "  \"syncts\": 1571297660521,\n" +
            "  \"@timestamp\": \"2019-10-17T07:34:20.521Z\",\n" +
            "  \"flags\": {\n" +
            "    \"tv_processed\": true,\n" +
            "    \"dd_processed\": true\n" +
            "  },\n" +
            "  \"type\": \"events\",\n" +
            "  \"ts\": \"2019-10-17T07:34:20.511+0000\"\n" +
            "}";

    public static final String ANY_STRING = "Any String";
}
