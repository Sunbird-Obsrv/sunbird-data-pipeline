package org.ekstep.ep.samza.fixtures;

import com.google.gson.Gson;

import java.util.Map;
import java.util.UUID;


public class EventFixture {
    public String OTHER_EVENT = "{\n" +
            "     \"eid\": \"OTHER_EVENTS\",\n" +
            "     \"ts\": \"2015-07-24T12:07:35+05:30\",\n" +
            "     \"ver\": \"1.0\",\n" +
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
            "            \"language\": \"ML\"\n" +
            "        }\n" +
            "    }\n" +
            "}";

    public String CREATE_PROFILE_EVENT_WITH_AGE = "{\n" +
            "    \"eid\": \"GE_CREATE_PROFILE\",\n" +
            "    \"ts\": \"2014-04-23T10:23:46+05:30\",\n" +
            "    \"ver\": \"1.0\",\n" +
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
            "            \"language\": \"ML\"\n" +
            "        }\n" +
            "    }\n" +
            "}";

    public String INVALID_CREATE_EVENT = "{\n" +
            "     \"eid\": \"GE_CREATE_USER\",\n" +
            "     \"ts\": \"2015-07-24T12:07:35+05:30\",\n" +
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
            "        }\n" +
            "    }\n" +
            "}";

    public String UPDATE_PROFILE_EVENT = "{\n" +
                "    \"eid\": \"GE_UPDATE_PROFILE\",\n" +
                "    \"ts\": \"2015-04-23T10:23:46+05:30\",\n" +
                "    \"ver\": \"1.0\",\n" +
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
                "            \"language\": \"ML\"\n" +
                "        }\n" +
                "    }\n" +
                "}";


    public String CREATE_PROFILE_EVENT_1(String uid) {
        return "{\n" +
                "    \"eid\": \"GE_UPDATE_PROFILE\",\n" +
                "    \"ts\": \"2015-04-23T10:23:46+05:30\",\n" +
                "    \"ver\": \"1.0\",\n" +
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
                "            \"language\": \"ML\"\n" +
                "        }\n" +
                "    }\n" +
                "}";
    }

    public String UPDATE_PROFILE_EVENT_1(String uid) {
        return "{\n" +
                "    \"eid\": \"GE_UPDATE_PROFILE\",\n" +
                "    \"ts\": \"2015-04-23T10:23:46+05:30\",\n" +
                "    \"ver\": \"1.0\",\n" +
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
                "            \"language\": \"EL\"\n" +
                "        }\n" +
                "    }\n" +
                "}";
    }

    private String getRandomUID(){
        return UUID.randomUUID().toString();
    }
}
