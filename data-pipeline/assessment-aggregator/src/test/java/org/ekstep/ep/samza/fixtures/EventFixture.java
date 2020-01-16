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

    public static final String BATCH_DUPLICATE_QUESTION_EVENT ="{\n" +
            "  \"assessmentTs\": 1569304757079,\n" +
            "  \"batchId\": \"01284169026368307244\",\n" +
            "  \"courseId\": \"do_2128410273679114241112\",\n" +
            "  \"userId\": \"d0d8a341-9637-484c-b871-0c27015af238\",\n" +
            "  \"attemptId\": \"90e1a0d12542806389a1a52aaf1fc622\",\n" +
            "  \"contentId\": \"do_2128373396098744321673\",\n" +
            "  \"events\": [\n" +
            "    {\n" +
            "      \"eid\": \"ASSESS\",\n" +
            "      \"ets\": 1569304758743,\n" +
            "      \"ver\": \"3.1\",\n" +
            "      \"mid\": \"ASSESS:5b2e689446886f3cee13de44fec8c02f\",\n" +
            "      \"actor\": {\n" +
            "        \"id\": \"d0d8a341-9637-484c-b871-0c27015af238\",\n" +
            "        \"type\": \"User\"\n" +
            "      },\n" +
            "      \"context\": {\n" +
            "        \"channel\": \"0124511394914140160\",\n" +
            "        \"pdata\": {\n" +
            "          \"id\": \"staging.diksha.portal\",\n" +
            "          \"ver\": \"2.4.0\",\n" +
            "          \"pid\": \"sunbird-portal.contentplayer\"\n" +
            "        },\n" +
            "        \"env\": \"contentplayer\",\n" +
            "        \"sid\": \"2cPynWPvF_X0xSqeCUOzka-kXEDT0vvw\",\n" +
            "        \"did\": \"609b1be929adff933abd1b32caf10b6d\",\n" +
            "        \"cdata\": [\n" +
            "          {\n" +
            "            \"id\": \"do_2128410273679114241112\",\n" +
            "            \"type\": \"course\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"type\": \"batch\",\n" +
            "            \"id\": \"01284169026368307244\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"id\": \"f16a4cacf105ca65e98c61f5a63b8bd3\",\n" +
            "            \"type\": \"ContentSession\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"rollup\": {\n" +
            "          \"l1\": \"0124511394914140160\",\n" +
            "          \"l2\": \"01245115225042944040\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"object\": {\n" +
            "        \"id\": \"do_2128373396098744321673\",\n" +
            "        \"type\": \"Content\",\n" +
            "        \"ver\": \"1\",\n" +
            "        \"rollup\": {\n" +
            "          \"l1\": \"do_2128410273679114241112\",\n" +
            "          \"l2\": \"do_2128410274404106241113\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"tags\": [\n" +
            "        \"0124511394914140160\",\n" +
            "        \"01245115225042944040\"\n" +
            "      ],\n" +
            "      \"edata\": {\n" +
            "        \"item\": {\n" +
            "          \"id\": \"b7886294-95dd-4c83-91e3-7b67e82aaab2\",\n" +
            "          \"maxscore\": 1,\n" +
            "          \"type\": \"mcq\",\n" +
            "          \"exlength\": 0,\n" +
            "          \"params\": [\n" +
            "            {\n" +
            "              \"1\": \"{\\\"text\\\":\\\"normal distribution\\\\n\\\"}\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"2\": \"{\\\"text\\\":\\\"binomial distribution\\\\n\\\"}\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"3\": \"{\\\"text\\\":\\\"Poisson distribution\\\\n\\\"}\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"4\": \"{\\\"text\\\":\\\"uniform distribution\\\\n\\\"}\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"answer\": \"{\\\"correct\\\":[\\\"3\\\"]}\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"uri\": \"\",\n" +
            "          \"title\": \"In the textile industry, a manufacturer is interested in the number of blemishes or flaws occurring in each 100 feet of material. The probability distribution that has the greatest chance of applying to this situation is the\\n\",\n" +
            "          \"mmc\": [],\n" +
            "          \"mc\": [],\n" +
            "          \"desc\": \"\"\n" +
            "        },\n" +
            "        \"index\": 1,\n" +
            "        \"pass\": \"No\",\n" +
            "        \"score\": 0,\n" +
            "        \"resvalues\": [\n" +
            "          {\n" +
            "            \"1\": \"{\\\"text\\\":\\\"normal distribution\\\\n\\\"}\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"duration\": 2\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"eid\": \"ASSESS\",\n" +
            "      \"ets\": 1569304761105,\n" +
            "      \"ver\": \"3.1\",\n" +
            "      \"mid\": \"ASSESS:0cba50019b880fc064a343a6d01d3d1a\",\n" +
            "      \"actor\": {\n" +
            "        \"id\": \"d0d8a341-9637-484c-b871-0c27015af238\",\n" +
            "        \"type\": \"User\"\n" +
            "      },\n" +
            "      \"context\": {\n" +
            "        \"channel\": \"0124511394914140160\",\n" +
            "        \"pdata\": {\n" +
            "          \"id\": \"staging.diksha.portal\",\n" +
            "          \"ver\": \"2.4.0\",\n" +
            "          \"pid\": \"sunbird-portal.contentplayer\"\n" +
            "        },\n" +
            "        \"env\": \"contentplayer\",\n" +
            "        \"sid\": \"2cPynWPvF_X0xSqeCUOzka-kXEDT0vvw\",\n" +
            "        \"did\": \"609b1be929adff933abd1b32caf10b6d\",\n" +
            "        \"cdata\": [\n" +
            "          {\n" +
            "            \"id\": \"do_2128410273679114241112\",\n" +
            "            \"type\": \"course\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"type\": \"batch\",\n" +
            "            \"id\": \"01284169026368307244\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"id\": \"f16a4cacf105ca65e98c61f5a63b8bd3\",\n" +
            "            \"type\": \"ContentSession\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"rollup\": {\n" +
            "          \"l1\": \"0124511394914140160\",\n" +
            "          \"l2\": \"01245115225042944040\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"object\": {\n" +
            "        \"id\": \"do_2128373396098744321673\",\n" +
            "        \"type\": \"Content\",\n" +
            "        \"ver\": \"1\",\n" +
            "        \"rollup\": {\n" +
            "          \"l1\": \"do_2128410273679114241112\",\n" +
            "          \"l2\": \"do_2128410274404106241113\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"tags\": [\n" +
            "        \"0124511394914140160\",\n" +
            "        \"01245115225042944040\"\n" +
            "      ],\n" +
            "      \"edata\": {\n" +
            "        \"item\": {\n" +
            "          \"id\": \"b7886294-95dd-4c83-91e3-7b67e82aaab2\",\n" +
            "          \"maxscore\": 1,\n" +
            "          \"type\": \"mcq\",\n" +
            "          \"exlength\": 0,\n" +
            "          \"params\": [\n" +
            "            {\n" +
            "              \"1\": \"{\\\"text\\\":\\\"normal distribution\\\\n\\\"}\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"2\": \"{\\\"text\\\":\\\"binomial distribution\\\\n\\\"}\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"3\": \"{\\\"text\\\":\\\"Poisson distribution\\\\n\\\"}\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"4\": \"{\\\"text\\\":\\\"uniform distribution\\\\n\\\"}\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"answer\": \"{\\\"correct\\\":[\\\"3\\\"]}\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"uri\": \"\",\n" +
            "          \"title\": \"In the textile industry, a manufacturer is interested in the number of blemishes or flaws occurring in each 100 feet of material. The probability distribution that has the greatest chance of applying to this situation is the\\n\",\n" +
            "          \"mmc\": [],\n" +
            "          \"mc\": [],\n" +
            "          \"desc\": \"\"\n" +
            "        },\n" +
            "        \"index\": 1,\n" +
            "        \"pass\": \"No\",\n" +
            "        \"score\": 0,\n" +
            "        \"resvalues\": [\n" +
            "          {\n" +
            "            \"2\": \"{\\\"text\\\":\\\"binomial distribution\\\\n\\\"}\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"duration\": 4\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"eid\": \"ASSESS\",\n" +
            "      \"ets\": 1569304763323,\n" +
            "      \"ver\": \"3.1\",\n" +
            "      \"mid\": \"ASSESS:ace9f977fdb8254e709180777ff81ba6\",\n" +
            "      \"actor\": {\n" +
            "        \"id\": \"d0d8a341-9637-484c-b871-0c27015af238\",\n" +
            "        \"type\": \"User\"\n" +
            "      },\n" +
            "      \"context\": {\n" +
            "        \"channel\": \"0124511394914140160\",\n" +
            "        \"pdata\": {\n" +
            "          \"id\": \"staging.diksha.portal\",\n" +
            "          \"ver\": \"2.4.0\",\n" +
            "          \"pid\": \"sunbird-portal.contentplayer\"\n" +
            "        },\n" +
            "        \"env\": \"contentplayer\",\n" +
            "        \"sid\": \"2cPynWPvF_X0xSqeCUOzka-kXEDT0vvw\",\n" +
            "        \"did\": \"609b1be929adff933abd1b32caf10b6d\",\n" +
            "        \"cdata\": [\n" +
            "          {\n" +
            "            \"id\": \"do_2128410273679114241112\",\n" +
            "            \"type\": \"course\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"type\": \"batch\",\n" +
            "            \"id\": \"01284169026368307244\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"id\": \"f16a4cacf105ca65e98c61f5a63b8bd3\",\n" +
            "            \"type\": \"ContentSession\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"rollup\": {\n" +
            "          \"l1\": \"0124511394914140160\",\n" +
            "          \"l2\": \"01245115225042944040\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"object\": {\n" +
            "        \"id\": \"do_2128373396098744321673\",\n" +
            "        \"type\": \"Content\",\n" +
            "        \"ver\": \"1\",\n" +
            "        \"rollup\": {\n" +
            "          \"l1\": \"do_2128410273679114241112\",\n" +
            "          \"l2\": \"do_2128410274404106241113\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"tags\": [\n" +
            "        \"0124511394914140160\",\n" +
            "        \"01245115225042944040\"\n" +
            "      ],\n" +
            "      \"edata\": {\n" +
            "        \"item\": {\n" +
            "          \"id\": \"b7886294-95dd-4c83-91e3-7b67e82aaab2\",\n" +
            "          \"maxscore\": 1,\n" +
            "          \"type\": \"mcq\",\n" +
            "          \"exlength\": 0,\n" +
            "          \"params\": [\n" +
            "            {\n" +
            "              \"1\": \"{\\\"text\\\":\\\"normal distribution\\\\n\\\"}\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"2\": \"{\\\"text\\\":\\\"binomial distribution\\\\n\\\"}\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"3\": \"{\\\"text\\\":\\\"Poisson distribution\\\\n\\\"}\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"4\": \"{\\\"text\\\":\\\"uniform distribution\\\\n\\\"}\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"answer\": \"{\\\"correct\\\":[\\\"3\\\"]}\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"uri\": \"\",\n" +
            "          \"title\": \"In the textile industry, a manufacturer is interested in the number of blemishes or flaws occurring in each 100 feet of material. The probability distribution that has the greatest chance of applying to this situation is the\\n\",\n" +
            "          \"mmc\": [],\n" +
            "          \"mc\": [],\n" +
            "          \"desc\": \"\"\n" +
            "        },\n" +
            "        \"index\": 1,\n" +
            "        \"pass\": \"No\",\n" +
            "        \"score\": 0,\n" +
            "        \"resvalues\": [\n" +
            "          {\n" +
            "            \"4\": \"{\\\"text\\\":\\\"uniform distribution\\\\n\\\"}\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"duration\": 6\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"eid\": \"ASSESS\",\n" +
            "      \"ets\": 1569304765515,\n" +
            "      \"ver\": \"3.1\",\n" +
            "      \"mid\": \"ASSESS:b4b19e35aed5b32cf240650aa09ec558\",\n" +
            "      \"actor\": {\n" +
            "        \"id\": \"d0d8a341-9637-484c-b871-0c27015af238\",\n" +
            "        \"type\": \"User\"\n" +
            "      },\n" +
            "      \"context\": {\n" +
            "        \"channel\": \"0124511394914140160\",\n" +
            "        \"pdata\": {\n" +
            "          \"id\": \"staging.diksha.portal\",\n" +
            "          \"ver\": \"2.4.0\",\n" +
            "          \"pid\": \"sunbird-portal.contentplayer\"\n" +
            "        },\n" +
            "        \"env\": \"contentplayer\",\n" +
            "        \"sid\": \"2cPynWPvF_X0xSqeCUOzka-kXEDT0vvw\",\n" +
            "        \"did\": \"609b1be929adff933abd1b32caf10b6d\",\n" +
            "        \"cdata\": [\n" +
            "          {\n" +
            "            \"id\": \"do_2128410273679114241112\",\n" +
            "            \"type\": \"course\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"type\": \"batch\",\n" +
            "            \"id\": \"01284169026368307244\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"id\": \"f16a4cacf105ca65e98c61f5a63b8bd3\",\n" +
            "            \"type\": \"ContentSession\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"rollup\": {\n" +
            "          \"l1\": \"0124511394914140160\",\n" +
            "          \"l2\": \"01245115225042944040\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"object\": {\n" +
            "        \"id\": \"do_2128373396098744321673\",\n" +
            "        \"type\": \"Content\",\n" +
            "        \"ver\": \"1\",\n" +
            "        \"rollup\": {\n" +
            "          \"l1\": \"do_2128410273679114241112\",\n" +
            "          \"l2\": \"do_2128410274404106241113\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"tags\": [\n" +
            "        \"0124511394914140160\",\n" +
            "        \"01245115225042944040\"\n" +
            "      ],\n" +
            "      \"edata\": {\n" +
            "        \"item\": {\n" +
            "          \"id\": \"b7886294-95dd-4c83-91e3-7b67e82aaab2\",\n" +
            "          \"maxscore\": 1,\n" +
            "          \"type\": \"mcq\",\n" +
            "          \"exlength\": 0,\n" +
            "          \"params\": [\n" +
            "            {\n" +
            "              \"1\": \"{\\\"text\\\":\\\"normal distribution\\\\n\\\"}\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"2\": \"{\\\"text\\\":\\\"binomial distribution\\\\n\\\"}\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"3\": \"{\\\"text\\\":\\\"Poisson distribution\\\\n\\\"}\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"4\": \"{\\\"text\\\":\\\"uniform distribution\\\\n\\\"}\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"answer\": \"{\\\"correct\\\":[\\\"3\\\"]}\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"uri\": \"\",\n" +
            "          \"title\": \"In the textile industry, a manufacturer is interested in the number of blemishes or flaws occurring in each 100 feet of material. The probability distribution that has the greatest chance of applying to this situation is the\\n\",\n" +
            "          \"mmc\": [],\n" +
            "          \"mc\": [],\n" +
            "          \"desc\": \"\"\n" +
            "        },\n" +
            "        \"index\": 1,\n" +
            "        \"pass\": \"Yes\",\n" +
            "        \"score\": 1,\n" +
            "        \"resvalues\": [\n" +
            "          {\n" +
            "            \"3\": \"{\\\"text\\\":\\\"Poisson distribution\\\\n\\\"}\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"duration\": 8\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"eid\": \"ASSESS\",\n" +
            "      \"ets\": 1569304767576,\n" +
            "      \"ver\": \"3.1\",\n" +
            "      \"mid\": \"ASSESS:366e140cf5fddf850d1e548644a35729\",\n" +
            "      \"actor\": {\n" +
            "        \"id\": \"d0d8a341-9637-484c-b871-0c27015af238\",\n" +
            "        \"type\": \"User\"\n" +
            "      },\n" +
            "      \"context\": {\n" +
            "        \"channel\": \"0124511394914140160\",\n" +
            "        \"pdata\": {\n" +
            "          \"id\": \"staging.diksha.portal\",\n" +
            "          \"ver\": \"2.4.0\",\n" +
            "          \"pid\": \"sunbird-portal.contentplayer\"\n" +
            "        },\n" +
            "        \"env\": \"contentplayer\",\n" +
            "        \"sid\": \"2cPynWPvF_X0xSqeCUOzka-kXEDT0vvw\",\n" +
            "        \"did\": \"609b1be929adff933abd1b32caf10b6d\",\n" +
            "        \"cdata\": [\n" +
            "          {\n" +
            "            \"id\": \"do_2128410273679114241112\",\n" +
            "            \"type\": \"course\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"type\": \"batch\",\n" +
            "            \"id\": \"01284169026368307244\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"id\": \"f16a4cacf105ca65e98c61f5a63b8bd3\",\n" +
            "            \"type\": \"ContentSession\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"rollup\": {\n" +
            "          \"l1\": \"0124511394914140160\",\n" +
            "          \"l2\": \"01245115225042944040\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"object\": {\n" +
            "        \"id\": \"do_2128373396098744321673\",\n" +
            "        \"type\": \"Content\",\n" +
            "        \"ver\": \"1\",\n" +
            "        \"rollup\": {\n" +
            "          \"l1\": \"do_2128410273679114241112\",\n" +
            "          \"l2\": \"do_2128410274404106241113\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"tags\": [\n" +
            "        \"0124511394914140160\",\n" +
            "        \"01245115225042944040\"\n" +
            "      ],\n" +
            "      \"edata\": {\n" +
            "        \"item\": {\n" +
            "          \"id\": \"759b8138-4c47-4724-8cd6-cae6a7d1ad18\",\n" +
            "          \"maxscore\": 1,\n" +
            "          \"type\": \"mcq\",\n" +
            "          \"exlength\": 0,\n" +
            "          \"params\": [\n" +
            "            {\n" +
            "              \"1\": \"{\\\"text\\\":\\\"descriptive statistic\\\\n\\\"}\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"2\": \"{\\\"text\\\":\\\"probability function\\\\n\\\"}\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"3\": \"{\\\"text\\\":\\\"variance\\\\n\\\"}\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"4\": \"{\\\"text\\\":\\\"random variable\\\\n\\\"}\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"answer\": \"{\\\"correct\\\":[\\\"4\\\"]}\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"uri\": \"\",\n" +
            "          \"title\": \"A numerical description of the outcome of an experiment is called a\\n\",\n" +
            "          \"mmc\": [],\n" +
            "          \"mc\": [],\n" +
            "          \"desc\": \"\"\n" +
            "        },\n" +
            "        \"index\": 2,\n" +
            "        \"pass\": \"No\",\n" +
            "        \"score\": 0,\n" +
            "        \"resvalues\": [\n" +
            "          {\n" +
            "            \"3\": \"{\\\"text\\\":\\\"variance\\\\n\\\"}\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"duration\": 1\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"eid\": \"ASSESS\",\n" +
            "      \"ets\": 1569304769433,\n" +
            "      \"ver\": \"3.1\",\n" +
            "      \"mid\": \"ASSESS:834b83c5205b657a208e552d5964a872\",\n" +
            "      \"actor\": {\n" +
            "        \"id\": \"d0d8a341-9637-484c-b871-0c27015af238\",\n" +
            "        \"type\": \"User\"\n" +
            "      },\n" +
            "      \"context\": {\n" +
            "        \"channel\": \"0124511394914140160\",\n" +
            "        \"pdata\": {\n" +
            "          \"id\": \"staging.diksha.portal\",\n" +
            "          \"ver\": \"2.4.0\",\n" +
            "          \"pid\": \"sunbird-portal.contentplayer\"\n" +
            "        },\n" +
            "        \"env\": \"contentplayer\",\n" +
            "        \"sid\": \"2cPynWPvF_X0xSqeCUOzka-kXEDT0vvw\",\n" +
            "        \"did\": \"609b1be929adff933abd1b32caf10b6d\",\n" +
            "        \"cdata\": [\n" +
            "          {\n" +
            "            \"id\": \"do_2128410273679114241112\",\n" +
            "            \"type\": \"course\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"type\": \"batch\",\n" +
            "            \"id\": \"01284169026368307244\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"id\": \"f16a4cacf105ca65e98c61f5a63b8bd3\",\n" +
            "            \"type\": \"ContentSession\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"rollup\": {\n" +
            "          \"l1\": \"0124511394914140160\",\n" +
            "          \"l2\": \"01245115225042944040\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"object\": {\n" +
            "        \"id\": \"do_2128373396098744321673\",\n" +
            "        \"type\": \"Content\",\n" +
            "        \"ver\": \"1\",\n" +
            "        \"rollup\": {\n" +
            "          \"l1\": \"do_2128410273679114241112\",\n" +
            "          \"l2\": \"do_2128410274404106241113\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"tags\": [\n" +
            "        \"0124511394914140160\",\n" +
            "        \"01245115225042944040\"\n" +
            "      ],\n" +
            "      \"edata\": {\n" +
            "        \"item\": {\n" +
            "          \"id\": \"759b8138-4c47-4724-8cd6-cae6a7d1ad18\",\n" +
            "          \"maxscore\": 1,\n" +
            "          \"type\": \"mcq\",\n" +
            "          \"exlength\": 0,\n" +
            "          \"params\": [\n" +
            "            {\n" +
            "              \"1\": \"{\\\"text\\\":\\\"descriptive statistic\\\\n\\\"}\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"2\": \"{\\\"text\\\":\\\"probability function\\\\n\\\"}\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"3\": \"{\\\"text\\\":\\\"variance\\\\n\\\"}\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"4\": \"{\\\"text\\\":\\\"random variable\\\\n\\\"}\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"answer\": \"{\\\"correct\\\":[\\\"4\\\"]}\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"uri\": \"\",\n" +
            "          \"title\": \"A numerical description of the outcome of an experiment is called a\\n\",\n" +
            "          \"mmc\": [],\n" +
            "          \"mc\": [],\n" +
            "          \"desc\": \"\"\n" +
            "        },\n" +
            "        \"index\": 2,\n" +
            "        \"pass\": \"Yes\",\n" +
            "        \"score\": 1,\n" +
            "        \"resvalues\": [\n" +
            "          {\n" +
            "            \"4\": \"{\\\"text\\\":\\\"random variable\\\\n\\\"}\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"duration\": 3\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";
    public static final String QUESTION_EVENT_RES_VALUES = "{\n" +
            "  \"assessmentTs\": 1578663044164,\n" +
            "  \"userId\": \"50a9e3fc-d047-4fa5-a37b-67501b8933db\",\n" +
            "  \"contentId\": \"do_3129323935897108481169\",\n" +
            "  \"courseId\": \"do_3129323995959541761169\",\n" +
            "  \"batchId\": \"0129324118211215362\",\n" +
            "  \"attemptId\": \"702ae8b81e37c94448d1fa117678d68c\",\n" +
            "  \"events\": [\n" +
            "    {\n" +
            "      \"eid\": \"ASSESS\",\n" +
            "      \"ets\": 1578663044164,\n" +
            "      \"ver\": \"3.0\",\n" +
            "      \"mid\": \"ASSESS:0b5c1d06920337df9d662b69d53f48bf\",\n" +
            "      \"actor\": {\n" +
            "        \"id\": \"50a9e3fc-d047-4fa5-a37b-67501b8933db\",\n" +
            "        \"type\": \"User\"\n" +
            "      },\n" +
            "      \"context\": {\n" +
            "        \"channel\": \"0126684405014528002\",\n" +
            "        \"pdata\": {\n" +
            "          \"id\": \"prod.diksha.app\",\n" +
            "          \"ver\": \"2.6.203\",\n" +
            "          \"pid\": \"sunbird.app.contentplayer\"\n" +
            "        },\n" +
            "        \"env\": \"contentplayer\",\n" +
            "        \"sid\": \"11059dcc-0a69-4d92-90b3-c1eb97f5f93b\",\n" +
            "        \"did\": \"b9bdcb8cd7abc5bd7813bd65ec0b5084dc0dadd8\",\n" +
            "        \"cdata\": [\n" +
            "          {\n" +
            "            \"id\": \"83f8c374dc0e4d91ec4c48d6e5d10b4c\",\n" +
            "            \"type\": \"AttemptId\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"id\": \"streaming\",\n" +
            "            \"type\": \"PlayerLaunch\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"id\": \"6a7809b4eccf75ae62b18c224a4cfeb5\",\n" +
            "            \"type\": \"ContentSession\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"id\": \"48e8e20e8c6fbc660f4ca317497fa7a6\",\n" +
            "            \"type\": \"PlaySession\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"rollup\": {\n" +
            "          \"l1\": \"0126684405014528002\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"object\": {\n" +
            "        \"id\": \"do_3129323935897108481169\",\n" +
            "        \"type\": \"Content\",\n" +
            "        \"ver\": \"1\",\n" +
            "        \"rollup\": {\n" +
            "          \"l1\": \"do_3129323995959541761169\",\n" +
            "          \"l2\": \"do_31293239986759270411495\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"tags\": [],\n" +
            "      \"edata\": {\n" +
            "        \"item\": {\n" +
            "          \"id\": \"QMCQ02115\",\n" +
            "          \"maxscore\": 1,\n" +
            "          \"exlength\": 0,\n" +
            "          \"params\": [\n" +
            "            {\n" +
            "              \"item\": {\n" +
            "                \"keywords\": [\n" +
            "                  \"mdd\"\n" +
            "                ],\n" +
            "                \"subject\": \"Mathematics\",\n" +
            "                \"channel\": \"in.ekstep\",\n" +
            "                \"language\": [\n" +
            "                  \"English\"\n" +
            "                ],\n" +
            "                \"type\": \"ftb\",\n" +
            "                \"editorState\": null,\n" +
            "                \"body\": null,\n" +
            "                \"question_audio\": \"\",\n" +
            "                \"gradeLevel\": [\n" +
            "                  \"Class 4\"\n" +
            "                ],\n" +
            "                \"appId\": \"prod.diksha.app\",\n" +
            "                \"used_for\": \"worksheet\",\n" +
            "                \"model\": {\n" +
            "                  \"hintMsg\": \"Observe the key value and find the number of aircrafts which flies in the morning.\",\n" +
            "                  \"numericLangId\": \"en\",\n" +
            "                  \"langId\": \"en\",\n" +
            "                  \"variables\": {\n" +
            "                    \"$aircraft1\": \"random(100,100)\",\n" +
            "                    \"$aircraft2\": \"random(70,70)\",\n" +
            "                    \"$aircraft3\": \"random(80,80)\",\n" +
            "                    \"$aircraft4\": \"random(30,30)\"\n" +
            "                  },\n" +
            "                  \"dropDowns\": [\n" +
            "                    {\n" +
            "                      \"identifier\": 1,\n" +
            "                      \"options\": [\n" +
            "                        {\n" +
            "                          \"text\": \"OPT_1_0\",\n" +
            "                          \"mh\": null,\n" +
            "                          \"mmc\": [\n" +
            "                            \"DH277\",\n" +
            "                            \"DH278\"\n" +
            "                          ],\n" +
            "                          \"answer\": true,\n" +
            "                          \"image\": null\n" +
            "                        },\n" +
            "                        {\n" +
            "                          \"text\": \"OPT_1_1\",\n" +
            "                          \"mh\": \"MH_1_1\",\n" +
            "                          \"mmc\": [\n" +
            "                            \"DH277\",\n" +
            "                            \"DH278\"\n" +
            "                          ],\n" +
            "                          \"answer\": false,\n" +
            "                          \"image\": null\n" +
            "                        },\n" +
            "                        {\n" +
            "                          \"text\": \"OPT_1_2\",\n" +
            "                          \"mh\": \"MH_1_2\",\n" +
            "                          \"mmc\": [\n" +
            "                            \"DH277\",\n" +
            "                            \"DH278\"\n" +
            "                          ],\n" +
            "                          \"answer\": false,\n" +
            "                          \"image\": null\n" +
            "                        },\n" +
            "                        {\n" +
            "                          \"text\": \"OPT_1_3\",\n" +
            "                          \"mh\": \"MH_1_3\",\n" +
            "                          \"mmc\": [\n" +
            "                            \"DH277\",\n" +
            "                            \"DH278\"\n" +
            "                          ],\n" +
            "                          \"answer\": false,\n" +
            "                          \"image\": null\n" +
            "                        }\n" +
            "                      ]\n" +
            "                    },\n" +
            "                    {\n" +
            "                      \"identifier\": 2,\n" +
            "                      \"options\": []\n" +
            "                    },\n" +
            "                    {\n" +
            "                      \"identifier\": 3,\n" +
            "                      \"options\": []\n" +
            "                    },\n" +
            "                    {\n" +
            "                      \"identifier\": 4,\n" +
            "                      \"options\": []\n" +
            "                    }\n" +
            "                  ],\n" +
            "                  \"variablesProcessed\": true\n" +
            "                },\n" +
            "                \"state\": \"Verified\",\n" +
            "                \"identifier\": \"QMCQ02115\",\n" +
            "                \"level\": 2,\n" +
            "                \"author\": \"funtoot\",\n" +
            "                \"consumerId\": \"1762141d-8681-458b-9bd1-c6af98c0d989\",\n" +
            "                \"solutions\": null,\n" +
            "                \"portalOwner\": \"562\",\n" +
            "                \"version\": 1,\n" +
            "                \"i18n\": {\n" +
            "                  \"en\": {\n" +
            "                    \"HINT\": \"Hint\",\n" +
            "                    \"MICROHINT\": \"Micro Hint\",\n" +
            "                    \"SOLUTION\": \"Solution\",\n" +
            "                    \"HELP\": \"Help\",\n" +
            "                    \"REDUCE_FRACTION\": \"Check the solution again. Remove the common factors in the fraction and reduce it to its lowest term.\",\n" +
            "                    \"MIXED_FRACTION\": \"Convert the mixed fraction to an improper fraction.\",\n" +
            "                    \"IMPROPER_FRACTION\": \"Convert the improper fraction to an mixed fraction.\",\n" +
            "                    \"LIKEFRACTIONADDITION_NUMERATOR\": \"Add the numerator of the given fractions correctly.\",\n" +
            "                    \"LIKEFRACTIONADDITION_DENOMINATOR\": \"For addition of like fractions the denominator remains the same.\",\n" +
            "                    \"LIKEFRACTIONADDITION_FULL\": \"Check the addition of the numerator. The denominator remains the same.\",\n" +
            "                    \"UNLIKEFRACTIONADDITION_NUMERATOR\": \"First convert the unlike fractions to like fractions. Then add the numerator of the like fractions.\",\n" +
            "                    \"UNLIKEFRACTIONADDITION_DENOMINATOR\": \"First find the least common factor and convert the unlike fractions to like fractions. The denominator of the solution is the denominator of the like fractions.\",\n" +
            "                    \"UNLIKEFRACTIONADDITION_FULL\": \"Convert the given fractions to like fractions and then carefully do the addition.\",\n" +
            "                    \"LIKEFRACTIONSUBTRACTION_NUMERATOR\": \"Subtract the numerator of the given fractions correctly.\",\n" +
            "                    \"LIKEFRACTIONSUBTRACTION_DENOMINATOR\": \"For subtraction of like fractions the denominator remains the same as that of the question.\",\n" +
            "                    \"LIKEFRACTIONSUBTRACTION_FULL\": \"Subtract the numerator of the fractions. Denominator remains the same.\",\n" +
            "                    \"UNLIKEFRACTIONSUBTRACTION_NUMERATOR\": \"First convert the unlike fractions to like fractions. Then subtract the numerator of the like fractions.\",\n" +
            "                    \"UNLIKEFRACTIONSUBTRACTION_DENOMINATOR\": \"First find the least common factor and convert the unlike fractions to like fractions. The denominator of the solution is the denominator of the like fractions.\",\n" +
            "                    \"UNLIKEFRACTIONSUBTRACTION_FULL\": \"Convert the given fractions to like fractions and then carefully do the subtraction.\",\n" +
            "                    \"LIKEMIXEDFRACTIONADDITION_WHOLE\": \"Add the whole number part of both the fractions carefully. Check for the conversion of the improper fraction to proper fractions in the solution.\",\n" +
            "                    \"LIKEMIXEDFRACTIONADDITION_NUMERATOR\": \"Add the numerator of the like fractions. If the fractional part is an improper fraction, then convert it to a proper fraction.\",\n" +
            "                    \"LIKEMIXEDFRACTIONADDITION_DENOMINATOR\": \"The denominator of the solution fraction does not change for like fractions.\",\n" +
            "                    \"LIKEMIXEDFRACTIONADDITION_FULL\": \"Convert the mixed fractions to improper fractions and then add them. The final solution should again be as a mixed fraction.\",\n" +
            "                    \"LIKEMIXEDFRACTIONSUBTRACTION_WHOLE\": \"Subtract the whole number part of both the fractions carefully.\",\n" +
            "                    \"LIKEMIXEDFRACTIONSUBTRACTION_NUMERATOR\": \"Subtract the numerator of the like fractions.\",\n" +
            "                    \"LIKEMIXEDFRACTIONSUBTRACTION_DENOMINATOR\": \"The denominator of the solution fraction does not change for like fractions.\",\n" +
            "                    \"LIKEMIXEDFRACTIONSUBTRACTION_FULL\": \"Convert the mixed fractions to improper fractions and then subtract them. The final solution should again be as a mixed fraction.\",\n" +
            "                    \"WHOLEANDPROPERFRACTIONADDITION_WHOLE\": \"The integer added becomes the whole number part of the mixed fraction.\",\n" +
            "                    \"WHOLEANDPROPERFRACTIONADDITION_NUMERATOR\": \"The numerator of the solution fraction remains the same. \",\n" +
            "                    \"WHOLEANDPROPERFRACTIONADDITION_DENOMINATOR\": \"The denominator of the solution fraction remains the same as given in the problem.\",\n" +
            "                    \"WHOLEANDPROPERFRACTIONADDITION_FULL\": \"The integer and the proper fraction when added becomes the mixed fraction.\",\n" +
            "                    \"WHOLEANDPROPERFRACTIONSUBTRACTION_WHOLE\": \"Convert the whole number to an equivalent fraction. Then find the difference between the like fractions.\",\n" +
            "                    \"WHOLEANDPROPERFRACTIONSUBTRACTION_NUMERATOR\": \"First find the least common factor and convert them to difference between like fractions. Then subtract the numerators carefully.\",\n" +
            "                    \"WHOLEANDPROPERFRACTIONSUBTRACTION_DENOMINATOR\": \"The denominator of the solution fraction remains the same as given in the problem. \",\n" +
            "                    \"WHOLEANDPROPERFRACTIONSUBTRACTION_FULL\": \"When the proper fraction is subtracted from the integer becomes the mixed fraction.\",\n" +
            "                    \"WHOLEANDFRACTIONMULTIPLICATION_NUMERATOR\": \"Multiply the numerator and the whole number carefully.\",\n" +
            "                    \"WHOLEANDFRACTIONMULTIPLICATION_DENOMINATOR\": \"Multiply the denominators of the fractions carefully. \",\n" +
            "                    \"WHOLEANDFRACTIONMULTIPLICATION_FULL\": \"Multiply the integer with the fraction.\",\n" +
            "                    \"FRACTIONMULTIPLICATION_NUMERATOR\": \"Multiply the numerators of the fractions carefully.\",\n" +
            "                    \"FRACTIONMULTIPLICATION_DENOMINATOR\": \"Multiply the denominators of the fractions carefully. \",\n" +
            "                    \"FRACTIONMULTIPLICATION_FULL\": \"Multiply the numerators and denominators of the fractions.\",\n" +
            "                    \"FRACTIONDIVISION\": \"First find the reciprocal of the divisor. Then multiply the fraction with the reciprocal of the divisor to get the solution.\",\n" +
            "                    \"SELECT\": \"Select\",\n" +
            "                    \"EXPRESSIONS\": \"$aircraft1=random(100,100)\\n$aircraft2=random(70,70)\\n$aircraft3=random(80,80)\\n$aircraft4=random(30,30)\",\n" +
            "                    \"NO_HINT\": \"There is no hint for this question.\",\n" +
            "                    \"NO_ANSWER\": \"Please answer.\",\n" +
            "                    \"SOLUTION_ID\": \"a. The tallest bar shows the subject which is most liked by the students i.e., Social Studies. 40 students like Social Studiesb. There are 5 subjects mentioned in the graph, namely English, Mathematics, Hindi, Social Studies, and General Science.c. The shortest bar represents the subject which is least liked by the students i.e., Mathematics. Only 10 students like Mathematics.d. Number of students who like Hindi = 20Number of students who like Mathematics = 10We can clearly see that the number of students who like Hindi is double  that of Mathematics. Hence the statement is true.\",\n" +
            "                    \"HINT_ID\": \"Observe the key value and find the number of aircrafts which flies in the morning.\",\n" +
            "                    \"QUESTION_TEXT\": \"The pictograph represents the number of aircrafts flying from India to foreign countries at different timings. Airlines decided to fly $aircraft1 aircrafts in the morning. How many aircrafts should they fly more? __1__\",\n" +
            "                    \"MH_1_3\": \"Find how many number of aircrafts to be added in the morning to make the count as $aircraft1.\",\n" +
            "                    \"OPT_1_3\": \"$aircraft1\",\n" +
            "                    \"MH_1_2\": \"According to the pictograph the number of aircrafts which flies in the afternoon is $aircraft3. Find how many number of aircrafts to be added in the morning to make the count as $aircraft1.\",\n" +
            "                    \"OPT_1_2\": \"$aircraft3\",\n" +
            "                    \"MH_1_1\": \"According to the pictograph the number of aircrafts which flies in the morning is $aircraft2. Find how many number of aircrafts to be added in the morning to make the count as $aircraft1.\",\n" +
            "                    \"OPT_1_1\": \"$aircraft2\",\n" +
            "                    \"OPT_1_0\": \"$aircraft4\"\n" +
            "                  }\n" +
            "                },\n" +
            "                \"tags\": [\n" +
            "                  \"mdd\"\n" +
            "                ],\n" +
            "                \"concepts\": [\n" +
            "                  \"C421\"\n" +
            "                ],\n" +
            "                \"grade\": [\n" +
            "                  \"4\"\n" +
            "                ],\n" +
            "                \"domain\": \"Numeracy\",\n" +
            "                \"name\": \"QMCQ02115\",\n" +
            "                \"bloomsTaxonomyLevel\": \"Understand\",\n" +
            "                \"status\": \"Live\",\n" +
            "                \"itemType\": \"UNIT\",\n" +
            "                \"code\": \"QMCQ02115\",\n" +
            "                \"qtype\": \"mdd\",\n" +
            "                \"qlevel\": \"MEDIUM\",\n" +
            "                \"flags\": \"{\\\"isZoomable\\\":true}\",\n" +
            "                \"questionImage\": \"org.ekstep.funtoot.QMCQ02115.image.9074280266192281\",\n" +
            "                \"media\": [\n" +
            "                  {\n" +
            "                    \"id\": \"org.ekstep.funtoot.QMCQ02115.image.9074280266192281\",\n" +
            "                    \"src\": \"https://ekstep-public-prod.s3-ap-south-1.amazonaws.com/content/org.ekstep.funtoot.qmcq02115.image.9886103695997586/artifact/org.ekstep.funtoot.qmcq02115.image.9886103695997586_1514092203316.png\",\n" +
            "                    \"type\": \"image\"\n" +
            "                  }\n" +
            "                ],\n" +
            "                \"title\": \"\",\n" +
            "                \"qid\": \"QMCQ02115\",\n" +
            "                \"createdOn\": \"2017-12-24T05:10:03.956+0000\",\n" +
            "                \"qindex\": 1,\n" +
            "                \"lastUpdatedOn\": \"2018-08-16T14:08:14.323+0000\",\n" +
            "                \"subLevel\": \"\",\n" +
            "                \"question\": \"QUESTION_TEXT\",\n" +
            "                \"versionKey\": \"1534428494323\",\n" +
            "                \"framework\": \"NCF\",\n" +
            "                \"answer\": {},\n" +
            "                \"max_score\": 5,\n" +
            "                \"sublevel\": 3,\n" +
            "                \"template_id\": \"org.ekstep.funtoot.template.01\",\n" +
            "                \"category\": \"MCQ\",\n" +
            "                \"isSelected\": true,\n" +
            "                \"$$hashKey\": \"object:1540\",\n" +
            "                \"template\": \"funtoot.template.01\",\n" +
            "                \"maxAttempts\": 2,\n" +
            "                \"curAttempt\": 0\n" +
            "              },\n" +
            "              \"config\": {\n" +
            "                \"count\": 1,\n" +
            "                \"selectedConfig\": {},\n" +
            "                \"title\": \"\",\n" +
            "                \"type\": \"items\",\n" +
            "                \"var\": \"item\"\n" +
            "              }\n" +
            "            }\n" +
            "          ],\n" +
            "          \"uri\": \"\",\n" +
            "          \"mmc\": [],\n" +
            "          \"mc\": [\n" +
            "            null\n" +
            "          ],\n" +
            "          \"desc\": \"\"\n" +
            "        },\n" +
            "        \"index\": 1,\n" +
            "        \"pass\": \"Yes\",\n" +
            "        \"score\": 1,\n" +
            "        \"resvalues\": [\n" +
            "          {\n" +
            "            \"1\": \"random(30,30)\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"duration\": 6\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    public static Map<String, Object> getMap(String message) {
        return (Map<String, Object>) new Gson().fromJson(message, Map.class);
    }
}