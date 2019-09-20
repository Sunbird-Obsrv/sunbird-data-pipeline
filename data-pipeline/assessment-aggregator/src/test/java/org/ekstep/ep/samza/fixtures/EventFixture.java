package org.ekstep.ep.samza.fixtures;

import com.google.gson.Gson;

import java.util.Map;

public class EventFixture {

    public static final String BATCH_ASSESS_EVENT = "{\n" +
            "  \"assessmentTs\": 1568891729576,\n" +
            "  \"batchId\": \"012846671379595264119\",\n" +
            "  \"courseId\": \"do_2128415652377067521127\",\n" +
            "  \"userId\": \"ff1c4bdf-27e2-49bc-a53f-6e304bb3a87f\",\n" +
            "  \"attemptId\": \"8cd87e24df268ad09a8b0060c0a40271\",\n" +
            "  \"contentId\": \"do_212686723743318016173\",\n" +
            "  \"events\": [\n" +
            "    {\n" +
            "      \"eid\": \"ASSESS\",\n" +
            "      \"ets\": 1568891735461,\n" +
            "      \"ver\": \"3.1\",\n" +
            "      \"mid\": \"ASSESS:db00a858fec1b8796c62f224874c7edf\",\n" +
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
            "        \"resvalues\": [],\n" +
            "        \"duration\": 2\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
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
            "    },\n" +
            "    {\n" +
            "      \"eid\": \"ASSESS\",\n" +
            "      \"ets\": 1568891747395,\n" +
            "      \"ver\": \"3.1\",\n" +
            "      \"mid\": \"ASSESS:6ba5953669ea86e8f85759d3e7f5998b\",\n" +
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
            "        \"pass\": \"Yes\",\n" +
            "        \"score\": 1,\n" +
            "        \"resvalues\": [\n" +
            "          {\n" +
            "            \"1\": \"{\\\"text\\\":\\\"World Health Organizaton\\\\n\\\"}\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"duration\": 14\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"eid\": \"ASSESS\",\n" +
            "      \"ets\": 1568891772964,\n" +
            "      \"ver\": \"3.1\",\n" +
            "      \"mid\": \"ASSESS:018f01bf99288474860b630b513b9d0c\",\n" +
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
            "          \"id\": \"2bc922e7-985e-486a-ae23-4ba9a1c67edc\",\n" +
            "          \"maxscore\": 1,\n" +
            "          \"type\": \"mtf\",\n" +
            "          \"exlength\": 0,\n" +
            "          \"params\": [\n" +
            "            {\n" +
            "              \"lhs\": \"[{\\\"1\\\":\\\"{\\\\\\\"text\\\\\\\":\\\\\\\"1\\\\\\\"}\\\"},{\\\"2\\\":\\\"{\\\\\\\"text\\\\\\\":\\\\\\\"2\\\\\\\"}\\\"},{\\\"3\\\":\\\"{\\\\\\\"text\\\\\\\":\\\\\\\"3\\\\\\\"}\\\"}]\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"rhs\": \"[{\\\"1\\\":\\\"{\\\\\\\"text\\\\\\\":\\\\\\\"2\\\\\\\"}\\\"},{\\\"2\\\":\\\"{\\\\\\\"text\\\\\\\":\\\\\\\"3\\\\\\\"}\\\"},{\\\"3\\\":\\\"{\\\\\\\"text\\\\\\\":\\\\\\\"1\\\\\\\"}\\\"}]\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"answer\": \"{\\\"lhs\\\":[\\\"1\\\",\\\"2\\\",\\\"3\\\"],\\\"rhs\\\":[\\\"3\\\",\\\"1\\\",\\\"2\\\"]}\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"uri\": \"\",\n" +
            "          \"title\": \"MTF 3\\n\",\n" +
            "          \"mmc\": [],\n" +
            "          \"mc\": [],\n" +
            "          \"desc\": \"\"\n" +
            "        },\n" +
            "        \"index\": 2,\n" +
            "        \"pass\": \"No\",\n" +
            "        \"score\": 0.33,\n" +
            "        \"resvalues\": [\n" +
            "          {\n" +
            "            \"lhs\": \"[{\\\"1\\\":\\\"{\\\\\\\"text\\\\\\\":\\\\\\\"1\\\\\\\"}\\\"},{\\\"2\\\":\\\"{\\\\\\\"text\\\\\\\":\\\\\\\"2\\\\\\\"}\\\"},{\\\"3\\\":\\\"{\\\\\\\"text\\\\\\\":\\\\\\\"3\\\\\\\"}\\\"}]\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"rhs\": \"[{\\\"1\\\":\\\"{\\\\\\\"text\\\\\\\":\\\\\\\"3\\\\\\\"}\\\"},{\\\"2\\\":\\\"{\\\\\\\"text\\\\\\\":\\\\\\\"2\\\\\\\"}\\\"},{\\\"3\\\":\\\"{\\\\\\\"text\\\\\\\":\\\\\\\"1\\\\\\\"}\\\"}]\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"duration\": 24\n" +
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

    public static final String BATCH_ASSESS_FAIL_EVENT = "{\n" +
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
            "      }\n" +
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