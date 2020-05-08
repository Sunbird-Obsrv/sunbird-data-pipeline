package org.ekstep.ep.samza.fixtures;

import com.google.gson.Gson;

import java.util.Map;

public class EventFixture {

  public static final String UNPARSABLE_GE_GENIE_UPDATE_EVENT = "{\n"
      + "  \"did\": \"c270f15d-5230-4954-92aa-d239e4281cc4\",\n"
      + "  \"mid\": \"43288930-e54a-230b-b56e-876gnm8712ok\",\n" + "  \"edata\": {\n" + "    \"eks\": {\n"
      + "      \"mode\": \"WIFI\",\n" + "      \"ver\": \"12\",\n" + "      \"size\": 12.67,\n"
      + "      \"err\": \"\",\n" + "      \"referrer\": [\n" + "        {\n" + "          \"action\": \"INSTALL\",\n"
      + "          \"utmsource\": \"Ekstep\",\n" + "          \"utmmedium\": \"Portal\",\n"
      + "          \"utmterm\": \"December 2016\",\n" + "          \"utmcontent\": \"Ramayana\",\n"
      + "          \"utmcampaign\": \"Epics of India\"\n" + "        }\n" + "      ]\n" + "    }\n" + "  },\n"
      + "  \"eid\": \"GE_GENIE_UPDATE\",\n" + "  \"gdata\": {\n" + "    \"id\": \"genie.android\",\n"
      + "    \"ver\": \"1.0\"\n" + "  },\n" + "  \"sid\": \"\",\n" + "  \"ets\": 1454064092546,\n"
      + "  \"uid\": \"\",\n" + "  \"ver\": \"2.0\",\n" + "  \"cdata\": [\n" + "    {\n"
      + "      \"id\": \"correlationid\",\n" + "      \"type\": \"correlationtype\"\n" + "    ";
  
  public static final String RESPONSE_EVENT = "{\"eid\":\"RESPONSE\",\"ets\":1586759864200,\"ver\":\"3.0\",\"mid\":\"RESPONSE:17d97e7cba61bd7c996d3ae71b9706ef\",\"actor\":{\"id\":\"c5b5732b-b8d6-44ad-bf52-d8f7fc513ac3\",\"type\":\"User\"},\"context\":{\"channel\":\"0126825293972439041\",\"pdata\":{\"id\":\"preprod.diksha.portal\",\"ver\":\"2.8.7\",\"pid\":\"sunbird-portal.contentplayer\"},\"env\":\"contentplayer\",\"sid\":\"zSizgfO7mzdZ--MhlGCzszgdNhKW6MCp\",\"did\":\"a915339fbca0afddbf537a1792f01791\",\"cdata\":[{\"id\":\"28d1f55313c9c14df8ea86ca1dc73a92\",\"type\":\"ContentSession\"},{\"id\":\"fed7932935f2223870beb120bf58aeed\",\"type\":\"PlaySession\"}],\"rollup\":{\"l1\":\"0126825293972439041\"}},\"object\":{\"id\":\"do_2129946748092334081914\",\"type\":\"Content\",\"ver\":\"2\",\"rollup\":{}},\"tags\":[\"0126825293972439041\"],\"edata\":{\"target\":{\"id\":\"do_21299463432309145612386\",\"ver\":\"1.0\",\"type\":\"AssessmentItem\"},\"type\":\"CHOOSE\",\"values\":[{\"option0\":\"text\"}]}}";
  
