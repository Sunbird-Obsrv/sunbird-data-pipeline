package org.sunbird.dp.fixture

object EventFixtures {
  /**
   * Inputs
   *
   * Event - 1 -> Valid INTERACT Event, DeDup-required, Route to Primary Topic -
   *
   * Event - 2 -> Valid ERROR Event, DeDup not required, Route to Error Topic -
   *
   * Event - 3 -> Valid LOG Event, DeDup not required, Route to LOG Topic -
   *
   * Event - 4 - > Valid AUDIT Event, DeDup not required, Route to AUDIT Topic -
   *
   * Event - 5 -> Valid SHARE Event, DeDup Not required, Generates the 3 SHARE_ITEM_EVENT -
   *
   * EVENT - 6 -> 3.1 Version ASSESS Event, Dedup not required, route to primary topic -
   *
   * EVENT - 7 -> INVALID Event, Route to failed topic -
   *
   * EVENT - 8 -> SCHEMA Not Found, Dedup required, Send to Primary Topic -
   *
   * EVENT - 9 -> SCHEMA Not Found, DeDup not required, Send to Primary Topic -
   *
   * EVENT - 10 -> SCHEMA Not found and invalid event
   *
   *
   *
   * ****************************END METRICS_RESULT****************************
   *
   * 1) Total Events Pushed = 12 (3skipped,  7success, 2 failed)
   *
   * 1. primary-route-success-count -> 07
   * 2. share-item-event-success-count -> 03
   * 2. audit-route-success-count -> 01
   * 3. share-route-success-count -> 01
   * 4. log-route-success-count -> 01
   * 5. error-route-success-count -> 01
   * 6. validation-success-event-count -> 10(skip + success)
   * 7. validation-failed-event-count -> 02
   * 8. duplicate-event-count -> 01
   * 9. duplicate-skipped-event-count ->  07
   * 10. unique-event-count -> 02
   * 11. validation-skipped-event-count -> 03
   *
   */

  val EVENT_1: String =
    """{"actor":{"type":"User","id":"610bab7d-1450-4e54-bf78-c7c9b14dbc81"},"eid":"INTERACT","edata":{"type":"OTHER","subtype":"sheen-animation-ended","id":"library","pageid":"library","extra":{"pos":[]}},"ver":"3.0","syncts":1579564974098,"@timestamp":"2020-01-21T00:02:54.098Z","ets":1579143065071,"context":{"cdata":[],"env":"home","channel":" ","pdata":{"id":"dev.sunbird.portal","pid":"sunbird.app","ver":"2.3.144"},"sid":"df936f82-e982-41ec-8412-70d414458272","did":"45f32f48592cb9bcf26bef9178b7bd20abe24932"},"flags":{"dd_processed":true},"mid":"mid4","type":"events","object":{"id":"GWNI38","type":"DialCode","version":"","rollup":{}}}""".stripMargin

  val EVENT_2: String =
    """
      |{"eid":"ERROR","ets":1551344699388,"ver":"3.0","mid":"LP.1553040097857.bf0e4e15-014e-4a22-ba00-e02ff3c38784",
      |"actor":{"id":"e85bcfb5-a8c2-4e65-87a2-0ebb43b45f01","type":"System"},"context":{"channel":"01235953109336064029450",
      |"pdata":{"id":"dev.sunbird.app","pid":"learning-service","ver":"1.0"},"env":"framework"},
      |"edata":{"err":"ERR_DATA_NOT_FOUND","stacktrace":"ERR_DATA_NOT_FOUND: Data not found with id : nullntat",
      |"errtype":"system"},"flags":{"tv_processed":true,"dd_processed":true},"type":"events","syncts":1.553040098435E12,
      |"@timestamp":"2019-03-20T00:01:38.435Z"}
      """.stripMargin


  val EVENT_3: String =
    """
      |{"eid":"LOG","ets":1.580495430158E12,"ver":"3.0","mid":"LP.1580495430158.23ffa945-18b5-4185-b347-5bc355fafaed","actor":{"id":"org.ekstep.learning.platform","type":"System"},"context":{"channel":"in.ekstep","pdata":{"id":"sunbird.dev.app","pid":"search-service","ver":"1.0"},"env":"search"},"edata":{"level":"INFO","type":"api_access","message":"","params":[{"duration":46.0},{"protocol":"HTTP"},{"size":438707.0},{"method":"POST"},{"rid":"ekstep.composite-search.search"},{"uip":"11.4.0.39"},{"url":"/v3/search"},{"status":200.0}]},"syncts":1.580495430158E12,"type":"events","@timestamp":"2020-01-31T18:30:30.158Z"}
      |""".stripMargin


