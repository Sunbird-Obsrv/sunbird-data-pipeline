package org.sunbird.dp.fixture

import java.util

import com.google.gson.Gson
import org.joda.time.DateTime

object EventFixture {

  val gson = new Gson()
  val deviceCacheData1 = """{"country":"India","state_custom":"Karnataka","devicespec":"{}","city":"Bengaluru","uaspec":"{}","district_custom":"BENGALURU URBAN SOUTH","country_code":"IN","firstaccess":"1571999041881","state_code_custom":"29"}"""
  val deviceCacheData2 = """{"user_declared_state":"Maharashtra","state_custom":"Maharashtra","devicespec":"{\"scrn\":\"5.46\",\"camera\":\"\",\"idisk\":\"25.44\",\"os\":\"Android 9\",\"id\":\"45f32f48592cb9bcf26bef9178b7bd20abe24932\",\"sims\":\"-1\",\"cpu\":\"abi: armeabi-v7a processor\t: 0 \",\"webview\":\"79.0.3945.116\",\"edisk\":\"25.42\",\"make\":\"Samsung SM-J400F\"}","uaspec":"{\"agent\":\"UNKNOWN\",\"ver\":\"UNKNOWN\",\"system\":\"Android\",\"raw\":\"Dalvik/2.1.0 (Linux U Android 9 SM-J400F Build/PPR1.180610.011)\"}","city":"Mumbai","country_code":"IN","firstaccess":"1578972432419","country":"India","country_name":"India","state":"Maharashtra","continent_name":"Asia","state_code":"MH","fcm_token":"d3ddT88xXLI:APA91bF9lJ4eH8tshAPgKiiZ3hL3sbib0pUN2I388T58oFDxUBQ2WKKuvtBga6iKiOPrgssNKLs4QBjZxE_BbtdGdO0gPdFPataEeXshgYxMKC0VT-oyjrNIZKdKkybQoyichBCiokTD","producer":"sunbirddev.diksha.app","district_custom":"Mumbai","user_declared_district":"Raigad","state_code_custom":"27"}"""
  // device data without user declared location fields
  val deviceCacheData3 = """{"state_custom":"Maharashtra","devicespec":"{\"scrn\":\"5.46\",\"camera\":\"\",\"idisk\":\"25.44\",\"os\":\"Android 9\",\"id\":\"45f32f48592cb9bcf26bef9178b7bd20abe24932\",\"sims\":\"-1\",\"cpu\":\"abi: armeabi-v7a processor\t: 0 \",\"webview\":\"79.0.3945.116\",\"edisk\":\"25.42\",\"make\":\"Samsung SM-J400F\"}","uaspec":"{\"agent\":\"UNKNOWN\",\"ver\":\"UNKNOWN\",\"system\":\"Android\",\"raw\":\"Dalvik/2.1.0 (Linux U Android 9 SM-J400F Build/PPR1.180610.011)\"}","city":"Mumbai","country_code":"IN","firstaccess":"1578972432419","country":"India","country_name":"India","state":"Maharashtra","continent_name":"Asia","state_code":"MH","fcm_token":"d3ddT88xXLI:APA91bF9lJ4eH8tshAPgKiiZ3hL3sbib0pUN2I388T58oFDxUBQ2WKKuvtBga6iKiOPrgssNKLs4QBjZxE_BbtdGdO0gPdFPataEeXshgYxMKC0VT-oyjrNIZKdKkybQoyichBCiokTD","producer":"sunbirddev.diksha.app","district_custom":"Mumbai","state_code_custom":"27"}"""
  
