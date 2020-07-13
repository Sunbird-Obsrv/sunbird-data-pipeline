package org.sunbird.dp.fixture

import org.joda.time.DateTime

object EventFixture {

  val deviceCacheData1 = """{"country":"India","state_custom":"Karnataka","devicespec":"{}","city":"Bengaluru","uaspec":"{}","district_custom":"BENGALURU URBAN SOUTH","country_code":"IN","firstaccess":"1571999041881","state_code_custom":"29"}"""
  val deviceCacheData2 = """{"user_declared_state":"Maharashtra","state_custom":"Maharashtra","devicespec":"{\"scrn\":\"5.46\",\"camera\":\"\",\"idisk\":\"25.44\",\"os\":\"Android 9\",\"id\":\"45f32f48592cb9bcf26bef9178b7bd20abe24932\",\"sims\":\"-1\",\"cpu\":\"abi: armeabi-v7a processor\t: 0 \",\"webview\":\"79.0.3945.116\",\"edisk\":\"25.42\",\"make\":\"Samsung SM-J400F\"}","uaspec":"{\"agent\":\"UNKNOWN\",\"ver\":\"UNKNOWN\",\"system\":\"Android\",\"raw\":\"Dalvik/2.1.0 (Linux U Android 9 SM-J400F Build/PPR1.180610.011)\"}","city":"Mumbai","country_code":"IN","firstaccess":"1578972432419","country":"India","country_name":"India","state":"Maharashtra","continent_name":"Asia","state_code":"MH","fcm_token":"d3ddT88xXLI:APA91bF9lJ4eH8tshAPgKiiZ3hL3sbib0pUN2I388T58oFDxUBQ2WKKuvtBga6iKiOPrgssNKLs4QBjZxE_BbtdGdO0gPdFPataEeXshgYxMKC0VT-oyjrNIZKdKkybQoyichBCiokTD","producer":"sunbirddev.diksha.app","district_custom":"Mumbai","user_declared_district":"Raigad","state_code_custom":"27"}"""

  val userCacheData1 = """{"usersignintype":"Anonymous","usertype":"TEACHER"}"""
  val userCacheData2 = """{"channel":"KV123","phoneverified":false,"createdby":"c8e51123-61a3-454d-beb0-2202450b0096","subject":["English"],"email":"BJAguqy3GaJECrYqDUPjeducVxa5J9ZsW9A8qc7YHelkV7KbgkCKW10quCbhpgxbh2t4toXC8uXW\\ngiguS+8ucwzbmgPm7q7YSYz26SfpHnzBo/0Vh3TWqr2MOq9LlX6gT6a+wzaAmCWueMEdPmZuRg==","username":"I+CyiN6Bx0GCRm9lkA3xn5uNBm0AODhxeDwJebxxBfuGJ5V2v1R8v1PEQsP+V+y9sAFcM2WtaMLj\\n91hpzBq0PFcQTq6OSPQOm0sySPXTDzyLvm1cKaLwzvJ6fzLLs9nKT6a+wzaAmCWueMEdPmZuRg==","firstname":"A512","framework":{},"userid":"610bab7d-1450-4e54-bf78-c7c9b14dbc81","usertype":"TEACHER","rootorgid":"0126978705345576967","id":"610bab7d-1450-4e54-bf78-c7c9b14dbc81","language":[],"grade":[],"roles":["BOOK_REVIEWER"],"status":1,"webpages":[],"createddate":"2019-04-11 08:58:16:512+0000","emailverified":true,"isdeleted":false,"locationids":[],"maskedemail":"a5**@yopmail.com","profilevisibility":{},"loginid":"I+CyiN6Bx0GCRm9lkA3xnx2W8+QgN39Y0We3KjR98O8hD6YjyoCirIBDsWHGwRf65PY/Cx+pFFK1\\nIz1VinIaKgDnSQwkl7ajzQjjRTzQbKOyHsAXkJgo9I5l7ulEYVXRT6a+wzaAmCWueMEdPmZuRg==","usersignintype":"Self-Signed-In","userlogintype":"Student","state":"Telangana","district":"Hyderabad"}"""