  val EVENT_4: String =
    """
      |{"ver":"3.0","eid":"AUDIT","ets":1580495389750,"actor":{"type":"System","id":"77ae3fd0-5d27-44e1-ab40-dbc3f723d3ed"},"context":{"cdata":[{"id":"student","type":"UserRole"}],"env":"sdk","channel":"505c7c48ac6dc1edc9b08f21db5a571d","pdata":{"id":"sunbird.dev.app","pid":"sunbird.app","ver":"2.6.204"},"sid":"73756d52-2b20-456d-a83f-6f18389f2fa5","did":"8b5538ac75796b7701c9ff02be785035bb245a2e"},"edata":{"state":"Updated","props":["medium","board","grade","syllabus","gradeValue"]},"object":{"id":"77ae3fd0-5d27-44e1-ab40-dbc3f723d3ed","type":"User","version":"","rollup":{}},"mid":"7e46a186-83ef-493b-8e45-814150f9cff9","syncts":1580495403199,"@timestamp":"2020-01-31T18:30:03.199Z","type":"events"}
      |""".stripMargin


  val EVENT_5: String =
    """
      |{"ver":"3.0","eid":"SHARE","ets":1577278681178,"actor":{"type":"User","id":"7c3ea1bb-4da1-48d0-9cc0-c4f150554149"},"context":{"channel":"505c7c48ac6dc1edc9b08f21db5a571d","pdata":{"id":"dev.sunbird.app","pid":"sunbird.app","ver":"2.3.162"},"env":"app","sid":"82e41d87-e33f-4269-aeae-d56394985599","did":"1b17c32bad61eb9e33df281eecc727590d739b2b"},"edata":{"dir":"In","type":"File","items":[{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_312785709424099328114191","type":"CONTENT","ver":"1","params":[{"transfers":0,"size":21084308}]},{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_31277435209002188818711","type":"CONTENT","ver":"18","params":[{"transfers":12,"size":""}]},{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_31278794857559654411554","type":"TextBook","ver":"1"}]},"object":{"id":"do_312528116260749312248818","type":"TextBook","version":"10","rollup":{}},"mid":"02ba33e5-15fe-4ec5-b32.1084308E760-3d03429fae84","tags": [], "syncts":1577278682630,"@timestamp":"2019-12-25T12:58:02.630Z","type":"events"}
      |""".stripMargin

  val EVENT_6: String =
    """
      |{"eid":"ASSESS","ets":1586431492513,"ver":"3.1","mid":"ASSESS:12159f2827880221eef12a6be9560379:test","actor":{"id":"6e89dba6-10d6-4044-9105-b80ce7f56b38","type":"User"},"context":{"channel":"01275678925675724817","pdata":{"id":"sunbird.dev.app","ver":"2.8.260preproduction","pid":"sunbird.app.contentplayer"},"env":"contentplayer","sid":"0cbc018c","did":"b9bdcb8cd7abc5bd7813bd65ec0b5084dc0dadd8","cdata":[]},"object":{"id":"do_212995828601487360194","type":"Content","ver":"2"},"tags":[],"edata":{"item":{"id":"do_21299582901864857613016","maxscore":1,"type":"ftb","exlength":0,"params":[{"eval":"order"}],"uri":"","title":"Registration","mmc":[],"mc":[],"desc":""},"index":1,"pass":"Yes","score":1,"resvalues":[{"1":"{\"text\":\"NARENDRA MODI\"}"}],"duration":2},"syncts":1586431504608,"@timestamp":"2020-04-09T11:25:04.608Z"}
      |""".stripMargin

  // Invalid
  val EVENT_7: String =
    """
      |
      |{"ver":3,"eid":"START","ets":"1577278681178","object":{"id":"do_312528116260749312248818","type":"TextBook","version":"10","rollup":{}},"mid":"02ba33efff5-15fe-4ec5-b32.1084308E760-3d03ff429fae84","syncts":1577278682630,"@timestamp":"2019-12-25T12:58:02.630Z","type":"events"}
      |""".stripMargin

