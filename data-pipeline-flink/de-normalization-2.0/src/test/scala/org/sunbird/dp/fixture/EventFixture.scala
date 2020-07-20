package org.sunbird.dp.fixture

import java.util

import com.google.gson.Gson
import org.joda.time.DateTime

import scala.collection.JavaConversions.mapAsJavaMap

object EventFixture {
  
  val gson = new Gson()
  val deviceCacheData1 = """{"country":"India","state_custom":"Karnataka","devicespec":"{}","city":"Bengaluru","uaspec":"{}","district_custom":"BENGALURU URBAN SOUTH","country_code":"IN","firstaccess":"1571999041881","state_code_custom":"29"}"""
  val deviceCacheData2 = """{"user_declared_state":"Maharashtra","state_custom":"Maharashtra","devicespec":"{\"scrn\":\"5.46\",\"camera\":\"\",\"idisk\":\"25.44\",\"os\":\"Android 9\",\"id\":\"45f32f48592cb9bcf26bef9178b7bd20abe24932\",\"sims\":\"-1\",\"cpu\":\"abi: armeabi-v7a processor\t: 0 \",\"webview\":\"79.0.3945.116\",\"edisk\":\"25.42\",\"make\":\"Samsung SM-J400F\"}","uaspec":"{\"agent\":\"UNKNOWN\",\"ver\":\"UNKNOWN\",\"system\":\"Android\",\"raw\":\"Dalvik/2.1.0 (Linux U Android 9 SM-J400F Build/PPR1.180610.011)\"}","city":"Mumbai","country_code":"IN","firstaccess":"1578972432419","country":"India","country_name":"India","state":"Maharashtra","continent_name":"Asia","state_code":"MH","fcm_token":"d3ddT88xXLI:APA91bF9lJ4eH8tshAPgKiiZ3hL3sbib0pUN2I388T58oFDxUBQ2WKKuvtBga6iKiOPrgssNKLs4QBjZxE_BbtdGdO0gPdFPataEeXshgYxMKC0VT-oyjrNIZKdKkybQoyichBCiokTD","producer":"sunbirddev.diksha.app","district_custom":"Mumbai","user_declared_district":"Raigad","state_code_custom":"27"}"""
  
  val userCacheData1 = """{"usersignintype":"Anonymous","usertype":"TEACHER"}"""
  val userCacheData2 = """{"channel":"KV123","phoneverified":"false","createdby":"c8e51123-61a3-454d-beb0-2202450b0096","subject":"[\"English\"]","email":"BJAguqy3GaJECrYqDUPjeducVxa5J9ZsW9A8qc7YHelkV7KbgkCKW10quCbhpgxbh2t4toXC8uXW\\ngiguS+8ucwzbmgPm7q7YSYz26SfpHnzBo/0Vh3TWqr2MOq9LlX6gT6a+wzaAmCWueMEdPmZuRg==","username":"I+CyiN6Bx0GCRm9lkA3xn5uNBm0AODhxeDwJebxxBfuGJ5V2v1R8v1PEQsP+V+y9sAFcM2WtaMLj\\n91hpzBq0PFcQTq6OSPQOm0sySPXTDzyLvm1cKaLwzvJ6fzLLs9nKT6a+wzaAmCWueMEdPmZuRg==","firstname":"A512","framework":"{}","userid":"610bab7d-1450-4e54-bf78-c7c9b14dbc81","usertype":"TEACHER","rootorgid":"0126978705345576967","id":"610bab7d-1450-4e54-bf78-c7c9b14dbc81","language":"","grade":"","roles":"[\"BOOK_REVIEWER\"]","status":"1","webpages":"[]","createddate":"2019-04-11 08:58:16:512+0000","emailverified":"true","isdeleted":"false","locationids":"[\"location-1\",\"location-2\",\"location-3\"]","maskedemail":"a5**@yopmail.com","profilevisibility":"{}","loginid":"I+CyiN6Bx0GCRm9lkA3xnx2W8+QgN39Y0We3KjR98O8hD6YjyoCirIBDsWHGwRf65PY/Cx+pFFK1\\nIz1VinIaKgDnSQwkl7ajzQjjRTzQbKOyHsAXkJgo9I5l7ulEYVXRT6a+wzaAmCWueMEdPmZuRg==","usersignintype":"Self-Signed-In","userlogintype":"Student","state":"Telangana","district":"Hyderabad"}"""
  val userCacheDataMap1 = mapAsJavaMap(Map("usersignintype" -> "Validated", "usertype" -> "TEACHER"))
  val userCacheDataMap2 = gson.fromJson(userCacheData2, new util.HashMap[String, String]().getClass)

