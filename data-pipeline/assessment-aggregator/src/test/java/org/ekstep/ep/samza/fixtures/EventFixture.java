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

    public static Map<String, Object> getMap(String message) {
        return (Map<String, Object>) new Gson().fromJson(message, Map.class);
    }
}