  val contentCacheData1 = """{"code":"org.ekstep.literacy.story.21797","keywords":["Story"],"subject":["English"],"channel":"in.ekstep","description":"Write a short description of your lesson","language":["English"],"mimeType":"application/vnd.ekstep.ecml-archive","idealScreenSize":"normal","createdOn":"2018-04-26T09:59:27.336+0000","objectType":"Content","gradeLevel":["Class 9"],"appId":"ekstep_portal","contentDisposition":"inline","contentEncoding":"gzip","lastUpdatedOn":1571999041881,"lastSubmittedOn":"2018-06-15T13:06:56.090+0000","lastPublishedOn":1571999041881,"contentType":"Resource","lastUpdatedBy":"7224","identifier":"do_31249064359802470412856","audience":["Learner"],"creator":"Pravin Panpatil","os":["All"],"visibility":"Default","consumerId":"62e15662-bb09-439f-86e2-d65bd84f3c23","mediaType":"content","osId":"org.ekstep.quiz.app","graph_id":"domain","nodeType":"DATA_NODE","versionKey":"1524736767336","idealScreenDensity":"hdpi","framework":"NCF","createdBy":"7224","compatibilityLevel":1.0,"domain":["literacy"],"name":"Walk a little slower","board":"State (Maharashtra)","resourceType":"Read","status":"Draft","node_id":310297.0,"license":"CC BY 4.0","copyright":"Ekstep","author":"Ekstep","copyrightYear":2019.0}"""
  val contentCacheData2 = """{"identifier":"do_312526125187809280139353","code":"c361b157-408c-4347-9be3-38d9d4ea2b90","visibility":"Parent","description":"Anction Words","mimeType":"application/vnd.ekstep.content-collection","graph_id":"domain","nodeType":"DATA_NODE","createdOn":"2018-06-15T13:06:56.090+0000","versionKey":"1529068016090","objectType":"Content","dialcodes":["TNPF7T"],"collections":["do_312526125186424832139255"],"name":"Anction Words","lastUpdatedOn":"2018-06-15T13:06:56.090+0000","lastsubmittedon":1571999041881,"lastPublishedOn":"2018-06-15T13:06:56.090+0000","contentType":"TextBookUnit","status":"Draft","node_id":438622.0,"license":"CC BY 4.0"}"""
  val contentCacheData3 = """{"code":"do_312526125187809280139355","channel":"in.ekstep","downloadUrl":"https://ekstep-public-prod.s3-ap-south-1.amazonaws.com/content/14681653089315c3173a3e1.mp3","language":["English"],"mimeType":"application/octet-stream","idealScreenSize":"normal","createdOn":"2016-07-10T15:41:49.111+0000","objectType":"Content","gradeLevel":["Class 1"],"contentDisposition":"inline","contentEncoding":"identity","lastUpdatedOn":"2017-03-10T18:10:00.448+0000","contentType":"Asset","identifier":"do_312526125187809280139355","os":["All"],"visibility":"Default","mediaType":"audio","osId":"org.ekstep.launcher","ageGroup":["5-6"],"graph_id":"domain","nodeType":"DATA_NODE","pkgVersion":1.0,"versionKey":"1489169400448","license":"CC BY 4.0","idealScreenDensity":"hdpi","framework":"NCF","compatibilityLevel":1.0,"name":"do_312526125187809280139355","status":"Live","node_id":65545.0,"size":677510.0}"""

  val dialcodeCacheData1 = """{"identifier":"GWNI38","batchcode":"jkpublisher.20180801T134105","channel":"01254592085869363222","dialcode_index":2328993,"generated_on":"2018-08-01T13:41:53.695","published_on":1571999041881,"publisher":"jkpublisher","status":"Draft"}"""
  val dialcodeCacheData2 = """{"identifier":"PCZKA3","batchcode":"jkpublisher.20180801T122031","channel":"01254592085869363222","dialcode_index":1623464,"generated_on":1571999041881,"published_on":"2018-08-01T13:41:53.695Z","publisher":"jkpublisher","status":"Draft"}"""

  val currentDate: Long = DateTime.now().getMillis
  val olderDate: Long = DateTime.now().minusMonths(5).getMillis
  val futureDate: Long = DateTime.now().plusMonths(1).getMillis