  val userCacheData1 = """{"usersignintype":"Anonymous","usertype":"TEACHER"}"""
  val userCacheData2 = """{"channel":"KV123","phoneverified":false,"createdby":"c8e51123-61a3-454d-beb0-2202450b0096","subject":["English"],"email":"BJAguqy3GaJECrYqDUPjeducVxa5J9ZsW9A8qc7YHelkV7KbgkCKW10quCbhpgxbh2t4toXC8uXW\\ngiguS+8ucwzbmgPm7q7YSYz26SfpHnzBo/0Vh3TWqr2MOq9LlX6gT6a+wzaAmCWueMEdPmZuRg==","username":"I+CyiN6Bx0GCRm9lkA3xn5uNBm0AODhxeDwJebxxBfuGJ5V2v1R8v1PEQsP+V+y9sAFcM2WtaMLj\\n91hpzBq0PFcQTq6OSPQOm0sySPXTDzyLvm1cKaLwzvJ6fzLLs9nKT6a+wzaAmCWueMEdPmZuRg==","firstname":"A512","framework":{},"userid":"610bab7d-1450-4e54-bf78-c7c9b14dbc81","usertype":"TEACHER","rootorgid":"0126978705345576967","id":"610bab7d-1450-4e54-bf78-c7c9b14dbc81","language":[],"grade":[],"roles":["BOOK_REVIEWER"],"status":1,"webpages":[],"createddate":"2019-04-11 08:58:16:512+0000","emailverified":true,"isdeleted":false,"locationids":[],"maskedemail":"a5**@yopmail.com","profilevisibility":{},"loginid":"I+CyiN6Bx0GCRm9lkA3xnx2W8+QgN39Y0We3KjR98O8hD6YjyoCirIBDsWHGwRf65PY/Cx+pFFK1\\nIz1VinIaKgDnSQwkl7ajzQjjRTzQbKOyHsAXkJgo9I5l7ulEYVXRT6a+wzaAmCWueMEdPmZuRg==","usersignintype":"Self-Signed-In","userlogintype":"Student","state":"Telangana","district":"Hyderabad"}"""
  val userCacheData3 = """{"channel":"KV123","phoneverified":"false","createdby":"c8e51123-61a3-454d-beb0-2202450b0096","subject":"[\"English\"]","email":"BJAguqy3GaJECrYqDUPjeducVxa5J9ZsW9A8qc7YHelkV7KbgkCKW10quCbhpgxbh2t4toXC8uXW\\ngiguS+8ucwzbmgPm7q7YSYz26SfpHnzBo/0Vh3TWqr2MOq9LlX6gT6a+wzaAmCWueMEdPmZuRg==","username":"I+CyiN6Bx0GCRm9lkA3xn5uNBm0AODhxeDwJebxxBfuGJ5V2v1R8v1PEQsP+V+y9sAFcM2WtaMLj\\n91hpzBq0PFcQTq6OSPQOm0sySPXTDzyLvm1cKaLwzvJ6fzLLs9nKT6a+wzaAmCWueMEdPmZuRg==","firstname":"A512","framework":"{}","userid":"610bab7d-1450-4e54-bf78-c7c9b14dbc81","usertype":"administrator", "usersubtype":"deo,hm","rootorgid":"0126978705345576967","id":"610bab7d-1450-4e54-bf78-c7c9b14dbc81","language":"","grade":"","roles":"[\"BOOK_REVIEWER\"]","status":"1","webpages":"[]","createddate":"2019-04-11 08:58:16:512+0000","emailverified":"true","isdeleted":"false","locationids":"[\"location-1\",\"location-2\",\"location-3\"]","maskedemail":"a5**@yopmail.com","profilevisibility":"{}","loginid":"I+CyiN6Bx0GCRm9lkA3xnx2W8+QgN39Y0We3KjR98O8hD6YjyoCirIBDsWHGwRf65PY/Cx+pFFK1\\nIz1VinIaKgDnSQwkl7ajzQjjRTzQbKOyHsAXkJgo9I5l7ulEYVXRT6a+wzaAmCWueMEdPmZuRg==","usersignintype":"Self-Signed-In","userlogintype":"Student","state":"Telangana","district":"Hyderabad","cluster":"Cluster001","block":"Sri Sai ACC Block","schoolname":"\\[RPMMAT M.S UDHADIH"}"""
  val userCacheDataMap1: util.HashMap[String, String] = gson.fromJson(userCacheData1, new util.HashMap[String, String]().getClass)
  val userCacheDataMap2: util.HashMap[String, String] = gson.fromJson(userCacheData3, new util.HashMap[String, String]().getClass)