  val EVENT_8 =
    """
      |{"ver":"3.0","eid":"NO_EID","ets":1577278681178,"actor":{"type":"User","id":"7c3ea1bb-4da1-48d0-9cc0-c4f150554149"},"context":{"channel":"505c7c48ac6dc1edc9b08f21db5a571d","pdata":{"id":"dev.sunbird.app","pid":"sunbird.app","ver":"2.3.162"},"env":"app","sid":"82e41d87-e33f-4269-aeae-d56394985599","did":"1b17c32bad61eb9e33df281eecc727590d739b2b"},"edata":{"dir":"In","type":"File","items":[{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_312785709424099328114191","type":"CONTENT","ver":"1","params":[{"transfers":0,"size":21084308}]},{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_31277435209002188818711","type":"CONTENT","ver":"18","params":[{"transfers":12,"size":"123"}]},{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_31278794857559654411554","type":"TextBook","ver":"1"}]},"object":{"id":"do_312528116260749312248818","type":"TextBook","version":"10","rollup":{}},"mid":"02ba33e5-15fe-4ec5-b32.1084308E760-3d03429fae84","syncts":1577278682630,"@timestamp":"2019-12-25T12:58:02.630Z","type":"events"}
      |
      |""".stripMargin


  val EVENT_9 =
    """
      |{"ver":"3.0","eid":"NO_EID","ets":1577278681178,"actor":{"type":"User","id":"7c3ea1bb-4da1-48d0-9cc0-c4f150554149"},"context":{"channel":"505c7c48ac6dc1edc9b08f21db5a571d","pdata":{"id":"dev.sunbird.portal","pid":"sunbird.app","ver":"2.3.162"},"env":"app","sid":"82e41d87-e33f-4269-aeae-d56394985599","did":"1b17c32bad61eb9e33df281eecc727590d739b2b"},"edata":{"dir":"In","type":"File","items":[{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_312785709424099328114191","type":"CONTENT","ver":"1","params":[{"transfers":0,"size":21084308}]},{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_31277435209002188818711","type":"CONTENT","ver":"18","params":[{"transfers":12,"size":"123"}]},{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_31278794857559654411554","type":"TextBook","ver":"1"}]},"object":{"id":"do_312528116260749312248818","type":"TextBook","version":"10","rollup":{}},"mid":"02ba33e5-15fe-4ec5-b32.1084308E760-3d03429fae84","syncts":1577278682630,"@timestamp":"2019-12-25T12:58:02.630Z","type":"events"}
      |
      |""".stripMargin

  //Invalid
  val EVENT_10 =
    """
      |{"ver":"3.0","eid":"NO_EID","ets":1577278681178,"actor":{"type":"User","id":"7c3ea1bb-4da1-48d0-9cc0-c4f150554149"}}
      |
      |""".stripMargin


  val EVENT_11 = EVENT_1 // Duplicate Event

  val EVENT_12 =
    """
      |{"eid":"SEARCH","ets":1577826509166,"ver":"3.0","mid":"LP.1577826509166.c5d13bb7-43c6-4174-9bbe-b06aed6758f2","actor":{"id":"org.ekstep.learning.platform","type":"System"},"context":{"channel":"in.ekstep","pdata":{"id":"dev.sunbird.learning.platform","pid":"search-service","ver":"1.0"},"env":"search"},"edata":{"size":112402,"query":"","filters":{"dialCodes":"WGHSK"},"sort":{},"type":"all","topn":[{"identifier":"do_11278295762528665612"},{"identifier":"domain_14443"},{"identifier":"do_11243460107708006418"},{"identifier":"tpd_medium_code"},{"identifier":"0128260921141493762"}]},"syncts":1577826509166}
      |
      |""".stripMargin


  val EVENT_WITHOUT_EID: String =
    """
      |{"ver":"3.0","ets":1577278681178,"actor":{"type":"User","id":"7c3ea1bb-4da1-48d0-9cc0-c4f150554149"},"context":{"channel":"505c7c48ac6dc1edc9b08f21db5a571d","pdata":{"id":"prod.sunbird.desktop","pid":"sunbird.app","ver":"2.3.162"},"env":"app","sid":"82e41d87-e33f-4269-aeae-d56394985599","did":"1b17c32bad61eb9e33df281eecc727590d739b2b"},"edata":{"dir":"In","type":"File","items":[{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_312785709424099328114191","type":"CONTENT","ver":"1","params":[{"transfers":0,"size":21084308}]},{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_31277435209002188818711","type":"CONTENT","ver":"18","params":[{"transfers":12,"size":"123"}]},{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_31278794857559654411554","type":"TextBook","ver":"1"}]},"object":{"id":"do_312528116260749312248818","type":"TextBook","version":"10","rollup":{}},"mid":"02ba33e5-15fe-4ec5-b32.1084308E760-3d03429fae84","syncts":1577278682630,"@timestamp":"2019-12-25T12:58:02.630Z","type":"events"}
      |""".stripMargin