  val contentCacheData1 = """{"code":"org.ekstep.literacy.story.21797","keywords":["Story"],"subject":["English"],"channel":"in.ekstep","description":"Write a short description of your lesson","language":["English"],"mimeType":"application/vnd.ekstep.ecml-archive","idealScreenSize":"normal","createdOn":"2018-04-26T09:59:27.336+0000","objectType":"Content","gradeLevel":["Class 9"],"appId":"ekstep_portal","contentDisposition":"inline","contentEncoding":"gzip","lastUpdatedOn":1571999041881,"lastSubmittedOn":"2018-06-15T13:06:56.090+0000","lastPublishedOn":1571999041881,"contentType":"Resource","lastUpdatedBy":"7224","identifier":"do_31249064359802470412856","audience":["Learner"],"creator":"Pravin Panpatil","os":["All"],"visibility":"Default","consumerId":"62e15662-bb09-439f-86e2-d65bd84f3c23","mediaType":"content","osId":"org.ekstep.quiz.app","graph_id":"domain","nodeType":"DATA_NODE","versionKey":"1524736767336","idealScreenDensity":"hdpi","framework":"NCF","createdBy":"7224","compatibilityLevel":1.0,"domain":["literacy"],"name":"Walk a little slower","board":"State (Maharashtra)","resourceType":"Read","status":"Draft","node_id":310297.0,"license":"CC BY 4.0","copyright":"Ekstep","author":"Ekstep","copyrightYear":2019.0}"""
  val contentCacheData2 = """{"identifier":"do_312526125187809280139353","code":"c361b157-408c-4347-9be3-38d9d4ea2b90","visibility":"Parent","description":"Anction Words","mimeType":"application/vnd.ekstep.content-collection","graph_id":"domain","nodeType":"DATA_NODE","createdOn":"2018-06-15T13:06:56.090+0000","versionKey":"1529068016090","objectType":"Content","dialcodes":["TNPF7T"],"collections":["do_312526125186424832139255"],"name":"Anction Words","lastUpdatedOn":"2018-06-15T13:06:56.090+0000","lastsubmittedon":1571999041881,"lastPublishedOn":"2018-06-15T13:06:56.090+0000","contentType":"TextBookUnit","status":"Draft","node_id":438622.0,"license":"CC BY 4.0"}"""
  val contentCacheData3 = """{"code":"do_312526125187809280139355","channel":"in.ekstep","downloadUrl":"https://ekstep-public-prod.s3-ap-south-1.amazonaws.com/content/14681653089315c3173a3e1.mp3","language":["English"],"mimeType":"application/octet-stream","idealScreenSize":"normal","createdOn":"2016-07-10T15:41:49.111+0000","objectType":"Content","gradeLevel":["Class 1"],"contentDisposition":"inline","contentEncoding":"identity","lastUpdatedOn":"2017-03-10T18:10:00.448+0000","contentType":"Asset","identifier":"do_312526125187809280139355","os":["All"],"visibility":"Default","mediaType":"audio","osId":"org.ekstep.launcher","ageGroup":["5-6"],"graph_id":"domain","nodeType":"DATA_NODE","pkgVersion":1.0,"versionKey":"1489169400448","license":"CC BY 4.0","idealScreenDensity":"hdpi","framework":"NCF","compatibilityLevel":1.0,"name":"do_312526125187809280139355","status":"Live","node_id":65545.0,"size":677510.0}"""
  
  val dialcodeCacheData1 = """{"identifier":"GWNI38","batchcode":"jkpublisher.20180801T134105","channel":"01254592085869363222","dialcode_index":2328993,"generated_on":"2018-08-01T13:41:53.695","published_on":1571999041881,"publisher":"jkpublisher","status":"Draft"}"""
  val dialcodeCacheData2 = """{"identifier":"PCZKA3","batchcode":"jkpublisher.20180801T122031","channel":"01254592085869363222","dialcode_index":1623464,"generated_on":1571999041881,"published_on":"2018-08-01T13:41:53.695Z","publisher":"jkpublisher","status":"Draft"}"""
  