  val summaryEvents: List[String] = List(

    // user_denorm = false, device_denorm=true
    s"""
       |{"eid":"ME_WORKFLOW_SUMMARY","ets":$currentDate,"syncts":$currentDate,"ver":"1.0",
       |"mid":"mid1","uid":"b7470841-7451-43db-b5c7-2dcf4f8d3b23",
       |"context":{"pdata":{"id":"AnalyticsDataPipeline","ver":"1.0","model":"WorkflowSummarizer"},
       |"granularity":"SESSION","date_range":{"from":1.594561551979E12,"to":1.594561557071E12},
       |"cdata":[]},"dimensions":{"did":"45f32f48592cb9bcf26bef9178b7bd20abe24932","pdata":{"id":"prod.diksha.app",
       |"ver":"2.10.294","pid":"sunbird.app"},"sid":"4d023503-b382-4410-bd93-61631da857da","channel":
       |"505c7c48ac6dc1edc9b08f21db5a571d","type":"session","mode":""},"edata":{"eks":{"interact_events_per_min":2.0,
       |"start_time":1.594561551979E12,"interact_events_count":2.0,"item_responses":[],"end_time":1.594561557071E12,
       |"events_summary":[{"id":"START","count":1.0},{"id":"INTERACT","count":6.0},{"id":"END","count":1.0}],
       |"page_summary":[],"time_diff":5.09,"telemetry_version":"3.0","env_summary":[],"time_spent":5.09}},
       |"object":{"id":"","type":"","rollup":{}},"type":"events"}
     """.stripMargin,
    //device_denorm=true, content_denorm=true, collection_denorm=true
    s"""{"eid":"ME_WORKFLOW_SUMMARY","ets":$currentDate,"syncts":$currentDate,"ver":"1.0","mid":
       |"mid2","uid":"610bab7d-1450-4e54-bf78-c7c9b14dbc81","context":{"pdata":
       |{"id":"AnalyticsDataPipeline","ver":"1.0","model":"WorkflowSummarizer"},"granularity":"SESSION","date_range":
       |{"from":1.59455777247E12,"to":1.594557901023E12},"rollup":{"l1":"0126684405014528002"},"cdata":[{"id":"streaming",
       |"type":"PlayerLaunch"},{"id":"10c774726243b17874c4072fd2684618","type":"ContentSession"},
       |{"id":"f26677e302ceab599ecc87b199cc7cb0","type":"PlaySession"},{"id":"27f0be5c-616f-45b4-a12d-8a06f748867d",
       |"type":"UserSession"}]},"dimensions":{"did":"264d679186d4b0734d858d4e18d4d31e","pdata":{"id":"prod.diksha.app",
       |"ver":"3.0.352","pid":"sunbird.app.contentplayer"},"sid":"27f0be5c-616f-45b4-a12d-8a06f748867d","channel":
       |"0126684405014528002","type":"content","mode":"play"},"edata":{"eks":{"interact_events_per_min":6.53,
       |"start_time":1.59455777247E12,"interact_events_count":14.0,"item_responses":[],"end_time":1.594557901023E12,
       |"events_summary":[{"id":"START","count":1.0},{"id":"INTERACT","count":19.0},{"id":"IMPRESSION","count":6.0},
       |{"id":"INTERRUPT","count":1.0}],"page_summary":[{"id":"content-detail","type":"view","env":"home",
       |"time_spent":6.61,"visit_count":1.0},{"id":"139465e7-183a-420d-8bab-41c46b7e7927","type":"workflow","env":
       |"contentplayer","time_spent":33.34,"visit_count":1.0},{"id":"7c04c53b-2871-4221-9757-cd04bdbb390e","type":
       |"workflow","env":"contentplayer","time_spent":7.85,"visit_count":1.0}],"time_diff":128.55,"telemetry_version":
       |"3.0","env_summary":[{"env":"contentplayer","time_spent":41.19,"count":1.0},{"env":"home","time_spent":6.61,
       |"count":1.0}],"time_spent":128.57}},"tags":[],"object":{"id":"do_31249064359802470412856","type":"Content",
       |"ver":"1","rollup":{"l1":"do_312526125187809280139353"}},"type":"events"}""".stripMargin,
    s"""
       |{"eid":"ME_WORKFLOW_SUMMARY","ets":$currentDate,"syncts":$currentDate,"ver":"1.0",
       |"mid":"mid1","uid":"b7470841-7451-43db-b5c7-2dcf4f8d3b23",
       |"context":{"pdata":{"id":"AnalyticsDataPipeline","ver":"1.0","model":"WorkflowSummarizer"},
       |"granularity":"SESSION","date_range":{"from":1.594561551979E12,"to":1.594561557071E12},
       |"cdata":[]},"dimensions":{"did":"45f32f48592cb9bcf26bef9178b7bd20abe24932","pdata":{"id":"prod.diksha.app",
       |"ver":"2.10.294","pid":"sunbird.app"},"sid":"4d023503-b382-4410-bd93-61631da857da","channel":
       |"505c7c48ac6dc1edc9b08f21db5a571d","type":"session","mode":""},"edata":{"eks":{"interact_events_per_min":2.0,
       |"start_time":1.594561551979E12,"interact_events_count":2.0,"item_responses":[],"end_time":1.594561557071E12,
       |"events_summary":[{"id":"START","count":1.0},{"id":"INTERACT","count":6.0},{"id":"END","count":1.0}],
       |"page_summary":[],"time_diff":5.09,"telemetry_version":"3.0","env_summary":[],"time_spent":5.09}},
       |"object":{"id":"","type":"","rollup":{}},"type":"events"}
     """.stripMargin
  )

}