  // Invalid SUMMARY event with edata.extra.[].value datatype mismatch
  val EVENT_13 =
    """
      |{"eid":"SUMMARY","ets":1625043400402,"ver":"3.0","mid":"SUMMARY:a3e517153c4ba392297e70521aa5e17a","actor":{"id":"70e496fe83dee324009a847ea222bc5c","type":"User"},"context":{"channel":"01268904781886259221","pdata":{"id":"staging.sunbird.portal","ver":"4.1.0","pid":"sunbird-portal"},"env":"contentplayer","sid":"73d82044-8ea5-dffc-1af5-6cdf2a1fa1da","did":"70e496fe83dee324009a847ea222bc5c","cdata":[{"id":"kubXMwcsJK2JANa0PeYc00GK5CSXoS1q","type":"ContentSession"},{"id":"xcFG0rntKUGltu2m8zJh7ZqattT9u3Ix","type":"PlaySession"},{"id":"2.0","type":"PlayerVersion"}],"rollup":{"l1":"01268904781886259221"},"uid":"anonymous"},"object":{"id":"do_213302422998196224166","ver":"1","type":"Content","rollup":{}},"tags":["01268904781886259221"],"edata":{"type":"content","mode":"play","starttime":1625043385301,"endtime":1625043401236,"timespent":16,"pageviews":2,"interactions":2,"extra":[{"id":"progress","value":100},{"id":"endpageseen","value":true},{"id":"score","value":2},{"id":"correct","value":2},{"id":"incorrect","value":0},{"id":"partial","value":0},{"id":"skipped","value":0}]}}
      |
      |""".stripMargin

  // Invalid SUMMARY event with edata.timespent datatype mismatch
  val EVENT_14 =
    """
      |{"eid":"SUMMARY","ets":1625043400402,"ver":"3.0","mid":"SUMMARY:a3e517153c4ba392297e70521aa5e17a","actor":{"id":"70e496fe83dee324009a847ea222bc5c","type":"User"},"context":{"channel":"01268904781886259221","pdata":{"id":"staging.sunbird.portal","ver":"4.1.0","pid":"sunbird-portal"},"env":"contentplayer","sid":"73d82044-8ea5-dffc-1af5-6cdf2a1fa1da","did":"70e496fe83dee324009a847ea222bc5c","cdata":[{"id":"kubXMwcsJK2JANa0PeYc00GK5CSXoS1q","type":"ContentSession"},{"id":"xcFG0rntKUGltu2m8zJh7ZqattT9u3Ix","type":"PlaySession"},{"id":"2.0","type":"PlayerVersion"}],"rollup":{"l1":"01268904781886259221"},"uid":"anonymous"},"object":{"id":"do_213302422998196224166","ver":"1","type":"Content","rollup":{}},"tags":["01268904781886259221"],"edata":{"type":"content","mode":"play","starttime":1625043385301,"endtime":1625043401236,"timespent":"0:16","pageviews":2,"interactions":2,"extra":[{"id":"progress","value":"100"},{"id":"endpageseen","value":"true"},{"id":"score","value":"2"},{"id":"correct","value":"2"},{"id":"incorrect","value":"0"},{"id":"partial","value":"0"},{"id":"skipped","value":"0"}]}}
      |
      |""".stripMargin

