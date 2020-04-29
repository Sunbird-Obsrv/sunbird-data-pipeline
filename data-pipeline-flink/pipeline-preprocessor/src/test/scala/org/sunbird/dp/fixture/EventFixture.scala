package org.sunbird.dp.fixture

object EventFixtures {

  val EVENT_WITH_MID: String =
    """{"actor":{"type":"User","id":"f:bc3be7ae-ad2b-4dee-ac4c-220c7db146b2"},"eid":"INTERACT",
      |"edata":{"type":"OTHER","subtype":"sheen-animation-ended","id":"library","pageid":"library","extra":{"pos":[]}},
      |"ver":"3.0","syncts":1.579564974098E12,"@timestamp":"2020-01-21T00:02:54.098Z","ets":1.579143065071E12,
      |"context":{"cdata":[],"env":"home","channel":"505c7c48ac6dc1edc9b08f21db5a571d",
      |"pdata":{"id":"prod.diksha.portal","pid":"sunbird.app","ver":"2.3.144"},"sid":"df936f82-e982-41ec-8412-70d414458272",
      |"did":"758e054a400f20f7677f2def76427dc13ad1f837"},
      |"mid":"321a6f0c-10c6-4cdc-9893-207bb64fea50","type":"events","object":{"id":"","type":"",
      |"version":"","rollup":{}}}""".stripMargin

  val SHARE_EVENT: String =
    """
      |{"ver":"3.0","eid":"SHARE","ets":1577278681178,"actor":{"type":"User","id":"7c3ea1bb-4da1-48d0-9cc0-c4f150554149"},"context":{"channel":"505c7c48ac6dc1edc9b08f21db5a571d","pdata":{"id":"prod.sunbird.desktop","pid":"sunbird.app","ver":"2.3.162"},"env":"app","sid":"82e41d87-e33f-4269-aeae-d56394985599","did":"1b17c32bad61eb9e33df281eecc727590d739b2b"},"edata":{"dir":"In","type":"File","items":[{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_312785709424099328114191","type":"CONTENT","ver":"1","params":[{"transfers":0,"size":21084308}]},{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_31277435209002188818711","type":"CONTENT","ver":"18","params":[{"transfers":12,"size":"123"}]},{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_31278794857559654411554","type":"TextBook","ver":"1"}]},"object":{"id":"do_312528116260749312248818","type":"TextBook","version":"10","rollup":{}},"mid":"02ba33e5-15fe-4ec5-b32.1084308E760-3d03429fae84","syncts":1577278682630,"@timestamp":"2019-12-25T12:58:02.630Z","type":"events"}
      |""".stripMargin

  val DIFFERENT_VERSION_EVENT: String =
    """{"actor":{"type":"User","id":"f:bc3be7ae-ad2b-4dee-ac4c-220c7db146b2"},"eid":"INTERACT",
      |"edata":{"type":"OTHER","subtype":"sheen-animation-ended","id":"library","pageid":"library","extra":{"pos":[]}},
      |"ver":"3.1","syncts":1.579564974098E12,"@timestamp":"2020-01-21T00:02:54.098Z","ets":1.579143065071E12,
      |"context":{"cdata":[],"env":"home","channel":"505c7c48ac6dc1edc9b08f21db5a571d",
      |"pdata":{"id":"prod.diksha.portal","pid":"sunbird.app","ver":"2.3.144"},"sid":"df936f82-e982-41ec-8412-70d414458272",
      |"did":"758e054a400f20f7677f2def76427dc13ad1f837"},
      |"mid":"321a6f0c-10c6-4cdc-95345345893-207bb64fea50","type":"events","object":{"id":"","type":"",
      |"version":"","rollup":{}}}""".stripMargin