  public static final String ASSESS_EVENT = "{" +
      "      \"eid\": \"ASSESS\",\n" +
      "      \"ets\": 1568891738245,\n" +
      "      \"ver\": \"3.1\",\n" +
      "      \"mid\": \"ASSESS:135815023ec32a430632ba5d7f84fe18\",\n" +
      "      \"actor\": {\n" +
      "        \"id\": \"ff1c4bdf-27e2-49bc-a53f-6e304bb3a87f\",\n" +
      "        \"type\": \"User\"\n" +
      "      },\n" +
      "      \"context\": {\n" +
      "        \"channel\": \"0124784842112040965\",\n" +
      "        \"pdata\": {\n" +
      "          \"id\": \"staging.diksha.portal\",\n" +
      "          \"ver\": \"2.4.0\",\n" +
      "          \"pid\": \"sunbird-portal.contentplayer\"\n" +
      "        },\n" +
      "        \"env\": \"contentplayer\",\n" +
      "        \"sid\": \"wqmQpaYc9mRD6jdU6NOWuBTEyGMPXFEe\",\n" +
      "        \"did\": \"a08946e8b72abfeeff6642f245d470cb\",\n" +
      "        \"cdata\": [\n" +
      "          {\n" +
      "            \"id\": \"do_2128415652377067521127\",\n" +
      "            \"type\": \"course\"\n" +
      "          },\n" +
      "          {\n" +
      "            \"type\": \"batch\",\n" +
      "            \"id\": \"012846671379595264119\"\n" +
      "          },\n" +
      "          {\n" +
      "            \"id\": \"f3ec2acf4360e93172b9234e29e38be4\",\n" +
      "            \"type\": \"ContentSession\"\n" +
      "          }\n" +
      "        ],\n" +
      "        \"rollup\": {\n" +
      "          \"l1\": \"0124784842112040965\"\n" +
      "        }\n" +
      "      },\n" +
      "      \"object\": {\n" +
      "        \"id\": \"do_212686723743318016173\",\n" +
      "        \"type\": \"Content\",\n" +
      "        \"ver\": \"1\",\n" +
      "        \"rollup\": {\n" +
      "          \"l1\": \"do_2128415652377067521127\",\n" +
      "          \"l2\": \"do_2128415660716359681128\"\n" +
      "        }\n" +
      "      },\n" +
      "      \"tags\": [\n" +
      "        \"0124784842112040965\"\n" +
      "      ],\n" +
      "      \"edata\": {\n" +
      "        \"item\": {\n" +
      "          \"id\": \"801ae93c-8807-4be5-8853-dd49362d8776\",\n" +
      "          \"maxscore\": 1,\n" +
      "          \"type\": \"mcq\",\n" +
      "          \"exlength\": 0,\n" +
      "          \"params\": [\n" +
      "            {\n" +
      "              \"1\": \"{\\\"text\\\":\\\"World Health Organizaton\\\\n\\\"}\"\n" +
      "            },\n" +
      "            {\n" +
      "              \"2\": \"{\\\"text\\\":\\\"Work Heavy Organization\\\\n\\\"}\"\n" +
      "            },\n" +
      "            {\n" +
      "              \"3\": \"{\\\"text\\\":\\\"Work hell Organization\\\\n\\\"}\"\n" +
      "            },\n" +
      "            {\n" +
      "              \"4\": \"{\\\"text\\\":\\\"None of The above\\\\n\\\"}\"\n" +
      "            },\n" +
      "            {\n" +
      "              \"answer\": \"{\\\"correct\\\":[\\\"1\\\"]}\"\n" +
      "            }\n" +
      "          ],\n" +
      "          \"uri\": \"\",\n" +
      "          \"title\": \"What is the Full form of WHO..?\\n\",\n" +
      "          \"mmc\": [],\n" +
      "          \"mc\": [],\n" +
      "          \"desc\": \"\"\n" +
      "        },\n" +
      "        \"index\": 1,\n" +
      "        \"pass\": \"No\",\n" +
      "        \"score\": 0,\n" +
      "        \"resvalues\": [\n" +
      "          {\n" +
      "            \"2\": \"{\\\"text\\\":\\\"Work Heavy Organization\\\\n\\\"}\"\n" +
      "          }\n" +
      "        ],\n" +
      "        \"duration\": 4\n" +
      "      }\n" +
      "    }";
  