  val contentCacheData1 = """{"code":"org.ekstep.literacy.story.21797","keywords":["Story"],"subject":["English"],"channel":"in.ekstep","description":"Write a short description of your lesson","language":["English"],"mimeType":"application/vnd.ekstep.ecml-archive","idealScreenSize":"normal","createdOn":"2018-04-26T09:59:27.336+0000","objectType":"Content","gradeLevel":["Class 9"],"appId":"ekstep_portal","contentDisposition":"inline","contentEncoding":"gzip","lastUpdatedOn":1571999041881,"lastSubmittedOn":"2018-06-15T13:06:56.090+0000","lastPublishedOn":1571999041881,"contentType":"Resource","lastUpdatedBy":"7224","identifier":"do_31249064359802470412856","audience":["Learner"],"creator":"Pravin Panpatil","os":["All"],"visibility":"Default","consumerId":"62e15662-bb09-439f-86e2-d65bd84f3c23","mediaType":"content","osId":"org.ekstep.quiz.app","graph_id":"domain","nodeType":"DATA_NODE","versionKey":"1524736767336","idealScreenDensity":"hdpi","framework":"NCF","createdBy":"7224","compatibilityLevel":1.0,"domain":["literacy"],"name":"Walk a little slower","board":"State (Maharashtra)","resourceType":"Read","status":"Draft","node_id":310297.0,"license":"CC BY 4.0","copyright":"Ekstep","author":"Ekstep","copyrightYear":2019.0}"""
  val contentCacheData2 = """{"identifier":"do_312526125187809280139353","code":"c361b157-408c-4347-9be3-38d9d4ea2b90","visibility":"Parent","description":"Anction Words","mimeType":"application/vnd.ekstep.content-collection","graph_id":"domain","nodeType":"DATA_NODE","createdOn":"2018-06-15T13:06:56.090+0000","versionKey":"1529068016090","objectType":"Content","dialcodes":["TNPF7T"],"collections":["do_312526125186424832139255"],"name":"Anction Words","lastUpdatedOn":"2018-06-15T13:06:56.090+0000","lastsubmittedon":1571999041881,"lastPublishedOn":"2018-06-15T13:06:56.090+0000","contentType":"TextBookUnit","status":"Draft","node_id":438622.0,"license":"CC BY 4.0"}"""
  val contentCacheData3 = """{"code":"do_312526125187809280139355","channel":"in.ekstep","downloadUrl":"https://ekstep-public-prod.s3-ap-south-1.amazonaws.com/content/14681653089315c3173a3e1.mp3","language":["English"],"mimeType":"application/octet-stream","idealScreenSize":"normal","createdOn":"2016-07-10T15:41:49.111+0000","objectType":"Content","gradeLevel":["Class 1"],"contentDisposition":"inline","contentEncoding":"identity","lastUpdatedOn":"2017-03-10T18:10:00.448+0000","contentType":"Asset","identifier":"do_312526125187809280139355","os":["All"],"visibility":"Default","mediaType":"audio","osId":"org.ekstep.launcher","ageGroup":["5-6"],"graph_id":"domain","nodeType":"DATA_NODE","pkgVersion":1.0,"versionKey":"1489169400448","license":"CC BY 4.0","idealScreenDensity":"hdpi","framework":"NCF","compatibilityLevel":1.0,"name":"do_312526125187809280139355","status":"Live","node_id":65545.0,"size":677510.0}"""
  val contentCacheData4 = """{"code":"org.sunbird.XiKRjo","channel":"0123221617357783046602","description":"à¤à¤£à¤¿à¤¤ à¤¤à¥à¤¸à¤°à¥ à¤à¤à¥à¤·à¤¾","language":["English"],"mimeType":"application/vnd.ekstep.content-collection","medium":["Hindi"],"idealScreenSize":"normal","createdOn":"2018-06-12T08:26:22.841+0000","objectType":"Content","gradeLevel":["Class 3"],"children":["do_312523863926120448117914","do_312523863926120448117918","do_312523863926120448117917","do_312523863926120448117916","do_312523863926120448117915","do_312523863926128640117922","do_312523863926128640117921","do_312523863926128640117920","do_312523863926128640117919","do_312523863926136832117926","do_312523863926136832117925","do_312523863926136832117924","do_312523863926128640117923","do_312523863926153216117930","do_312523863926145024117929","do_312523863926145024117928","do_312523863926145024117927","do_312523863926161408117934","do_312523863926161408117933","do_312523863926153216117932","do_312523863926153216117931","do_312523863926169600117938","do_312523863926161408117937","do_312523863926161408117936","do_312523863926161408117935","do_312523863926177792117942","do_312523863926169600117941","do_312523863926169600117940","do_312523863926169600117939","do_312523863926177792117946","do_312523863926177792117945","do_312523863926177792117944","do_312523863926177792117943","do_312523863926185984117950","do_312523863926185984117949","do_312523863926185984117948","do_312523863926185984117947","do_312523863926194176117954","do_312523863926194176117953","do_312523863926194176117952","do_312523863926185984117951","do_312523863926202368117958","do_312523863926202368117957","do_312523863926202368117956","do_312523863926194176117955","do_312523863926210560117962","do_312523863926210560117961","do_312523863926210560117960","do_312523863926202368117959","do_312523863926218752117966","do_312523863926218752117965","do_312523863926218752117964","do_312523863926210560117963","do_312523863926226944117970","do_312523863926226944117969","do_312523863926218752117968","do_312523863926218752117967","do_312523863926235136117974","do_312523863926226944117973","do_312523863926226944117972","do_312523863926226944117971"],"appId":"prod.diksha.portal","contentDisposition":"inline","contentEncoding":"gzip","lastUpdatedOn":"2018-06-12T08:31:38.614+0000","contentType":"TextBook","identifier":"do_312523863923441664117896","createdFor":["0123221617357783046602"],"audience":["Learner"],"os":["All"],"visibility":"Default","consumerId":"0aa13c48-dda0-4259-9007-c795dacd7b9c","mediaType":"content","osId":"org.ekstep.quiz.app","graph_id":"domain","nodeType":"DATA_NODE","versionKey":"1528792298614","idealScreenDensity":"hdpi","framework":"mh_k-12_1","createdBy":"80ae760d-f9dd-43a9-91a7-09dfabba2293","compatibilityLevel":1.0,"name":"test","board":"State (Maharashtra)","resourceType":"Book","status":"Retired","node_id":398184.0,"license":"CC BY 4.0","copyright":"MITRA","author":"MITRA","copyrightYear":2019.0}"""
  val contentCacheData5 = """{"identifier":"7426472e-8b1a-4387-8b7a-962cb6cda006","code":"c361b157-408c-4347-9be3-38d9d4ea2b90","visibility":"Parent","description":"Anction Words","mimeType":"application/vnd.ekstep.content-collection","graph_id":"domain","nodeType":"DATA_NODE","createdOn":"2018-06-15T13:06:56.090+0000","versionKey":"1529068016090","objectType":"Content","dialcodes":["TNPF7T"],"collections":["do_312526125186424832139255"],"name":"Anction Words","lastUpdatedOn":"2018-06-15T13:06:56.090+0000","lastsubmittedon":1571999041881,"lastPublishedOn":"2018-06-15T13:06:56.090+0000","contentType":"TextBookUnit","status":"Draft","node_id":438622.0,"license":"CC BY 4.0"}"""

