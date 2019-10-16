package org.ekstep.ep.samza.service.Fixtures;

public class EventFixture {
    public static final String OBJECT_TYPE_CONTENT_EVENT_1 = "{\"ets\":1548780816826,\"channel\":\"in.ekstep\",\"transactionData\":{\"properties\":{\"ownershipType\":{\"ov\":null,\"nv\":[\"createdBy\"]},\"code\":{\"ov\":null,\"nv\":\"testbook1\"},\"channel\":{\"ov\":null,\"nv\":\"in.ekstep\"},\"description\":{\"ov\":null,\"nv\":\"TestCollection\"},\"status\":{\"ov\":null,\"nv\":\"Draft\"}}},\"label\":\"TestBook1\",\"nodeType\":\"DATA_NODE\",\"userId\":\"ANONYMOUS\",\"createdOn\":\"2019-01-29T22:23:36.826+0530\",\"objectType\":\"Content\",\"nodeUniqueId\":\"do_11268761245100441611\",\"requestId\":null,\"operationType\":\"CREATE\",\"nodeGraphId\":703654,\"graphId\":\"domain\"}";
    public static final String OBJECT_TYPE_CONTENT_EVENT_1_UPDATED = "{\"ets\":1548780816826,\"channel\":\"sunbird.portal\",\"transactionData\":{\"properties\":{\"ownershipType\":{\"ov\":null,\"nv\":null},\"code\":{\"ov\":null,\"nv\":\"testbook1\"},\"channel\":{\"ov\":null,\"nv\":\"sunbird.portal\"},\"status\":{\"ov\":null,\"nv\":\"Live\"},\"visibility\":{\"ov\":null,\"nv\":\"Default\"}}},\"label\":\"TestBook1\",\"nodeType\":\"DATA_NODE\",\"userId\":\"ANONYMOUS\",\"createdOn\":\"2019-01-29T22:23:36.826+0530\",\"objectType\":\"Content\",\"nodeUniqueId\":\"do_11268761245100441611\",\"requestId\":null,\"operationType\":\"CREATE\",\"nodeGraphId\":703654,\"graphId\":\"domain\"}";
    public static final String OBJECT_TYPE_DIAL_CODE_1 = "{\"nodeUniqueId\":\"YC9EP8\",\"ets\":1549345256766,\"requestId\":null,\"audit\":false,\"transactionData\":{\"properties\":{\"dialcode_index\":{\"ov\":null,\"nv\":261351.0},\"identifier\":{\"ov\":null,\"nv\":\"YC9EP8\"},\"channel\":{\"ov\":null,\"nv\":\"b00bc992ef25f1a9a8d63291e20efc8d\"},\"batchcode\":{\"ov\":null,\"nv\":\"do_112692236142379008136\"},\"publisher\":{\"ov\":null,\"nv\":null},\"generated_on\":{\"ov\":null,\"nv\":\"2019-02-05T05:40:56.762\"},\"status\":{\"ov\":null,\"nv\":\"Draft\"}}},\"channel\":\"in.ekstep\",\"index\":true,\"operationType\":\"CREATE\",\"nodeType\":\"EXTERNAL\",\"userId\":\"ANONYMOUS\",\"createdOn\":\"2019-02-05T05:40:56.766+0000\",\"objectType\":\"DialCode\"}";
    public static final String CONTENT_EVENT_EMPTY_PROPERTIES = "{\"ets\":1548780816826,\"channel\":\"in.ekstep\",\"transactionData\":{\"properties\":{}},\"label\":\"TestBook1\",\"nodeType\":\"DATA_NODE\",\"userId\":\"ANONYMOUS\",\"createdOn\":\"2019-01-29T22:23:36.826+0530\",\"objectType\":\"Content\",\"nodeUniqueId\":\"do_11268761245100441611\",\"requestId\":null,\"operationType\":\"CREATE\",\"nodeGraphId\":703654,\"graphId\":\"domain\"}";
    public static final String CONTENT_EVENT_EMPTY_NODE_UNIQUEID = "{\"ets\":1548780816826,\"channel\":\"in.ekstep\",\"transactionData\":{\"properties\":{\"ownershipType\":{\"ov\":null,\"nv\":[\"createdBy\"]},\"code\":{\"ov\":null,\"nv\":\"testbook1\"},\"channel\":{\"ov\":null,\"nv\":\"in.ekstep\"},\"description\":{\"ov\":null,\"nv\":\"TestCollection\"},\"status\":{\"ov\":null,\"nv\":\"Draft\"}}},\"label\":\"TestBook1\",\"nodeType\":\"DATA_NODE\",\"userId\":\"ANONYMOUS\",\"createdOn\":\"2019-01-29T22:23:36.826+0530\",\"objectType\":\"Content\",\"nodeUniqueId\":\"\",\"requestId\":null,\"operationType\":\"CREATE\",\"nodeGraphId\":703654,\"graphId\":\"domain\"}";
    public static final String OBJECT_TYPE_CONCEPT_EVENT = "{\"ets\":1547644874028,\"channel\":\"in.ekstep\",\"transactionData\":{\"properties\":{\"language\":{\"ov\":null,\"nv\":\"English\"}}},\"label\":\"Properties of Light\",\"nodeType\":\"DATA_NODE\",\"userId\":\"ANONYMOUS\",\"createdOn\":\"2019-01-16T18:51:14.028+0530\",\"objectType\":\"Concept\",\"nodeUniqueId\":\"SMC4\",\"requestId\":null,\"operationType\":\"UPDATE\",\"nodeGraphId\":101409,\"graphId\":\"domain\"}";
    public static final String EMPTY_LANGUAGE_FIELD_EVENT= "{\"ets\":1547644874028,\"channel\":\"in.ekstep\",\"transactionData\":{\"properties\":{\"language\":{\"ov\":null,\"nv\":\"\"}}},\"label\":\"Properties of Light\",\"nodeType\":\"DATA_NODE\",\"userId\":\"ANONYMOUS\",\"createdOn\":\"2019-01-16T18:51:14.028+0530\",\"objectType\":\"Concept\",\"nodeUniqueId\":\"SMC4\",\"requestId\":null,\"operationType\":\"UPDATE\",\"nodeGraphId\":101409,\"graphId\":\"domain\"}";
    public static final String LIST_LANGUAGE_FIELD_EVENT= "{\"ets\":1547644874028,\"channel\":\"in.ekstep\",\"transactionData\":{\"properties\":{\"language\":{\"ov\":null,\"nv\":[\"Kannada\"]}}},\"label\":\"Properties of Light\",\"nodeType\":\"DATA_NODE\",\"userId\":\"ANONYMOUS\",\"createdOn\":\"2019-01-16T18:51:14.028+0530\",\"objectType\":\"Concept\",\"nodeUniqueId\":\"SMC4\",\"requestId\":null,\"operationType\":\"UPDATE\",\"nodeGraphId\":101409,\"graphId\":\"domain\"}";
    public static final String INCORRECT_LIST_TYPE_FIELDS= "{\"ets\":1559670759,\"channel\":\"in.ekstep\",\"transactionData\":{\"properties\":{\"language\":{\"ov\":null,\"nv\":\"Spanish\"},\"subject\":{\"ov\":null,\"nv\":\"CS\"},\"ageGroup\":{\"ov\":null,\"nv\":\"22\"},\"ownershipType\":{\"ov\":null,\"nv\":[\"createdBy\"]}}},\"label\":\"Properties of Light\",\"nodeType\":\"DATA_NODE\",\"userId\":\"ANONYMOUS\",\"createdOn\":\"2019-06-07T18:51:14.028+0530\",\"objectType\":\"Concept\",\"nodeUniqueId\":\"SMC4\",\"requestId\":null,\"operationType\":\"UPDATE\",\"nodeGraphId\":98765,\"graphId\":\"domain\"}";
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
    public static final String AUDIT_EVENT_LOGININTYPE ="{\n" +
            "  \"actor\": {\n" +
            "    \"type\": \"System\",\n" +
            "    \"id\": \"3b46b4c9-3a10-439a-a2cb-feb5435b3a0d\"\n" +
            "  },\n" +
            "  \"eid\": \"AUDIT\",\n" +
            "  \"edata\": {\n" +
            "    \"state\": \"Updated\",\n" +
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
            "  \"actor\": {\n" +
            "    \"type\": \"Consumer\",\n" +
            "    \"id\": \"52226956-61d8-4c1b-b115-c660111866d34\"\n" +
            "  },\n" +
            "  \"eid\": \"AUDIT\",\n" +
            "  \"edata\": {\n" +
            "    \"state\": \"Update\",\n" +
            "    \"props\": [\n" +
            "      \"firstName\",\n" +
            "      \"email\",\n" +
            "      \"locationids\",\n" +
            "      \"emailVerified\"\n" +
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
            "    \"id\": \"52226956-61d8-4c1b-b115-c660111866d3\"\n" +
            "  },\n" +
            "  \"syncts\": 1561739243532,\n" +
            "  \"@timestamp\": \"2019-06-28T16:27:23.532Z\",\n" +
            "  \"flags\": {\n" +
            "    \"tv_processed\": true\n" +
            "  },\n" +
            "  \"type\": \"events\"\n" +
            "}";
}
