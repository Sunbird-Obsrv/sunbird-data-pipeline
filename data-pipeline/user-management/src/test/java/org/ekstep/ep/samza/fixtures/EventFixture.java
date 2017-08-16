package org.ekstep.ep.samza.fixtures;

import java.util.UUID;


public class EventFixture {
    public String OTHER_EVENT = "{\n" +
            "     \"eid\": \"OTHER_EVENTS\",\n" +
            "     \"ts\": \"2015-07-24T12:07:35+05:30\",\n" +
            "     \"ver\": \"1.0\",\n" +
            "    \"channel\": \"in.ekstep.test\",\n" +
            "     \"edata\": {\n" +
            "          \"eks\": {\n" +
            "              \"uid\":\"" + getRandomUID() + "\"\n" +
            "          }\n" +
            "     }\n" +
            "}";

    public String CREATE_USER_EVENT = "{\n" +
            "     \"eid\": \"GE_CREATE_USER\",\n" +
            "     \"ts\": \"2015-07-24T12:07:35+05:30\",\n" +
            "     \"ver\": \"1.0\",\n" +
            "    \"channel\": \"in.ekstep.test\",\n" +
            "     \"edata\": {\n" +
            "          \"eks\": {\n" +
            "              \"uid\":\"" + getRandomUID() + "\"\n" +
            "          }\n" +
            "     }\n" +
            "}";

    public String CREATE_PROFILE_EVENT = "{\n" +
            "    \"eid\": \"GE_CREATE_PROFILE\",\n" +
            "    \"ts\": \"2015-04-23T10:23:46+05:30\",\n" +
            "    \"ver\": \"1.0\",\n" +
            "    \"channel\": \"in.ekstep.test\",\n" +
            "    \"gdata\": {\n" +
            "        \"id\": \"genie.android\",\n" +
            "        \"ver\": \"1.0\"\n" +
            "    },\n" +
            "    \"sid\": \"\",\n" +
            "    \"uid\": \"ff305d5485b4341bda2feb6b9e5460fa\",\n" +
            "    \"did\": \"eb6b9e5460faff305d5485b4341bda2f\",\n" +
            "    \"edata\": {\n" +
            "        \"eks\": {\n" +
            "            \"uid\":\"+" + getRandomUID() + "\",\n" +
            "            \"handle\": \"user@twitter.com\",\n" +
            "            \"gender\": \"male\",\n" +
            "            \"age\": -1,\n" +
            "            \"standard\": -1.0,\n" +
            "            \"language\": \"ML\",\n" +
            "            \"day\": 12,\n" +
            "            \"month\": 11,\n" +
            "            \"is_group_user\": false\n" +
            "        }\n" +
            "    }\n" +
            "}";

    public String CREATE_GROUP_USER_PROFILE_EVENT = "{\n" +
            "    \"eid\": \"GE_CREATE_PROFILE\",\n" +
            "    \"ts\": \"2015-04-23T10:23:46+05:30\",\n" +
            "    \"ver\": \"1.0\",\n" +
            "    \"channel\": \"in.ekstep.test\",\n" +
            "    \"gdata\": {\n" +
            "        \"id\": \"genie.android\",\n" +
            "        \"ver\": \"1.0\"\n" +
            "    },\n" +
            "    \"sid\": \"\",\n" +
            "    \"uid\": \"ff305d5485b4341bda2feb6b9e5460fa\",\n" +
            "    \"did\": \"eb6b9e5460faff305d5485b4341bda2f\",\n" +
            "    \"edata\": {\n" +
            "        \"eks\": {\n" +
            "            \"uid\":\"+" + getRandomUID() + "\",\n" +
            "            \"handle\": \"user@twitter.com\",\n" +
            "            \"gender\": \"male\",\n" +
            "            \"age\": -1,\n" +
            "            \"standard\": -1.0,\n" +
            "            \"language\": \"ML\",\n" +
            "            \"day\": 12,\n" +
            "            \"month\": 11,\n" +
            "            \"is_group_user\": true\n" +
            "        }\n" +
            "    }\n" +
            "}";