  val DUPLICATE_SHARE_EVENT: String =
    """
      |{"ver":"3.0","eid":"SHARE","ets":1577278681178,"actor":{"type":"User","id":"7c3ea1bb-4da1-48d0-9cc0-c4f150554149"},"context":{"channel":"505c7c48ac6dc1edc9b08f21db5a571d","pdata":{"id":"prod.sunbird.desktop","pid":"sunbird.app","ver":"2.3.162"},"env":"app","sid":"82e41d87-e33f-4269-aeae-d56394985599","did":"1b17c32bad61eb9e33df281eecc727590d739b2b"},"edata":{"dir":"In","type":"File","items":[{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_312785709424099328114191","type":"CONTENT","ver":"1","params":[{"transfers":0,"size":21084308}]},{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_31277435209002188818711","type":"CONTENT","ver":"18","params":[{"transfers":12,"size":"123"}]},{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_31278794857559654411554","type":"TextBook","ver":"1"}]},"object":{"id":"do_312528116260749312248818","type":"TextBook","version":"10","rollup":{}},"mid":"02ba33e5-15fe-4ec5-b32.1084308E760-3d03429fae84","syncts":1577278682630,"@timestamp":"2019-12-25T12:58:02.630Z","type":"events"}
      |""".stripMargin

  val INVALID_EVENT: String =
    """
      |
      |{"ver":"3.0","eid":"SHARE","ets":1577278681178,"actor":{"type":"User","id":"7c3ea1bb-4da1-48d0-9cc0-c4f150554149"},"edata":{"dir":"In","type":"File","items":[{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_312785709424099328114191","type":"CONTENT","ver":"1","params":[{"transfers":0,"size":21084308}]},{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_31277435209002188818711","type":"CONTENT","ver":"18","params":[{"transfers":12,"size":"123"}]},{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_31278794857559654411554","type":"TextBook","ver":"1"}]},"object":{"id":"do_312528116260749312248818","type":"TextBook","version":"10","rollup":{}},"mid":"02ba33efff5-15fe-4ec5-b32.1084308E760-3d03ff429fae84","syncts":1577278682630,"@timestamp":"2019-12-25T12:58:02.630Z","type":"events"}
      |""".stripMargin

  val INVALID_EVENT_SCHEMA_DOESNT_EXISTS: String =
    """
      |
      |{"ver":"3.0","eid":"SHARE_ITEM","ets":1577278681178,"actor":{"type":"User","id":"7c3ea1bb-4da1-48d0-9cc0-c4f150554149"},"edata":{"dir":"In","type":"File","items":[{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_312785709424099328114191","type":"CONTENT","ver":"1","params":[{"transfers":0,"size":21084308}]},{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_31277435209002188818711","type":"CONTENT","ver":"18","params":[{"transfers":12,"size":"123"}]},{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_31278794857559654411554","type":"TextBook","ver":"1"}]},"object":{"id":"do_312528116260749312248818","type":"TextBook","version":"10","rollup":{}},"mid":"02ba33efff5-15fe-4ec5-b32.1084308E760-3d03ff429fae84","syncts":1577278682630,"@timestamp":"2019-12-25T12:58:02.630Z","type":"events"}
      |""".stripMargin

  val TELEMETRY_AUDIT_EVENT: String =
    """
      |{"ver":"3.0","eid":"AUDIT","ets":1580495389750,"actor":{"type":"System","id":"77ae3fd0-5d27-44e1-ab40-dbc3f723d3ed"},"context":{"cdata":[{"id":"student","type":"UserRole"}],"env":"sdk","channel":"505c7c48ac6dc1edc9b08f21db5a571d","pdata":{"id":"prod.diksha.app","pid":"sunbird.app","ver":"2.6.204"},"sid":"73756d52-2b20-456d-a83f-6f18389f2fa5","did":"8b5538ac75796b7701c9ff02be785035bb245a2e"},"edata":{"state":"Updated","props":["medium","board","grade","syllabus","gradeValue"]},"object":{"id":"77ae3fd0-5d27-44e1-ab40-dbc3f723d3ed","type":"User","version":"","rollup":{}},"mid":"7e46a186-83ef-493b-8e45-814150f9cff9","syncts":1580495403199,"@timestamp":"2020-01-31T18:30:03.199Z","type":"events"}
      |""".stripMargin

  val TELEMETRY_LOG_EVENT: String =
    """
      |{"eid":"LOG","ets":1.580495430158E12,"ver":"3.0","mid":"LP.1580495430158.23ffa945-18b5-4185-b347-5bc355fafaed","actor":{"id":"org.ekstep.learning.platform","type":"System"},"context":{"channel":"in.ekstep","pdata":{"id":"prod.ntp.learning.platform","pid":"search-service","ver":"1.0"},"env":"search"},"edata":{"level":"INFO","type":"api_access","message":"","params":[{"duration":46.0},{"protocol":"HTTP"},{"size":438707.0},{"method":"POST"},{"rid":"ekstep.composite-search.search"},{"uip":"11.4.0.39"},{"url":"/v3/search"},{"status":200.0}]},"syncts":1.580495430158E12,"type":"events","@timestamp":"2020-01-31T18:30:30.158Z"}
      |""".stripMargin