  // Valid SUMMARY event
  val EVENT_15 =
    """
      |{"eid":"SUMMARY","ets":1625043400402,"ver":"3.0","mid":"SUMMARY:a3e517153c4ba392297e70521aa5e17a","actor":{"id":"70e496fe83dee324009a847ea222bc5c","type":"User"},"context":{"channel":"01268904781886259221","pdata":{"id":"staging.sunbird.portal","ver":"4.1.0","pid":"sunbird-portal"},"env":"contentplayer","sid":"73d82044-8ea5-dffc-1af5-6cdf2a1fa1da","did":"70e496fe83dee324009a847ea222bc5c","cdata":[{"id":"kubXMwcsJK2JANa0PeYc00GK5CSXoS1q","type":"ContentSession"},{"id":"xcFG0rntKUGltu2m8zJh7ZqattT9u3Ix","type":"PlaySession"},{"id":"2.0","type":"PlayerVersion"}],"rollup":{"l1":"01268904781886259221"},"uid":"anonymous"},"object":{"id":"do_213302422998196224166","ver":"1","type":"Content","rollup":{}},"tags":["01268904781886259221"],"edata":{"type":"content","mode":"play","starttime":1625043385301,"endtime":1625043401236,"timespent":16,"pageviews":2,"interactions":2,"extra":[{"id":"progress","value":"100"},{"id":"endpageseen","value":"true"},{"id":"score","value":"2"},{"id":"correct","value":"2"},{"id":"incorrect","value":"0"},{"id":"partial","value":"0"},{"id":"skipped","value":"0"}]}}
      |
      |""".stripMargin

  // Invalid METRICS event with edata.subsystem missing
  val EVENT_16 =
    """
      |{"@timestamp":"2021-05-09T06:53:58.317Z","actor":{"id":"5e78cb4a62f5c60d3a007ff366c740727afa2aec42f9e63e26fb1a879b87513b","type":"User"},"context":{"cdata":[],"did":"5e78cb4a62f5c60d3a007ff366c740727afa2aec42f9e63e26fb1a879b87513b","channel":"desktop","env":"DesktopApp","pdata":{"id":"desktop,app","pid":"desktop.app","ver":"1.0"},"sid":"0e2dcdd2-e81c-4e9c-9c43-b41a6fdd6c48"},"edata":{"metrics":[{"metric":"minApiTime","value":0.002482144},{"metric":"maxApiTime","value":7.236265204},{"metric":"avgApiTime","value":0.5468935359999998},{"metric":"totalApiS","value":50.0},{"metric":"minAppStartupTime","value":6.566},{"metric":"maxAppStartupTime","value":11.422},{"metric":"avgAppStartupTime","value":9.755999999999998},{"metric":"totalAppStartupS","value":3.0},{"metric":"minImportTime","value":1.196166354944613},{"metric":"maxImportTime","value":1.196166354944613},{"metric":"avgImportTime","value":1.196166354944613},{"metric":"totalImportS","value":1.0},{"metric":"createdDate","value":1620431999999.0}],"system":"DesktopApp"},"eid":"METRICS","ets":1620542437965.0,"mid":"METRICS:1711d2706507cbd2e22ec88f7660a569","object":{},"syncts":1620543238317,"tags":[],"ver":"3.0"}
      |
      |""".stripMargin

  // Valid METRICS event
  val EVENT_17 =
    """
      |{"@timestamp":"2021-05-09T06:53:58.317Z","actor":{"id":"5e78cb4a62f5c60d3a007ff366c740727afa2aec42f9e63e26fb1a879b87513b","type":"User"},"context":{"cdata":[],"did":"5e78cb4a62f5c60d3a007ff366c740727afa2aec42f9e63e26fb1a879b87513b","channel":"desktop","env":"DesktopApp","pdata":{"id":"desktop,app","pid":"desktop.app","ver":"1.0"},"sid":"0e2dcdd2-e81c-4e9c-9c43-b41a6fdd6c48"},"edata":{"metrics":[{"metric":"minApiTime","value":0.002482144},{"metric":"maxApiTime","value":7.236265204},{"metric":"avgApiTime","value":0.5468935359999998},{"metric":"totalApiS","value":50.0},{"metric":"minAppStartupTime","value":6.566},{"metric":"maxAppStartupTime","value":11.422},{"metric":"avgAppStartupTime","value":9.755999999999998},{"metric":"totalAppStartupS","value":3.0},{"metric":"minImportTime","value":1.196166354944613},{"metric":"maxImportTime","value":1.196166354944613},{"metric":"avgImportTime","value":1.196166354944613},{"metric":"totalImportS","value":1.0},{"metric":"createdDate","value":1620431999999.0}],"subsystem":"DesktopApp","system":"DesktopApp"},"eid":"METRICS","ets":1620542437965.0,"mid":"METRICS:1711d2706507cbd2e22ec88f7660a569","object":{},"syncts":1620543238317,"tags":[],"ver":"3.0"}
      |
      |""".stripMargin
}