    public String CREATE_PROFILE_EVENT_WITH_AGE = "{\n" +
            "    \"eid\": \"GE_CREATE_PROFILE\",\n" +
            "    \"ts\": \"2014-04-23T10:23:46+05:30\",\n" +
            "    \"ver\": \"1.0\",\n" +
            "    \"channel\": \"in.ekstep.test\",\n" +
            "    \"gdata\": {\n" +
            "        \"id\": \"genie.android\",\n" +
            "        \"ver\": \"1.0\"\n" +
            "    },\n" +
            "    \"sid\": \"\",\n" +
            "    \"uid\": \"ff305d5485b4341bda2feb6b9e5460fa\",\n" +
            "    \"did\": \"eb6b9e5460faff305d5485b4341bda2f\",\n" +
            "    \"edata\": {\n" +
            "        \"eks\": {\n" +
            "            \"uid\":\"+" + getRandomUID() + "\",\n" +
            "            \"handle\": \"user@twitter.com\",\n" +
            "            \"gender\": \"male\",\n" +
            "            \"age\": 10,\n" +
            "            \"standard\": 3,\n" +
            "            \"language\": \"ML\",\n" +
            "            \"day\": 12,\n" +
            "            \"month\": 11,\n" +
            "            \"is_group_user\": false\n" +
            "        }\n" +
            "    }\n" +
            "}";

    public String CREATE_PROFILE_EVENT_WITH_NO_AGE = "{\n" +
            "    \"eid\": \"GE_CREATE_PROFILE\",\n" +
            "    \"ts\": \"2014-04-23T10:23:46+05:30\",\n" +
            "    \"ver\": \"1.0\",\n" +
            "    \"channel\": \"in.ekstep.test\",\n" +
            "    \"gdata\": {\n" +
            "        \"id\": \"genie.android\",\n" +
            "        \"ver\": \"1.0\"\n" +
            "    },\n" +
            "    \"sid\": \"\",\n" +
            "    \"uid\": \"ff305d5485b4341bda2feb6b9e5460fa\",\n" +
            "    \"did\": \"eb6b9e5460faff305d5485b4341bda2f\",\n" +
            "    \"edata\": {\n" +
            "        \"eks\": {\n" +
            "            \"uid\":\"+" + getRandomUID() + "\",\n" +
            "            \"handle\": \"user@twitter.com\",\n" +
            "            \"gender\": \"male\",\n" +
            "            \"standard\": 3,\n" +
            "            \"language\": \"ML\",\n" +
            "            \"day\": 12,\n" +
            "            \"month\": 11,\n" +
            "            \"is_group_user\": false\n" +
            "        }\n" +
            "    }\n" +
            "}";

    public String INVALID_CREATE_EVENT = "{\n" +
            "     \"eid\": \"GE_CREATE_USER\",\n" +
            "     \"ts\": \"2015-07-24T12:07:35+05:30\",\n" +
            "    \"channel\": \"in.ekstep.test\",\n" +
            "     \"ver\": \"1.0\",\n" +
            "     \"edata\": {\n" +
            "          \"eks\": {\n" +
            "          }\n" +
            "     }\n" +
            "}";

    public String INVALID_PROFILE_EVENT = "{\n" +
            "    \"eid\": \"GE_CREATE_PROFILE\",\n" +
            "    \"ts\": \"2015-04-23T10:23:46+05:30\",\n" +
            "    \"ver\": \"1.0\",\n" +
            "    \"channel\": \"in.ekstep.test\",\n" +
            "    \"gdata\": {\n" +
            "        \"id\": \"genie.android\",\n" +
            "        \"ver\": \"1.0\"\n" +
            "    },\n" +
            "    \"sid\": \"\",\n" +
            "    \"uid\": \"ff305d5485b4341bda2feb6b9e5460fa\",\n" +
            "    \"did\": \"eb6b9e5460faff305d5485b4341bda2f\",\n" +
            "    \"edata\": {\n" +
            "        \"eks\": {\n" +
            "            \"uid\":\"" + getRandomUID() + "\",\n" +
            "            \"gender\": \"male\",\n" +
            "            \"age\": -1.0,\n" +
            "            \"standard\": -1.0,\n" +
            "            \"language\": \"ML\"\n" +
            "            \"is_group_user\": false\n" +
            "        }\n" +
            "    }\n" +
            "}";

    public String UPDATE_PROFILE_EVENT = "{\n" +
            "    \"eid\": \"GE_UPDATE_PROFILE\",\n" +
            "    \"ts\": \"2015-04-23T10:23:46+05:30\",\n" +
            "    \"ver\": \"1.0\",\n" +
            "    \"channel\": \"in.ekstep.test\",\n" +
            "    \"gdata\": {\n" +
            "        \"id\": \"genie.android\",\n" +
            "        \"ver\": \"1.0\"\n" +
            "    },\n" +
            "    \"sid\": \"\",\n" +
            "    \"uid\": \"ff305d5485b4341bda2feb6b9e5460fa\",\n" +
            "    \"did\": \"eb6b9e5460faff305d5485b4341bda2f\",\n" +
            "    \"edata\": {\n" +
            "        \"eks\": {\n" +
            "            \"uid\":\"" + getRandomUID() + "\",\n" +
            "            \"handle\": \"user@twitter.com\",\n" +
            "            \"gender\": \"FEMALE\",\n" +
            "            \"age\": 10,\n" +
            "            \"standard\": 5,\n" +
            "            \"language\": \"ML\",\n" +
            "            \"day\": 12,\n" +
            "            \"month\": 11\n" +
            "        }\n" +
            "    }\n" +
            "}";