  val collectionCache1 = """{"identifier":"do_31331086175718604812701","code":"c361b157-408c-4347-9be3-38d9d4ea2b90","visibility":"Parent","description":"Anction Words","mimeType":"application/vnd.ekstep.content-collection","graph_id":"domain","nodeType":"DATA_NODE","createdOn":"2018-06-15T13:06:56.090+0000","versionKey":"1529068016090","objectType":"Content","dialcodes":["TNPF7T"],"collections":["do_312526125186424832139255"],"name":"Anction Words","lastUpdatedOn":"2018-06-15T13:06:56.090+0000","lastsubmittedon":1571999041881,"lastPublishedOn":"2018-06-15T13:06:56.090+0000","contentType":"TextBookUnit","status":"Draft","node_id":438622.0,"license":"CC BY 4.0"}"""

  val dialcodeCacheData1 = """{"identifier":"GWNI38","batchcode":"jkpublisher.20180801T134105","channel":"01254592085869363222","dialcode_index":2328993,"generated_on":"2018-08-01T13:41:53.695","published_on":1571999041881,"publisher":"jkpublisher","status":"Draft"}"""
  val dialcodeCacheData2 = """{"identifier":"PCZKA3","batchcode":"jkpublisher.20180801T122031","channel":"01254592085869363222","dialcode_index":1623464,"generated_on":1571999041881,"published_on":"2018-08-01T13:41:53.695Z","publisher":"jkpublisher","status":"Draft"}"""
  

  val currentDate: Long = DateTime.now().getMillis
  val olderDate: Long = DateTime.now().minusMonths(5).getMillis
  val futureDate: Long = DateTime.now().plusMonths(1).getMillis