  val DIALCODE_EVENT: String =
    """{"actor":{"type":"User","id":"610bab7d-1450-4e54-bf78-c7c9b14dbc81"},"eid":"INTERACT",
      |"edata":{"type":"OTHER","subtype":"sheen-animation-ended","id":"library","pageid":"library","extra":{"pos":[]}},
      |"ver":"3.0","syncts":1.579564974098E12,"@timestamp":"2020-01-21T00:02:54.098Z","ets":1.579143065071E12,
      |"context":{"cdata":[],"env":"home","channel":" ",
      |"pdata":{"id":"sunbird.app","pid":"sunbird.app","ver":"2.3.144"},"sid":"df936f82-e982-41ec-8412-70d414458272",
      |"did":"45f32f48592cb9bcf26bef9178b7bd20abe24932"},"flags":{"dd_processed":true},
      |"mid":"mid4","type":"events","object":{"id":"GWNI38","type":"DialCode",
      |"version":"","rollup":{}}}""".stripMargin

  val SEARCH_EVENT : String =
    """
      |{"eid":"SEARCH","ets":1577826509166,"ver":"3.0","mid":"LP.1577826509166.c5d13bb7-43c6-4174-9bbe-b06aed6758f2","actor":{"id":"org.ekstep.learning.platform","type":"System"},"context":{"channel":"in.ekstep","pdata":{"id":"dev.sunbird.learning.platform","pid":"search-service","ver":"1.0"},"env":"search"},"edata":{"size":112402,"query":"","filters":{"dialCodes":"WGHSK"},"sort":{},"type":"all","topn":[{"identifier":"do_11278295762528665612"},{"identifier":"domain_14443"},{"identifier":"do_11243460107708006418"},{"identifier":"tpd_medium_code"},{"identifier":"0128260921141493762"}]},"syncts":1577826509166}""".stripMargin

  val EVENT_WITHOUT_EID: String =
    """
      |{"ver":"3.0","ets":1577278681178,"actor":{"type":"User","id":"7c3ea1bb-4da1-48d0-9cc0-c4f150554149"},"context":{"channel":"505c7c48ac6dc1edc9b08f21db5a571d","pdata":{"id":"prod.sunbird.desktop","pid":"sunbird.app","ver":"2.3.162"},"env":"app","sid":"82e41d87-e33f-4269-aeae-d56394985599","did":"1b17c32bad61eb9e33df281eecc727590d739b2b"},"edata":{"dir":"In","type":"File","items":[{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_312785709424099328114191","type":"CONTENT","ver":"1","params":[{"transfers":0,"size":21084308}]},{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_31277435209002188818711","type":"CONTENT","ver":"18","params":[{"transfers":12,"size":"123"}]},{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_31278794857559654411554","type":"TextBook","ver":"1"}]},"object":{"id":"do_312528116260749312248818","type":"TextBook","version":"10","rollup":{}},"mid":"02ba33e5-15fe-4ec5-b32.1084308E760-3d03429fae84","syncts":1577278682630,"@timestamp":"2019-12-25T12:58:02.630Z","type":"events"}
      |""".stripMargin

  val VALID_ERROR_EVENT: String =
    """
      |{"eid":"ERROR","ets":1551344699388,"ver":"3.0","mid":"LP.1553040097857.bf0e4e15-014e-4a22-ba00-e02ff3c38784",
      |"actor":{"id":"e85bcfb5-a8c2-4e65-87a2-0ebb43b45f01","type":"System"},"context":{"channel":"01235953109336064029450",
      |"pdata":{"id":"prod.sunbird.desktop","pid":"learning-service","ver":"1.0"},"env":"framework"},
      |"edata":{"err":"ERR_DATA_NOT_FOUND","stacktrace":"ERR_DATA_NOT_FOUND: Data not found with id : nullntat",
      |"errtype":"system"},"flags":{"tv_processed":true,"dd_processed":true},"type":"events","syncts":1.553040098435E12,
      |"@timestamp":"2019-03-20T00:01:38.435Z"}
    """.stripMargin


}
