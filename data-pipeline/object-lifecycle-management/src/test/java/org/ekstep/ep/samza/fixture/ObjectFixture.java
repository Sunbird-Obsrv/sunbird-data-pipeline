package org.ekstep.ep.samza.fixture;

import com.google.gson.Gson;
import org.ekstep.ep.samza.external.ObjectResponse;

import java.util.Map;

public class ObjectFixture {
    public static final String OBJECT_REQUEST_JSON = "{" +
               "            \"type\": \"User\",\n" +
               "            \"subtype\": \"\",\n" +
               "            \"id\": \"725\",\n" +
               "            \"parentid\": \"\",\n" +
               "            \"code\": \"\",\n" +
               "            \"name\": \"Amit\",\n" +
               "            \"state\": \"Create\",\n" +
               "            \"prevstate\": \"\",\n" +
               "            \"parenttype\": \"\"\n" +
               "        }\n";


    public static final String OBJECT_RESPONSE_JSON = "{\n" + " " +
            "  \"id\": \"ekstep.object-service.create_or_update\",\n" +
            "   \"ver\": \"1.0\",\n" + "   \"ts\": \"\",\n" +
            "   \"params\": {\n" +
                "       \"resmsgid\": \"054f3b10-309f-4552-ae11-02c66640967b\",\n" +
                "       \"msgid\": \"ff305d54-85b4-341b-da2f-eb6b9e5460fa\",\n" +
                "       \"status\": \"successful\",\n" +
                "       \"err\": \"\",\n" +
                "       \"errmsg\": \"\"\n" + "   " +
            "   },\n" +
            "   \"result\": {\n" +
                "      \"objectid\": 123\n" +
            "   }  \n" +
            "}";

    public static final String OBJECT_RESPONSE_FAILURE_JSON = "{\n" + " " +
            "  \"id\": \"ekstep.object-service.create_or_update\",\n" +
            "   \"ver\": \"1.0\",\n" + "   \"ts\": \"\",\n" +
            "   \"params\": {\n" +
            "       \"resmsgid\": \"054f3b10-309f-4552-ae11-02c66640967b\",\n" +
            "       \"msgid\": \"ff305d54-85b4-341b-da2f-eb6b9e5460fa\",\n" +
            "       \"status\": \"failed\",\n" +
            "       \"err\": \"BAD_REQUEST\",\n" +
            "       \"errmsg\": \"TYPE IS MANDATORY, ID IS MANDATORY\"\n" + "   " +
            "   },\n" +
            "   \"result\": {\n" +
            "      \"objectid\": null\n" +
            "   }  \n" +
            "}";

    public static Map<String,Object> getObjectRequest() {
        return new Gson().fromJson(OBJECT_REQUEST_JSON, Map.class);
    }

    public static ObjectResponse getObjectResponse() {return new Gson().fromJson(OBJECT_RESPONSE_JSON, ObjectResponse.class);}

    public static ObjectResponse getFailureResponse() {
        return new Gson().fromJson(OBJECT_RESPONSE_FAILURE_JSON, ObjectResponse.class);
    }
}