  val currentDate: Long = DateTime.now().getMillis
  val olderDate: Long = DateTime.now().minusMonths(5).getMillis
  val futureDate: Long = DateTime.now().plusMonths(1).getMillis
  
  val telemetrEvents: List[String] = List(
      
    // user_denorm = true, device_denorm=false
    s"""{"actor":{"type":"User","id":"b7470841-7451-43db-b5c7-2dcf4f8d3b23"},"eid":"INTERACT",
      |"edata":{"type":"OTHER","subtype":"sheen-animation-ended","id":"library","pageid":"library","extra":{"pos":[]}},
      |"ver":"3.0","syncts":1.579564974098E12,"@timestamp":"2020-01-21T00:02:54.098Z","ets":$currentDate,
      |"context":{"cdata":[],"env":"home","channel":"505c7c48ac6dc1edc9b08f21db5a571d",
      |"pdata":{"id":"sunbird.app","pid":"sunbird.app","ver":"2.3.144"},"sid":"df936f82-e982-41ec-8412-70d414458272",
      |"did":"758e054a400f20f7677f2def76427dc13ad1f837"},"flags":{"dd_processed":true},
      |"mid":"mid1","type":"events","object":{"id":"","type":"",
      |"version":"","rollup":{}}}""".stripMargin,
    // user_denorm = true, device_denorm=true, content_denorm=true
    s"""{"actor":{"type":"User","id":"610bab7d-1450-4e54-bf78-c7c9b14dbc81"},"eid":"INTERACT",
      |"edata":{"type":"OTHER","subtype":"sheen-animation-ended","id":"library","pageid":"library","extra":{"pos":[]}},
      |"ver":"3.0","syncts":1.579564974098E12,"@timestamp":"2020-01-21T00:02:54.098Z","ets":$currentDate,
      |"context":{"cdata":[],"env":"home","channel":"505c7c48ac6dc1edc9b08f21db5a571d",
      |"pdata":{"id":"sunbird.app","pid":"sunbird.app","ver":"2.3.144"},"sid":"df936f82-e982-41ec-8412-70d414458272",
      |"did":"264d679186d4b0734d858d4e18d4d31e"},"flags":{"dd_processed":true},
      |"mid":"mid2","type":"events","object":{"id":"do_31249064359802470412856","type":"Content",
      |"version":"","rollup":{}}}""".stripMargin,
    // user_denorm = false, device_denorm=true, content_denorm=true, collection_denorm=true
    s"""{"actor":{"type":"User","id":"b7470841-7451-43db-b5c7-2dcf4f8d3b24"},"eid":"INTERACT",
      |"edata":{"type":"OTHER","subtype":"sheen-animation-ended","id":"library","pageid":"library","extra":{"pos":[]}},
      |"ver":"3.0","syncts":1.579564974098E12,"@timestamp":"2020-01-21T00:02:54.098Z","ets":$currentDate,
      |"context":{"cdata":[],"env":"home","channel":"505c7c48ac6dc1edc9b08f21db5a571d",
      |"pdata":{"id":"sunbird.app","pid":"sunbird.app","ver":"2.3.144"},"sid":"df936f82-e982-41ec-8412-70d414458272",
      |"did":"45f32f48592cb9bcf26bef9178b7bd20abe24932"},"flags":{"dd_processed":true},
      |"mid":"mid3","type":"events","object":{"id":"do_312526125187809280139353","type":"Content",
      |"version":"","rollup":{"l1":"do_312526125187809280139355"}}}""".stripMargin,
    // user_denorm = true, dialcode_denorm=true
    s"""{"actor":{"type":"User","id":"610bab7d-1450-4e54-bf78-c7c9b14dbc81"},"eid":"INTERACT",
      |"edata":{"type":"OTHER","subtype":"sheen-animation-ended","id":"library","pageid":"library","extra":{"pos":[]}},
      |"ver":"3.0","syncts":1.579564974098E12,"@timestamp":"2020-01-21T00:02:54.098Z","ets":$currentDate,
      |"context":{"cdata":[],"env":"home","channel":"505c7c48ac6dc1edc9b08f21db5a571d",
      |"pdata":{"id":"sunbird.app","pid":"sunbird.app","ver":"2.3.144"},"sid":"df936f82-e982-41ec-8412-70d414458272",
      |"did":"45f32f48592cb9bcf26bef9178b7bd20abe24932"},"flags":{"dd_processed":true},
      |"mid":"mid4","type":"events","object":{"id":"GWNI38","type":"DialCode",
      |"version":"","rollup":{}}}""".stripMargin,
    // user_denorm = false, dialcode_denorm=true
    s"""{"actor":{"type":"User","id":"610bab7d-1450-4e54-bf78-c7c9b14dbc82"},"eid":"INTERACT",
      |"edata":{"type":"OTHER","subtype":"sheen-animation-ended","id":"library","pageid":"library","extra":{"pos":[]}},
      |"ver":"3.0","syncts":1.579564974098E12,"@timestamp":"2020-01-21T00:02:54.098Z","ets":$currentDate,
      |"context":{"cdata":[],"env":"home","channel":"505c7c48ac6dc1edc9b08f21db5a571d",
      |"pdata":{"id":"sunbird.app","pid":"sunbird.app","ver":"2.3.144"},"sid":"df936f82-e982-41ec-8412-70d414458272",
      |"did":"45f32f48592cb9bcf26bef9178b7bd20abe24932"},"flags":{"dd_processed":true},
      |"mid":"mid5","type":"events","object":{"id":"PCZKA3","type":"qr",
      |"version":"","rollup":{}}}""".stripMargin,
      // user_denorm = false, dialcode_denorm=true
    s"""{"actor":{"type":"User","id":"610bab7d-1450-4e54-bf78-c7c9b14dbc82"},"eid":"INTERACT",
      |"edata":{"type":"OTHER","subtype":"sheen-animation-ended","id":"library","pageid":"library","extra":{"pos":[]}},
      |"ver":"3.0","syncts":1.579564974098E12,"@timestamp":"2020-01-21T00:02:54.098Z","ets":$currentDate,
      |"context":{"cdata":[],"env":"home","channel":"505c7c48ac6dc1edc9b08f21db5a571d",
      |"pdata":{"id":"sunbird.app","pid":"sunbird.app","ver":"2.3.144"},"sid":"df936f82-e982-41ec-8412-70d414458272",
      |"did":"45f32f48592cb9bcf26bef9178b7bd20abe24932"},"flags":{"dd_processed":true},
      |"mid":"mid6","type":"events","object":{"id":"PCZKA4","type":"qr",
      |"version":"","rollup":{}}}""".stripMargin,
      // user_denorm = false, device_denorm=true, content_denorm=true, collection_denorm=false
    s"""{"actor":{"type":"User","id":"anonymous"},"eid":"INTERACT",
      |"edata":{"type":"OTHER","subtype":"sheen-animation-ended","id":"library","pageid":"library","extra":{"pos":[]}},
      |"ver":"3.0","syncts":1.579564974098E12,"@timestamp":"2020-01-21T00:02:54.098Z","ets":$currentDate,
      |"context":{"cdata":[],"env":"home","channel":"505c7c48ac6dc1edc9b08f21db5a571d",
      |"pdata":{"id":"sunbird.app","pid":"sunbird.app","ver":"2.3.144"},"sid":"df936f82-e982-41ec-8412-70d414458272",
      |"did":"45f32f48592cb9bcf26bef9178b7bd20abe24932"},"flags":{"dd_processed":true},
      |"mid":"mid7","type":"events","object":{"id":"do_312526125187809280139353","type":"Content",
      |"version":"","rollup":{"l1":"do_312526125187809280139353"}}}""".stripMargin,
      // user_denorm = false, device_denorm=false, content_denorm=false, collection_denorm=true
    s"""{"actor":{"type":"User","id":"anonymous"},"eid":"INTERACT",
      |"edata":{"type":"OTHER","subtype":"sheen-animation-ended","id":"library","pageid":"library","extra":{"pos":[]}},
      |"ver":"3.0","syncts":1.579564974098E12,"@timestamp":"2020-01-21T00:02:54.098Z","ets":$currentDate,
      |"context":{"cdata":[],"env":"home","channel":"505c7c48ac6dc1edc9b08f21db5a571d",
      |"pdata":{"id":"sunbird.app","pid":"sunbird.app","ver":"2.3.144"},"sid":"df936f82-e982-41ec-8412-70d414458272",
      |"did":""},"flags":{"dd_processed":true},
      |"mid":"mid8","type":"events","object":{"id":"do_312526125187809280139352","type":"Content",
      |"version":"","rollup":{"l1":"do_312526125187809280139353"}}}""".stripMargin,
      
      // Future date
      s"""{"actor":{"type":"System","id":"anonymous"},"eid":"INTERACT",
      |"edata":{"type":"OTHER","subtype":"sheen-animation-ended","id":"library","pageid":"library","extra":{"pos":[]}},
      |"ver":"3.0","syncts":1.579564974098E12,"@timestamp":"2020-01-21T00:02:54.098Z","ets":$futureDate,
      |"context":{"cdata":[],"env":"home","channel":"505c7c48ac6dc1edc9b08f21db5a571d",
      |"pdata":{"id":"sunbird.app","pid":"sunbird.app","ver":"2.3.144"},"sid":"df936f82-e982-41ec-8412-70d414458272",
      |"did":"45f32f48592cb9bcf26bef9178b7bd20abe24932"},"flags":{"dd_processed":true},
      |"mid":"mid9","type":"events","object":{"id":"do_312526125187809280139352","type":"Content",
      |"version":"","rollup":{}}}""".stripMargin,
      
      // Date older than 3 months
      s"""{"actor":{"type":"User","id":"anonymous"},"eid":"INTERACT",
      |"edata":{"type":"OTHER","subtype":"sheen-animation-ended","id":"library","pageid":"library","extra":{"pos":[]}},
      |"ver":"3.0","syncts":1.579564974098E12,"@timestamp":"2020-01-21T00:02:54.098Z","ets":$olderDate,
      |"context":{"cdata":[],"env":"home","channel":"505c7c48ac6dc1edc9b08f21db5a571d",
      |"pdata":{"id":"sunbird.app","pid":"sunbird.app","ver":"2.3.144"},"sid":"df936f82-e982-41ec-8412-70d414458272",
      |"did":"45f32f48592cb9bcf26bef9178b7bd20abe24932"},"flags":{"dd_processed":true},
      |"mid":"mid10","type":"events","object":{"id":"do_312526125187809280139352","type":"Content",
      |"version":"","rollup":{"l1":"do_312526125187809280139353"}}}""".stripMargin,
      
      // Test data for 100% coverage
      s"""{"eid":"AUDIT","ets":$currentDate,"ver":"3.0","mid":"AUDIT:eae9854a08764ce0c5c17f7ad8abe3e6",
      |"actor":{"id":"264d679186d4b0734d858d4e18d4d31e","type":"User"},
      |"context":{"channel":"505c7c48ac6dc1edc9b08f21db5a571d","pdata":{"id":"prod.diksha.desktop","ver":"1.0.3","pid":"desktop.app"},
      |"env":"downloadManager","sid":"317574c5-41e3-48cb-ba3a-00b6dd7577ea","did":"6e1d6dc06874a8cc60a51e01f97bc7a8fc65978055ed1bdcd1d33e07a85530bd",
      |"cdata":[],"rollup":{"l1":"505c7c48ac6dc1edc9b08f21db5a571d"}},"object":{"id":"do_31249064359802470412856","type":"content","ver":"","rollup":{}},
      |"tags":["505c7c48ac6dc1edc9b08f21db5a571d"],"edata":{"state":"COMPLETED","prevstate":"INPROGRESS",
      |"props":["stats.downloadedSize","status","updatedOn"],"duration":0.8},"syncts":1.58156611767E12,"@timestamp":"2020-02-13T03:55:17.670Z",
      |"contentdata":{"objectType":"Content"},"userdata":{"firstname":"A512"},"type":"events"}""".stripMargin
            
  )
  
  val summaryEvents: List[String] = List(
    s"""{"actor":{"type":"User","id":"b7470841-7451-43db-b5c7-2dcf4f8d3b23"},"eid":"INTERACT",
      |"edata":{"type":"OTHER","subtype":"sheen-animation-ended","id":"library","pageid":"library","extra":{"pos":[]}},
      |"ver":"3.0","syncts":1.579564974098E12,"@timestamp":"2020-01-21T00:02:54.098Z","ets":$futureDate,
      |"context":{"cdata":[],"env":"home","channel":"505c7c48ac6dc1edc9b08f21db5a571d",
      |"pdata":{"id":"sunbird.app","pid":"sunbird.app","ver":"2.3.144"},"sid":"df936f82-e982-41ec-8412-70d414458272"
      |},"flags":{"dd_processed":true},
      |"mid":"mid11","type":"events","object":{"id":"","type":"",
      |"version":"","rollup":{}}}""".stripMargin
  )

}