  val telemetryEvent: String = s"""{"actor":{"type":"User","id":"b7470841-7451-43db-b5c7-2dcf4f8d3b23"},"eid":"INTERACT",
                                  |"edata":{"type":"OTHER","subtype":"sheen-animation-ended","id":"library","pageid":"library","extra":{"pos":[]}},
                                  |"ver":"3.0","syncts":1.579564974098E12,"@timestamp":"2020-01-21T00:02:54.098Z","ets":$currentDate,
                                  |"context":{"cdata":[],"env":"home","channel":"505c7c48ac6dc1edc9b08f21db5a571d",
                                  |"pdata":{"id":"sunbird.app","pid":"sunbird.app","ver":"2.3.144"},"sid":"df936f82-e982-41ec-8412-70d414458272",
                                  |"did":"45f32f48592cb9bcf26bef9178b7bd20abe24932"},"flags":{"dd_processed":true},
                                  |"mid":"mid1","type":"events","object":{"id":"","type":"",
                                  |"version":"","rollup":{}}}""".stripMargin
  
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
      |"version":"","rollup":{"l1":"do_312526125187809280139353","l2":"do_312523863923441664117896"}}}""".stripMargin,
      
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
      |"contentdata":{"objectType":"Content"},"userdata":{"firstname":"A512"},"type":"events"}""".stripMargin,

      s"""{"actor":{"type":"User","id":"610bab7d-1450-4e54-bf78-c7c9b14dbc82"},"eid":"INTERRUPT",
       |"edata":{"type":"OTHER","subtype":"sheen-animation-ended","id":"library","pageid":"library","extra":{"pos":[]}},
       |"ver":"3.0","syncts":1.579564974098E12,"@timestamp":"2020-01-21T00:02:54.098Z","ets":$currentDate,
       |"context":{"cdata":[],"env":"home","channel":"505c7c48ac6dc1edc9b08f21db5a571d",
       |"pdata":{"id":"sunbird.app","pid":"sunbird.app","ver":"2.3.144"},"sid":"df936f82-e982-41ec-8412-70d414458272",
       |"did":"45f32f48592cb9bcf26bef9178b7bd20abe24932"},"flags":{"dd_processed":true},
       |"mid":"mid6","type":"events","object":{"id":"PCZKA4","type":"qr",
       |"version":"","rollup":{}}}""".stripMargin,

      // event without did - INTERRUPT skipped
      s"""{"actor":{"type":"User","id":"610bab7d-1450-4e54-bf78-c7c9b14dbc82"},"eid":"INTERRUPT",
       |"edata":{"type":"OTHER","subtype":"sheen-animation-ended","id":"library","pageid":"library","extra":{"pos":[]}},
       |"ver":"3.0","syncts":1.579564974098E12,"@timestamp":"2020-01-21T00:02:54.098Z","ets":$currentDate,
       |"context":{"cdata":[],"env":"home","channel":"505c7c48ac6dc1edc9b08f21db5a571d",
       |"pdata":{"id":"sunbird.app","pid":"sunbird.app","ver":"2.3.144"},"sid":"df936f82-e982-41ec-8412-70d414458272"},
       |"flags":{"dd_processed":true},
       |"mid":"mid11","type":"events","object":{"id":"PCZKA4","type":"qr",
       |"version":"","rollup":{}}}""".stripMargin,

      // event without did
      s"""{"actor":{"type":"User","id":"b7470841-7451-43db-b5c7-2dcf4f8d3b23"},"eid":"INTERACT",
       |"edata":{"type":"OTHER","subtype":"sheen-animation-ended","id":"library","pageid":"library","extra":{"pos":[]}},
       |"ver":"3.0","syncts":1.579564974098E12,"@timestamp":"2020-01-21T00:02:54.098Z","ets":$currentDate,
       |"context":{"cdata":[],"env":"home","channel":"505c7c48ac6dc1edc9b08f21db5a571d",
       |"pdata":{"id":"sunbird.app","pid":"sunbird.app","ver":"2.3.144"},"sid":"df936f82-e982-41ec-8412-70d414458272"},
       |"flags":{"dd_processed":true},
       |"mid":"mid12","type":"events","object":{"id":"","type":"",
       |"version":"","rollup":{}}}""".stripMargin,

      // SUMMARY event - user_denorm = true, device_denorm=true, content_denorm=true
      s"""{"eid":"SUMMARY","ets":$currentDate,"ver":"3.0","mid":"SUMMARY:a3e517153c4ba392297e70521aa5e17a",
         |"actor":{"id":"610bab7d-1450-4e54-bf78-c7c9b14dbc81","type":"User"},
         |"context":{"channel":"01268904781886259221","pdata":{"id":"staging.sunbird.portal","ver":"4.1.0",
         |"pid":"sunbird-portal"},"env":"contentplayer","sid":"73d82044-8ea5-dffc-1af5-6cdf2a1fa1da",
         |"did":"264d679186d4b0734d858d4e18d4d31e","cdata":[{"id":"kubXMwcsJK2JANa0PeYc00GK5CSXoS1q","type":"ContentSession"},
         |{"id":"xcFG0rntKUGltu2m8zJh7ZqattT9u3Ix","type":"PlaySession"},{"id":"2.0","type":"PlayerVersion"}],
         |"rollup":{"l1":"01268904781886259221"},"uid":"anonymous"},"object":{"id":"do_31249064359802470412856","ver":"1","type":"Content","rollup":{}},
         |"tags":["01268904781886259221"],"edata":{"type":"content","mode":"play","starttime":1625043385301,
         |"endtime":1625043401236,"timespent":16,"pageviews":2,"interactions":2,"extra":[{"id":"progress","value":"100"},
         |{"id":"endpageseen","value":"true"},{"id":"score","value":"2"},{"id":"correct","value":"2"},
         |{"id":"incorrect","value":"0"},{"id":"partial","value":"0"},{"id":"skipped","value":"0"}]}}""".stripMargin,

      // ME_DEVICE_SUMMARY event - should skip
      s"""
         |{"eid":"ME_DEVICE_SUMMARY","ets":$currentDate,"syncts":$currentDate,"ver":"1.0","mid":
         |"CFBA22543AA3EAD4C2737931D34F2E8D","context":{"pdata":{"id":"AnalyticsDataPipeline","ver":"1.0",
         |"model":"DeviceSummary"},"granularity":"DAY","date_range":{"from":1572786370125,"to":1572786403121}},
         |"dimensions":{"did":"3eb8d5dc49b063650ca18920956ea04e","channel":"ROOT_ORG"},"edata":{"eks":{"firstAccess":
         |1572786370121,"dial_stats":{"total_count":3,"success_count":3,"failure_count":0},"content_downloads":0,
         |"contents_played":0,"total_ts":0.0,"total_launches":0,"unique_contents_played":0}}}
         """.stripMargin,
      //SB-25755: if enrol-complete , denorm the event
      s"""
          |{"actor":{"id":"7426472e-8b1a-4387-8b7a-962cb6cda006","type":"User"},"eid":"AUDIT","edata":{"props":["status","completedon"],
          |"type":"enrol-complete"},"ver":"3.0","syncts":1626346241621,"ets":$currentDate,"context":{"channel":"in.sunbird",
          |"env":"Course","sid":"2ba8a10a-9722-42c6-a27f-f9fd86ff6bb5","did":"0a65cbaf-2d0e-4cc8-bc70-6ee1e71fb605","pdata":
          |{"ver":"3.0","id":"org.sunbird.learning.platform","pid":"course-progress-updater"},"cdata":[{"type":"CourseBatch","id":"01331092647782809655"},
          |{"type":"Course","id":"do_31331086175718604812701"}]},"mid":"LP.AUDIT.d14d8be6-da4e-4ee9-b833-fd86d57b8808",
          |"object":{"id":"7426472e-8b1a-4387-8b7a-962cb6cda006","type":"User","rollup":{"l1":"do_31331086175718604812701"}},"tags":[]}
        """.stripMargin
  )

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
     """.stripMargin,
    s"""
       |{"eid":"ME_DEVICE_SUMMARY","ets":$currentDate,"syncts":$currentDate,"ver":"1.0","mid":
       |"CFBA22543AA3EAD4C2737931D34F2E8D","context":{"pdata":{"id":"AnalyticsDataPipeline","ver":"1.0",
       |"model":"DeviceSummary"},"granularity":"DAY","date_range":{"from":1572786370125,"to":1572786403121}},
       |"dimensions":{"did":"3eb8d5dc49b063650ca18920956ea04e","channel":"ROOT_ORG"},"edata":{"eks":{"firstAccess":
       |1572786370121,"dial_stats":{"total_count":3,"success_count":3,"failure_count":0},"content_downloads":0,
       |"contents_played":0,"total_ts":0.0,"total_launches":0,"unique_contents_played":0}}}
     """.stripMargin
  )

}
