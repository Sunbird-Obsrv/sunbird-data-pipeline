package org.ekstep.ep.samza.fixtures;

public class EventFixture {
    public static final String JSON = "{\n" +
            "     \"eid\": \"ME_USER_GAME_LEVEL\",\n" +
            "     \"ts\": \"2015-07-24T12:07:35+05:30\",\n" +
            "     \"ver\": \"1.0\",\n" +
            "     \"uid\": \"3ee97c77-3587-4394-aff6-32b12576fcf6\",\n" +
            "     \"did\": \"9aa9291822a0f3df7f91f36a4cca8445ebb458a8\",\n" +
            "     \"gdata\": { \n" +
            "          \"id\":\"org.eks.lit_screener\",\n" +
            "          \"ver\":\"1.0\"\n" +
            "     },\n" +
            "     \"cid\": \"LT1\",\n" +
            "     \"ctype\": \"LT\",\n" +
            "     \"pdata\": {\n" +
            "          \"id\":\"ASSESMENT_PIPELINE\",\n" +
            "          \"mod\":\"LiteraryScreenerAssessment\",\n" +
            "          \"ver\":\"1.0\"\n" +
            "     },\n" +
            "     \"edata\": {\n" +
            "          \"eks\": {\n" +
            "              \"current_level\":\"Primary 5\",\n" +
            "              \"score\":\"15\"\n" +
            "          }\n" +
            "     }\n" +
            "}";

    public static final String JSON_WITH_MID = "{\n" +
            "     \"eid\": \"ME_USER_GAME_LEVEL\",\n" +
            "     \"ts\": \"2015-07-24T12:07:35+05:30\",\n" +
            "     \"ver\": \"1.0\",\n" +
            "     \"uid\": \"3ee97c77-3587-4394-aff6-32b12576fcf6\",\n" +
            "     \"did\": \"9aa9291822a0f3df7f91f36a4cca8445ebb458a8\",\n" +
            "     \"mid\": \"4aa9291822a0f3df7f91f36a4cca8445ebb458a4\",\n" +
            "     \"gdata\": { \n" +
            "          \"id\":\"org.eks.lit_screener\",\n" +
            "          \"ver\":\"1.0\"\n" +
            "     },\n" +
            "     \"cid\": \"LT1\",\n" +
            "     \"ctype\": \"LT\",\n" +
            "     \"pdata\": {\n" +
            "          \"id\":\"ASSESMENT_PIPELINE\",\n" +
            "          \"mod\":\"LiteraryScreenerAssessment\",\n" +
            "          \"ver\":\"1.0\"\n" +
            "     },\n" +
            "     \"edata\": {\n" +
            "          \"eks\": {\n" +
            "              \"current_level\":\"Primary 5\",\n" +
            "              \"score\":\"15\"\n" +
            "          }\n" +
            "     }\n" +
            "}";
}