    public String CREATE_PROFILE_EVENT_WITH_BOARD_AND_MEDIUM = "{\n" +
            "    \"eid\": \"GE_CREATE_PROFILE\",\n" +
            "    \"ts\": \"2014-04-23T10:23:46+05:30\",\n" +
            "    \"ver\": \"1.0\",\n" +
            "    \"channel\": \"in.ekstep.test\",\n" +
            "    \"gdata\": {\n" +
            "        \"id\": \"genie.android\",\n" +
            "        \"ver\": \"1.0\"\n" +
            "    },\n" +
            "    \"sid\": \"\",\n" +
            "    \"uid\": \"ff305d5485b4341bda2feb6b9e5460fa\",\n" +
            "    \"did\": \"eb6b9e5460faff305d5485b4341bda2f\",\n" +
            "    \"edata\": {\n" +
            "        \"eks\": {\n" +
            "            \"uid\":\"+" + getRandomUID() + "\",\n" +
            "            \"handle\": \"user@twitter.com\",\n" +
            "            \"gender\": \"male\",\n" +
            "            \"age\": 10,\n" +
            "            \"standard\": 3,\n" +
            "            \"language\": \"ML\",\n" +
            "            \"day\": 12,\n" +
            "            \"month\": 11,\n" +
            "            \"board\": \"SSLC\",\n" +
            "            \"medium\": \"ma\",\n" +
            "            \"is_group_user\": false\n" +
            "        }\n" +
            "    }\n" +
            "}";

    public String EVENT_WITHOUT_UID = "{\n" +
            "  \"@timestamp\": \"2017-04-13T03:58:33.542Z\",\n" +
            "  \"@version\": \"1\",\n" +
            "    \"channel\": \"in.ekstep.test\",\n" +
            "  \"did\": \"cbeda6a2ef327eaee21008de6495f89476aba58d\",\n" +
            "  \"edata\": {\n" +
            "    \"eks\": {\n" +
            "    }\n" +
            "  },\n" +
            "  \"eid\": \"GE_CREATE_PROFILE\",\n" +
            "  \"key\": \"11bf311d-09b1-47fc-810c-f987bbf55917\",\n" +
            "  \"mid\": \"3BE38CAFADC951C3E87D3BE0FDD06144507BB8921\",\n" +
            "  \"sid\": \"\",\n" +
            "  \"tags\": [],\n" +
            "  \"ts\": \"2017-04-12T18:45:02+05:30\"\n" +
            "}";

    public String CREATE_PROFILE_EVENT_1(String uid) {
        return "{\n" +
                "    \"eid\": \"GE_UPDATE_PROFILE\",\n" +
                "    \"ts\": \"2015-04-23T10:23:46+05:30\",\n" +
                "    \"ver\": \"1.0\",\n" +
                "    \"channel\": \"in.ekstep.test\",\n" +
                "    \"gdata\": {\n" +
                "        \"id\": \"genie.android\",\n" +
                "        \"ver\": \"1.0\"\n" +
                "    },\n" +
                "    \"sid\": \"\",\n" +
                "    \"uid\":\"" + uid + "\",\n" +
                "    \"did\": \"eb6b9e5460faff305d5485b4341bda2f\",\n" +
                "    \"edata\": {\n" +
                "        \"eks\": {\n" +
                "            \"uid\":\"" + uid + "\",\n" +
                "            \"handle\": \"user@twitter.com\",\n" +
                "            \"gender\": \"male\",\n" +
                "            \"age\": -1,\n" +
                "            \"standard\": -1,\n" +
                "            \"language\": \"ML\",\n" +
                "            \"day\": 12,\n" +
                "            \"month\": 11\n" +
                "        }\n" +
                "    }\n" +
                "}";
    }

    public String OTHER_CHANNEL_EVENT = "{\n" +
            "    \"eid\": \"GE_CREATE_PROFILE\",\n" +
            "    \"ts\": \"2015-04-23T10:23:46+05:30\",\n" +
            "    \"ver\": \"1.0\",\n" +
            "    \"channel\": \"in.other.channel\",\n" +
            "    \"gdata\": {\n" +
            "        \"id\": \"genie.android\",\n" +
            "        \"ver\": \"1.0\"\n" +
            "    },\n" +
            "    \"sid\": \"\",\n" +
            "    \"uid\": \"ff305d5485b4341bda2feb6b9e5460fa\",\n" +
            "    \"did\": \"eb6b9e5460faff305d5485b4341bda2f\",\n" +
            "    \"edata\": {\n" +
            "        \"eks\": {\n" +
            "            \"uid\":\"+" + getRandomUID() + "\",\n" +
            "            \"handle\": \"user@twitter.com\",\n" +
            "            \"gender\": \"male\",\n" +
            "            \"age\": -1,\n" +
            "            \"standard\": -1.0,\n" +
            "            \"language\": \"ML\",\n" +
            "            \"day\": 12,\n" +
            "            \"month\": 11,\n" +
            "            \"is_group_user\": false\n" +
            "        }\n" +
            "    }\n" +
            "}";