  public static final String ASSESS_EVENT_WITHOUT_QID = "{" +
      "      \"eid\": \"ASSESS\",\n" +
      "      \"ets\": 1568891738245,\n" +
      "      \"ver\": \"3.1\",\n" +
      "      \"mid\": \"ASSESS:135815023ec32a430632ba5d7f84fe18\",\n" +
      "      \"actor\": {\n" +
      "        \"id\": \"ff1c4bdf-27e2-49bc-a53f-6e304bb3a87f\",\n" +
      "        \"type\": \"User\"\n" +
      "      },\n" +
      "      \"context\": {\n" +
      "        \"channel\": \"0124784842112040965\",\n" +
      "        \"pdata\": {\n" +
      "          \"id\": \"staging.diksha.portal\",\n" +
      "          \"ver\": \"2.4.0\",\n" +
      "          \"pid\": \"sunbird-portal.contentplayer\"\n" +
      "        },\n" +
      "        \"env\": \"contentplayer\",\n" +
      "        \"sid\": \"wqmQpaYc9mRD6jdU6NOWuBTEyGMPXFEe\",\n" +
      "        \"did\": \"a08946e8b72abfeeff6642f245d470cb\",\n" +
      "        \"cdata\": [\n" +
      "          {\n" +
      "            \"id\": \"do_2128415652377067521127\",\n" +
      "            \"type\": \"course\"\n" +
      "          },\n" +
      "          {\n" +
      "            \"type\": \"batch\",\n" +
      "            \"id\": \"012846671379595264119\"\n" +
      "          },\n" +
      "          {\n" +
      "            \"id\": \"f3ec2acf4360e93172b9234e29e38be4\",\n" +
      "            \"type\": \"ContentSession\"\n" +
      "          }\n" +
      "        ],\n" +
      "        \"rollup\": {\n" +
      "          \"l1\": \"0124784842112040965\"\n" +
      "        }\n" +
      "      },\n" +
      "      \"object\": {\n" +
      "        \"id\": \"do_212686723743318016173\",\n" +
      "        \"type\": \"Content\",\n" +
      "        \"ver\": \"1\",\n" +
      "        \"rollup\": {\n" +
      "          \"l1\": \"do_2128415652377067521127\",\n" +
      "          \"l2\": \"do_2128415660716359681128\"\n" +
      "        }\n" +
      "      },\n" +
      "      \"tags\": [\n" +
      "        \"0124784842112040965\"\n" +
      "      ],\n" +
      "      \"edata\": {\n" +
      "        \"item\": {\n" +
      "          \"maxscore\": 1,\n" +
      "          \"type\": \"mcq\",\n" +
      "          \"exlength\": 0,\n" +
      "          \"params\": [\n" +
      "            {\n" +
      "              \"1\": \"{\\\"text\\\":\\\"World Health Organizaton\\\\n\\\"}\"\n" +
      "            },\n" +
      "            {\n" +
      "              \"2\": \"{\\\"text\\\":\\\"Work Heavy Organization\\\\n\\\"}\"\n" +
      "            },\n" +
      "            {\n" +
      "              \"3\": \"{\\\"text\\\":\\\"Work hell Organization\\\\n\\\"}\"\n" +
      "            },\n" +
      "            {\n" +
      "              \"4\": \"{\\\"text\\\":\\\"None of The above\\\\n\\\"}\"\n" +
      "            },\n" +
      "            {\n" +
      "              \"answer\": \"{\\\"correct\\\":[\\\"1\\\"]}\"\n" +
      "            }\n" +
      "          ],\n" +
      "          \"uri\": \"\",\n" +
      "          \"title\": \"What is the Full form of WHO..?\\n\",\n" +
      "          \"mmc\": [],\n" +
      "          \"mc\": [],\n" +
      "          \"desc\": \"\"\n" +
      "        },\n" +
      "        \"index\": 1,\n" +
      "        \"pass\": \"No\",\n" +
      "        \"score\": 0,\n" +
      "        \"resvalues\": [\n" +
      "          {\n" +
      "            \"2\": \"{\\\"text\\\":\\\"Work Heavy Organization\\\\n\\\"}\"\n" +
      "          }\n" +
      "        ],\n" +
      "        \"duration\": 4\n" +
      "      }\n" +
      "    }"; 
      

  public static Map<String, Object> getMap(String message) {
    return (Map<String, Object>) new Gson().fromJson(message, Map.class);
  }

}
