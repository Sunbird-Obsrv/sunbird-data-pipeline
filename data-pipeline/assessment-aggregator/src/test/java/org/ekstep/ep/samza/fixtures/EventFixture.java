package org.ekstep.ep.samza.fixtures;

import com.google.gson.Gson;
import org.joda.time.DateTime;

import java.util.Map;

public class EventFixture {

    public static Long current_ets = new DateTime().getMillis();

    public static final String BATCH_ASSESS_EVENT = "{\n" +
            "  \"courseId\": \"do_312712196780204032110117\",\n" +
            "  \"batchId\": \"01271220181664563270\",\n" +
            "  \"contentId\": \"505c7c48ac6dc1edc9b08f21db5a571d\",\n" +
            "  \"userId\": \"b3541347e18ab916c06ed76aeb0ce57f\",\n" +
            "  \"assessmentTs\": 1567073236195,\n" +
            "  \"attemptId\": \"attempt1\",\n" +
            "  \"events\": [\n" +
            "    {\n" +
            "      \"eid\": \"ASSESS\",\n" +
            "      \"ets\": 1567687351000,\n" +
            "      \"ver\": \"3.0\",\n" +
            "      \"mid\": \"ASSESS:48d754770446994b98e64577683ada25\",\n" +
            "      \"actor\": {\n" +
            "        \"id\": \"b3541347e18ab916c06ed76aeb0ce57f\",\n" +
            "        \"type\": \"User\"\n" +
            "      },\n" +
            "      \"context\": {\n" +
            "        \"channel\": \"505c7c48ac6dc1edc9b08f21db5a571d\",\n" +
            "        \"pdata\": {\n" +
            "          \"id\": \"prod.diksha.portal\",\n" +
            "          \"ver\": \"2.2.1\",\n" +
            "          \"pid\": \"sunbird-portal.contentplayer\"\n" +
            "        },\n" +
            "        \"env\": \"contentplayer\",\n" +
            "        \"sid\": \"310f123e-2764-c784-803a-0ca871f2b651\",\n" +
            "        \"did\": \"b3541347e18ab916c06ed76aeb0ce57f\",\n" +
            "        \"cdata\": [\n" +
            "          {\n" +
            "            \"id\": \"6b8617ccf0b4c35f3fe17ccdb71af908\",\n" +
            "            \"type\": \"ContentSession\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"id\": \"01271220181664563270\",\n" +
            "            \"type\": \"batch\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"id\": \"do_312712196780204032110117\",\n" +
            "            \"type\": \"course\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"rollup\": {\n" +
            "          \"l1\": \"505c7c48ac6dc1edc9b08f21db5a571d\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"object\": {\n" +
            "        \"id\": \"do_312592741863645184122936\",\n" +
            "        \"type\": \"Content\",\n" +
            "        \"ver\": \"3\",\n" +
            "        \"rollup\": {\n" +
            "          \"l1\": \"do_3126430145280819201402\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"tags\": [],\n" +
            "      \"edata\": {\n" +
            "        \"item\": {\n" +
            "          \"id\": \"do_312468066279276544217372\",\n" +
            "          \"maxscore\": 10,\n" +
            "          \"exlength\": 0,\n" +
            "          \"params\": [],\n" +
            "          \"uri\": \"\",\n" +
            "          \"title\": \"TNXMATHS-STATISTICS 5\",\n" +
            "          \"mmc\": [],\n" +
            "          \"mc\": [],\n" +
            "          \"desc\": \"For any collection of n  items  ∑(χ - χ̅  )=\"\n" +
            "        },\n" +
            "        \"index\": 1,\n" +
            "        \"pass\": \"Yes\",\n" +
            "        \"score\": 5,\n" +
            "        \"resvalues\": [\n" +
            "          {\n" +
            "            \"∑χ\": \"true\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"duration\": 9\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"eid\": \"ASSESS\",\n" +
            "      \"ets\": 1567073236195,\n" +
            "      \"ver\": \"3.0\",\n" +
            "      \"mid\": \"ASSESS:48d754770446994b98e64577683ada25\",\n" +
            "      \"actor\": {\n" +
            "        \"id\": \"b3541347e18ab916c06ed76aeb0ce57f\",\n" +
            "        \"type\": \"User\"\n" +
            "      },\n" +
            "      \"context\": {\n" +
            "        \"channel\": \"505c7c48ac6dc1edc9b08f21db5a571d\",\n" +
            "        \"pdata\": {\n" +
            "          \"id\": \"prod.diksha.portal\",\n" +
            "          \"ver\": \"2.2.1\",\n" +
            "          \"pid\": \"sunbird-portal.contentplayer\"\n" +
            "        },\n" +
            "        \"env\": \"contentplayer\",\n" +
            "        \"sid\": \"310f123e-2764-c784-803a-0ca871f2b651\",\n" +
            "        \"did\": \"b3541347e18ab916c06ed76aeb0ce57f\",\n" +
            "        \"cdata\": [\n" +
            "          {\n" +
            "            \"id\": \"6b8617ccf0b4c35f3fe17ccdb71af908\",\n" +
            "            \"type\": \"ContentSession\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"id\": \"01271220181664563270\",\n" +
            "            \"type\": \"batch\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"id\": \"do_312712196780204032110117\",\n" +
            "            \"type\": \"course\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"rollup\": {\n" +
            "          \"l1\": \"505c7c48ac6dc1edc9b08f21db5a571d\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"object\": {\n" +
            "        \"id\": \"do_312592741863645184122936\",\n" +
            "        \"type\": \"Content\",\n" +
            "        \"ver\": \"3\",\n" +
            "        \"rollup\": {\n" +
            "          \"l1\": \"do_3126430145280819201402\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"tags\": [],\n" +
            "      \"edata\": {\n" +
            "        \"item\": {\n" +
            "          \"id\": \"do_312468066279276544217372\",\n" +
            "          \"maxscore\": 1,\n" +
            "          \"exlength\": 0,\n" +
            "          \"params\": [],\n" +
            "          \"uri\": \"\",\n" +
            "          \"title\": \"TNXMATHS-STATISTICS 5\",\n" +
            "          \"mmc\": [],\n" +
            "          \"mc\": [],\n" +
            "          \"desc\": \"For any collection of n  items  ∑(χ - χ̅  )=\"\n" +
            "        },\n" +
            "        \"index\": 2,\n" +
            "        \"pass\": \"No\",\n" +
            "        \"score\": 0,\n" +
            "        \"resvalues\": [\n" +
            "          {\n" +
            "            \"∑χ\": \"true\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"duration\": 9\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    public static final String BATCH_ASSESS__OLDER_EVENT = "{\n" +
            "  \"courseId\": \"do_312712196780204032110117\",\n" +
            "  \"batchId\": \"01271220181664563270\",\n" +
            "  \"contentId\": \"505c7c48ac6dc1edc9b08f21db5a571d\",\n" +
            "  \"userId\": \"b3541347e18ab916c06ed76aeb0ce57f\",\n" +
            "  \"assessmentTs\": 1567073236195,\n" +
            "  \"attemptId\": \"attempt1\",\n" +
            "  \"events\": [\n" +
            "    {\n" +
            "      \"eid\": \"ASSESS\",\n" +
            "      \"ets\": 1567073236195,\n" +
            "      \"ver\": \"3.0\",\n" +
            "      \"mid\": \"ASSESS:48d754770446994b98e64577683ada25\",\n" +
            "      \"actor\": {\n" +
            "        \"id\": \"b3541347e18ab916c06ed76aeb0ce57f\",\n" +
            "        \"type\": \"User\"\n" +
            "      },\n" +
            "      \"context\": {\n" +
            "        \"channel\": \"505c7c48ac6dc1edc9b08f21db5a571d\",\n" +
            "        \"pdata\": {\n" +
            "          \"id\": \"prod.diksha.portal\",\n" +
            "          \"ver\": \"2.2.1\",\n" +
            "          \"pid\": \"sunbird-portal.contentplayer\"\n" +
            "        },\n" +
            "        \"env\": \"contentplayer\",\n" +
            "        \"sid\": \"310f123e-2764-c784-803a-0ca871f2b651\",\n" +
            "        \"did\": \"b3541347e18ab916c06ed76aeb0ce57f\",\n" +
            "        \"cdata\": [\n" +
            "          {\n" +
            "            \"id\": \"6b8617ccf0b4c35f3fe17ccdb71af908\",\n" +
            "            \"type\": \"ContentSession\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"id\": \"01271220181664563270\",\n" +
            "            \"type\": \"batch\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"id\": \"do_312712196780204032110117\",\n" +
            "            \"type\": \"course\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"rollup\": {\n" +
            "          \"l1\": \"505c7c48ac6dc1edc9b08f21db5a571d\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"object\": {\n" +
            "        \"id\": \"do_312592741863645184122936\",\n" +
            "        \"type\": \"Content\",\n" +
            "        \"ver\": \"3\",\n" +
            "        \"rollup\": {\n" +
            "          \"l1\": \"do_3126430145280819201402\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"tags\": [],\n" +
            "      \"edata\": {\n" +
            "        \"item\": {\n" +
            "          \"id\": \"do_312468066279276544217372\",\n" +
            "          \"maxscore\": 10,\n" +
            "          \"exlength\": 0,\n" +
            "          \"params\": [],\n" +
            "          \"uri\": \"\",\n" +
            "          \"title\": \"TNXMATHS-STATISTICS 5\",\n" +
            "          \"mmc\": [],\n" +
            "          \"mc\": [],\n" +
            "          \"desc\": \"For any collection of n  items  ∑(χ - χ̅  )=\"\n" +
            "        },\n" +
            "        \"index\": 1,\n" +
            "        \"pass\": \"Yes\",\n" +
            "        \"score\": 5,\n" +
            "        \"resvalues\": [\n" +
            "          {\n" +
            "            \"∑χ\": \"true\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"duration\": 9\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"eid\": \"ASSESS\",\n" +
            "      \"ets\": 1567073236195,\n" +
            "      \"ver\": \"3.0\",\n" +
            "      \"mid\": \"ASSESS:48d754770446994b98e64577683ada25\",\n" +
            "      \"actor\": {\n" +
            "        \"id\": \"b3541347e18ab916c06ed76aeb0ce57f\",\n" +
            "        \"type\": \"User\"\n" +
            "      },\n" +
            "      \"context\": {\n" +
            "        \"channel\": \"505c7c48ac6dc1edc9b08f21db5a571d\",\n" +
            "        \"pdata\": {\n" +
            "          \"id\": \"prod.diksha.portal\",\n" +
            "          \"ver\": \"2.2.1\",\n" +
            "          \"pid\": \"sunbird-portal.contentplayer\"\n" +
            "        },\n" +
            "        \"env\": \"contentplayer\",\n" +
            "        \"sid\": \"310f123e-2764-c784-803a-0ca871f2b651\",\n" +
            "        \"did\": \"b3541347e18ab916c06ed76aeb0ce57f\",\n" +
            "        \"cdata\": [\n" +
            "          {\n" +
            "            \"id\": \"6b8617ccf0b4c35f3fe17ccdb71af908\",\n" +
            "            \"type\": \"ContentSession\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"id\": \"01271220181664563270\",\n" +
            "            \"type\": \"batch\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"id\": \"do_312712196780204032110117\",\n" +
            "            \"type\": \"course\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"rollup\": {\n" +
            "          \"l1\": \"505c7c48ac6dc1edc9b08f21db5a571d\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"object\": {\n" +
            "        \"id\": \"do_312592741863645184122936\",\n" +
            "        \"type\": \"Content\",\n" +
            "        \"ver\": \"3\",\n" +
            "        \"rollup\": {\n" +
            "          \"l1\": \"do_3126430145280819201402\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"tags\": [],\n" +
            "      \"edata\": {\n" +
            "        \"item\": {\n" +
            "          \"id\": \"do_312468066279276544217372\",\n" +
            "          \"maxscore\": 1,\n" +
            "          \"exlength\": 0,\n" +
            "          \"params\": [],\n" +
            "          \"uri\": \"\",\n" +
            "          \"title\": \"TNXMATHS-STATISTICS 5\",\n" +
            "          \"mmc\": [],\n" +
            "          \"mc\": [],\n" +
            "          \"desc\": \"For any collection of n  items  ∑(χ - χ̅  )=\"\n" +
            "        },\n" +
            "        \"index\": 2,\n" +
            "        \"pass\": \"No\",\n" +
            "        \"score\": 0,\n" +
            "        \"resvalues\": [\n" +
            "          {\n" +
            "            \"∑χ\": \"true\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"duration\": 9\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    public static Map<String, Object> getMap(String message) {
        return (Map<String, Object>) new Gson().fromJson(message, Map.class);
    }
}