    public String UPDATE_PROFILE_EVENT_1(String uid) {
        return "{\n" +
                "    \"eid\": \"GE_UPDATE_PROFILE\",\n" +
                "    \"ts\": \"2015-04-23T10:23:46+05:30\",\n" +
                "    \"ver\": \"1.0\",\n" +
                "    \"channel\": \"in.ekstep.test\",\n" +
                "    \"gdata\": {\n" +
                "        \"id\": \"genie.android\",\n" +
                "        \"ver\": \"1.0\"\n" +
                "    },\n" +
                "    \"sid\": \"\",\n" +
                "    \"uid\":\"" + uid + "\",\n" +
                "    \"did\": \"eb6b9e5460faff305d5485b4341bda2f\",\n" +
                "    \"edata\": {\n" +
                "        \"eks\": {\n" +
                "            \"uid\":\"" + uid + "\",\n" +
                "            \"handle\": \"newuser@twitter.com\",\n" +
                "            \"gender\": \"MALE\",\n" +
                "            \"age\": 10,\n" +
                "            \"standard\": 5,\n" +
                "            \"language\": \"EL\",\n" +
                "            \"day\": 12,\n" +
                "            \"month\": 11\n" +
                "        }\n" +
                "    }\n" +
                "}";
    }

    public String UPDATE_PROFILE_EVENT_1_WITH_NOO_AGE(String uid) {
        return "{\n" +
                "    \"eid\": \"GE_UPDATE_PROFILE\",\n" +
                "    \"ts\": \"2015-04-23T10:23:46+05:30\",\n" +
                "    \"ver\": \"1.0\",\n" +
                "    \"channel\": \"in.ekstep.test\",\n" +
                "    \"gdata\": {\n" +
                "        \"id\": \"genie.android\",\n" +
                "        \"ver\": \"1.0\"\n" +
                "    },\n" +
                "    \"sid\": \"\",\n" +
                "    \"uid\":\"" + uid + "\",\n" +
                "    \"did\": \"eb6b9e5460faff305d5485b4341bda2f\",\n" +
                "    \"edata\": {\n" +
                "        \"eks\": {\n" +
                "            \"uid\":\"" + uid + "\",\n" +
                "            \"handle\": \"newuser@twitter.com\",\n" +
                "            \"gender\": \"MALE\",\n" +
                "            \"standard\": 5,\n" +
                "            \"language\": \"EL\",\n" +
                "            \"day\": 12,\n" +
                "            \"month\": 11\n" +
                "        }\n" +
                "    }\n" +
                "}";
    }

    public String UPDATE_PROFILE_EVENT_WITH_BOARD_AND_MEDIUM(String uid) {
        return "{\n" +
                "    \"eid\": \"GE_UPDATE_PROFILE\",\n" +
                "    \"ts\": \"2015-04-23T10:23:46+05:30\",\n" +
                "    \"ver\": \"1.0\",\n" +
                "    \"channel\": \"in.ekstep.test\",\n" +
                "    \"gdata\": {\n" +
                "        \"id\": \"genie.android\",\n" +
                "        \"ver\": \"1.0\"\n" +
                "    },\n" +
                "    \"sid\": \"\",\n" +
                "    \"uid\":\"" + uid + "\",\n" +
                "    \"did\": \"eb6b9e5460faff305d5485b4341bda2f\",\n" +
                "    \"edata\": {\n" +
                "        \"eks\": {\n" +
                "            \"uid\":\"" + uid + "\",\n" +
                "            \"handle\": \"newuser@twitter.com\",\n" +
                "            \"gender\": \"MALE\",\n" +
                "            \"age\": 10,\n" +
                "            \"standard\": 5,\n" +
                "            \"language\": \"EL\",\n" +
                "            \"day\": 12,\n" +
                "            \"board\": \"SSC\",\n" +
                "            \"medium\": \"hindi\",\n" +
                "            \"month\": 11\n" +
                "        }\n" +
                "    }\n" +
                "}";
    }

    private String getRandomUID() {
        return UUID.randomUUID().toString();
    }
}
