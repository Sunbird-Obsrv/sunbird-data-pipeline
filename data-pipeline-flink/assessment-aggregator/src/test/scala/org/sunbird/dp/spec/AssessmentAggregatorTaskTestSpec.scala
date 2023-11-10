package org.sunbird.dp.spec

import com.datastax.driver.core.Row

import java.util
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext
import org.apache.flink.test.util.MiniClusterWithClientResource
import org.cassandraunit.CQLDataLoader
import org.cassandraunit.dataset.cql.FileCQLDataSet
import org.cassandraunit.utils.EmbeddedCassandraServerHelper
import org.mockito.Mockito
import org.mockito.Mockito._
import org.sunbird.dp.assessment.domain.Event
import org.sunbird.dp.assessment.functions.{AggDetails, UserActivityAgg}
import org.sunbird.dp.assessment.task.{AssessmentAggregatorConfig, AssessmentAggregatorStreamTask}
import org.sunbird.dp.core.cache.RedisConnect
import org.sunbird.dp.core.job.FlinkKafkaConnector
import org.sunbird.dp.core.util.{CassandraUtil, JSONUtil}
import org.sunbird.dp.fixture.EventFixture
import org.sunbird.dp.{BaseMetricsReporter, BaseTestSpec}
import redis.embedded.RedisServer
import okhttp3.mockwebserver.{MockResponse, MockWebServer}
import java.io.IOException


class AssessmentAggregatorTaskTestSpec extends BaseTestSpec {

  implicit val mapTypeInfo: TypeInformation[util.Map[String, AnyRef]] = TypeExtractor.getForClass(classOf[util.Map[String, AnyRef]])

  val flinkCluster = new MiniClusterWithClientResource(new MiniClusterResourceConfiguration.Builder()
    .setConfiguration(testConfiguration())
    .setNumberSlotsPerTaskManager(1)
    .setNumberTaskManagers(1)
    .build)
  var redisServer: RedisServer = _

  val config: Config = ConfigFactory.load("test.conf")
  val assessmentConfig: AssessmentAggregatorConfig = new AssessmentAggregatorConfig(config)
  val mockKafkaUtil: FlinkKafkaConnector = mock[FlinkKafkaConnector](Mockito.withSettings().serializable())
  val gson = new Gson()
  val server = new MockWebServer()


  var cassandraUtil: CassandraUtil = _


  override protected def beforeAll(): Unit = {
    super.beforeAll()
    redisServer = new RedisServer(6340)
    redisServer.start()
    EmbeddedCassandraServerHelper.startEmbeddedCassandra(80000L)
    cassandraUtil = new CassandraUtil(assessmentConfig.dbHost, assessmentConfig.dbPort)
    val session = cassandraUtil.session
    setupRestUtilData()
    setupRedisTestData()

    val dataLoader = new CQLDataLoader(session)
    dataLoader.load(new FileCQLDataSet(getClass.getResource("/test.cql").getPath, true, true));
    // Clear the metrics
    testCassandraUtil(cassandraUtil)
    BaseMetricsReporter.gaugeMetrics.clear()

    flinkCluster.before()
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    redisServer.stop()
    try {
      EmbeddedCassandraServerHelper.cleanEmbeddedCassandra()
      server.close()
    } catch {
      case ex: Exception => {

      }
    }
    flinkCluster.after()
  }

  def setupRestUtilData(): Unit = {
    val do_11307972307046400011917_response = """{"id":"api.content.read","ver":"1.0","ts":"2023-05-17T10:26:51.549Z","params":{"resmsgid":"566eacd0-f49d-11ed-bf1e-7fae1bdbcdf8","msgid":"566de980-f49d-11ed-8721-d532b5857c8a","status":"successful","err":null,"errmsg":null},"responseCode":"OK","result":{"content":{"ownershipType":["createdBy"],"copyright":"Sunbird Org","previewUrl":"https://sunbirddev.blob.core.windows.net/sunbird-content-dev/content/assets/do_113762457691021312168/samplevideo_1280x720_1mb.mp4","channel":"0137541424673095687","downloadUrl":"https://sunbirddev.blob.core.windows.net/sunbird-content-dev/content/do_113762457691021312168/content-2_1679987660391_do_113762457691021312168_1.ecar","organisation":["Sunbird Org"],"language":["English"],"mimeType":"video/mp4","variants":{"full":{"ecarUrl":"https://sunbirddev.blob.core.windows.net/sunbird-content-dev/content/do_113762457691021312168/content-2_1679987660391_do_113762457691021312168_1.ecar","size":"1058720"},"spine":{"ecarUrl":"https://sunbirddev.blob.core.windows.net/sunbird-content-dev/content/do_113762457691021312168/content-2_1679987660892_do_113762457691021312168_1_SPINE.ecar","size":"4153"}},"objectType":"Content","appIcon":"https://sunbirddev.blob.core.windows.net/sunbird-content-dev/content/do_113762457691021312168/artifact/do_11376182453272576019_1679910221428_287-2876925_test-image-png-unit-testing-png-transparent-png.thumb.png","primaryCategory":"Explanation Content","contentEncoding":"identity","artifactUrl":"https://sunbirddev.blob.core.windows.net/sunbird-content-dev/content/assets/do_113762457691021312168/samplevideo_1280x720_1mb.mp4","lockKey":"1a558750-41dd-43d3-9d4f-9f12184a902e","contentType":"Resource","category2":"Category2 Term1","identifier":"do_113762457691021312168","lastUpdatedBy":"155ce3c5-713e-4749-bc1c-95d09c640914","category3":"Category3 Term1","audience":["Student"],"category4":"Category4 Term1","category5":"Category5 Term1","visibility":"Default","category1":"Category1 Term1","discussionForum":{"enabled":"No"},"mediaType":"content","osId":"org.ekstep.quiz.app","languageCode":["en"],"lastPublishedBy":"469dc732-04f3-42d9-9a85-30957a797acc","version":2,"license":"CC BY 4.0","prevState":"Review","size":1055736,"lastPublishedOn":"2023-03-28T07:14:20.009+0000","name":"Content - 2","status":"Live","code":"62ada120-13c4-4e94-aad6-56cebe6a089c","interceptionPoints":{},"credentials":{"enabled":"No"},"prevStatus":"Processing","streamingUrl":"https://sunbirdspikemedia-inct.streaming.media.azure.net/5d2643e3-fcae-42a8-8a22-ac291a317ed4/samplevideo_1280x720_1mb.ism/manifest(format=m3u8-aapl-v3)","posterImage":"https://sunbirddev.blob.core.windows.net/sunbird-content-dev/content/do_11376182453272576019/artifact/do_11376182453272576019_1679910221428_287-2876925_test-image-png-unit-testing-png-transparent-png.png","idealScreenSize":"normal","createdOn":"2023-03-28T07:11:51.115+0000","contentDisposition":"inline","lastUpdatedOn":"2023-03-28T07:44:16.190+0000","dialcodeRequired":"No","lastStatusChangedOn":"2023-03-28T07:14:21.098+0000","createdFor":["0137541424673095687"],"creator":"contentCreator Creator","os":["All"],"se_FWIds":["NCF"],"pkgVersion":1,"versionKey":"1679989456190","idealScreenDensity":"hdpi","framework":"framework1","lastSubmittedOn":"2023-03-28T07:12:10.623+0000","createdBy":"155ce3c5-713e-4749-bc1c-95d09c640914","compatibilityLevel":1,"resourceType":"Learn"}}}"""
    val do_1131998128479272961991_response = """{"id":"api.content.read","ver":"1.0","ts":"2023-05-17T11:08:52.532Z","params":{"resmsgid":"350e2740-f4a3-11ed-bf1e-7fae1bdbcdf8","msgid":"350d15d0-f4a3-11ed-8721-d532b5857c8a","status":"successful","err":null,"errmsg":null},"responseCode":"OK","result":{"content":{"ownershipType":["createdBy"],"copyright":"sunbird","se_gradeLevelIds":["ncf_gradelevel_class1"],"subject":["Telugu"],"channel":"0137541424673095687","downloadUrl":"https://sunbirddev.blob.core.windows.net/sunbird-content-dev/content/do_11376180991508480011/test-book_1679910340314_do_11376180991508480011_1_SPINE.ecar","organisation":["Sunbird Org"],"language":["English"],"mimeType":"application/vnd.ekstep.content-collection","variants":{"spine":{"ecarUrl":"https://sunbirddev.blob.core.windows.net/sunbird-content-dev/content/do_11376180991508480011/test-book_1679910340314_do_11376180991508480011_1_SPINE.ecar","size":"8922"},"online":{"ecarUrl":"https://sunbirddev.blob.core.windows.net/sunbird-content-dev/content/do_11376180991508480011/test-book_1679910340572_do_11376180991508480011_1_ONLINE.ecar","size":"5179"}},"leafNodes":["do_11376182093890355216","do_11376182438513868818"],"objectType":"Content","se_mediums":["Telugu"],"gradeLevel":["Class 1"],"appIcon":"","primaryCategory":"Digital Textbook","contentEncoding":"gzip","lockKey":"fbeda787-5742-4c45-a535-fbb2c99ac3a0","generateDIALCodes":"Yes","totalCompressedSize":3579342,"mimeTypesCount":"{\"video/mp4\":2,\"application/vnd.ekstep.content-collection\":2}","sYS_INTERNAL_LAST_UPDATED_ON":"2023-03-27T09:45:40.314+0000","contentType":"TextBook","se_gradeLevels":["Class 1"],"trackable":{"enabled":"No","autoBatch":"No"},"identifier":"do_11376180991508480011","audience":["Student"],"se_boardIds":["ncf_board_other"],"subjectIds":["ncf_subject_telugu"],"toc_url":"https://sunbirddev.blob.core.windows.net/sunbird-content-dev/content/do_11376180991508480011/artifact/do_11376180991508480011_toc.json","visibility":"Default","contentTypesCount":"{\"TextBookUnit\":2,\"Resource\":2}","author":"BookCreator bookCreator","consumerId":"bfe5883f-ac66-4744-a064-3ed88d986eba","childNodes":["do_11376182093890355216","do_11376181820567552012","do_11376182438513868818","do_11376181820809216014"],"discussionForum":{"enabled":"No"},"mediaType":"content","osId":"org.ekstep.quiz.app","languageCode":["en"],"lastPublishedBy":"4b4dda54-b061-4346-9aaa-e2801430b885","version":2,"se_subjects":["Telugu"],"license":"CC BY 4.0","prevState":"Review","size":8922,"lastPublishedOn":"2023-03-27T09:45:40.159+0000","name":"Test Book","mediumIds":["ncf_medium_telugu"],"status":"Live","code":"org.sunbird.jMNK3Z","credentials":{"enabled":"No"},"prevStatus":"Processing","description":"Enter description for TextBook","medium":["Telugu"],"idealScreenSize":"normal","createdOn":"2023-03-27T09:13:56.965+0000","se_boards":["Other"],"se_mediumIds":["ncf_medium_telugu"],"copyrightYear":2023,"contentDisposition":"inline","lastUpdatedOn":"2023-03-27T09:45:40.820+0000","dialcodeRequired":"No","lastStatusChangedOn":"2023-03-27T09:45:40.820+0000","createdFor":["0137541424673095687"],"creator":"BookCreator bookCreator","os":["All"],"se_subjectIds":["ncf_subject_telugu"],"se_FWIds":["NCF"],"pkgVersion":1,"versionKey":"1679910320288","idealScreenDensity":"hdpi","framework":"NCF","depth":0,"s3Key":"content/do_11376180991508480011/artifact/do_11376180991508480011_toc.json","boardIds":["ncf_board_other"],"lastSubmittedOn":"2023-03-27T09:45:20.280+0000","createdBy":"7bf81b8b-ab64-47ca-b9d7-c9f74f811980","compatibilityLevel":1,"leafNodesCount":2,"userConsent":"Yes","gradeLevelIds":["ncf_gradelevel_class1"],"board":"Other","resourceType":"Book"}}}"""
    try {
      server.start(3000)
    } catch {
      case e: IOException =>
        System.out.println("Exception" + e)
    }
    server.enqueue(new MockResponse().setBody(do_11307972307046400011917_response))
    server.url("http://127.0.0.1:3000/api/content/v1/read/do_11307972307046400011917")
    server.enqueue(new MockResponse().setBody(do_1131998128479272961991_response))
    server.url("http://127.0.0.1:3000/api/content/v1/read/do_1131998128479272961991")
  }


  "AssessmentAggregator " should "Update event to db" in {
    when(mockKafkaUtil.kafkaEventSource[Event](assessmentConfig.kafkaInputTopic)).thenReturn(new AssessmentAggreagatorEventSource)
    when(mockKafkaUtil.kafkaEventSink[Event](assessmentConfig.kafkaFailedTopic)).thenReturn(new FailedEventsSink)
    when(mockKafkaUtil.kafkaStringSink(assessmentConfig.kafkaCertIssueTopic)).thenReturn(new certificateIssuedEventsSink)
    val task = new AssessmentAggregatorStreamTask(assessmentConfig, mockKafkaUtil)
    task.process()
    assert(FailedEventsSink.values.get(0).getTelemetry.getMap.containsKey("metadata"))
    BaseMetricsReporter.gaugeMetrics(s"${assessmentConfig.jobName}.${assessmentConfig.skippedEventCount}").getValue() should be(1)
    BaseMetricsReporter.gaugeMetrics(s"${assessmentConfig.jobName}.${assessmentConfig.dbReadCount}").getValue() should be(2)
    BaseMetricsReporter.gaugeMetrics(s"${assessmentConfig.jobName}.${assessmentConfig.dbUpdateCount}").getValue() should be(6)
    BaseMetricsReporter.gaugeMetrics(s"${assessmentConfig.jobName}.${assessmentConfig.failedEventCount}").getValue() should be(2)
    BaseMetricsReporter.gaugeMetrics(s"${assessmentConfig.jobName}.${assessmentConfig.batchSuccessCount}").getValue() should be(6)
    BaseMetricsReporter.gaugeMetrics(s"${assessmentConfig.jobName}.${assessmentConfig.cacheHitCount}").getValue() should be(8)
    BaseMetricsReporter.gaugeMetrics(s"${assessmentConfig.jobName}.${assessmentConfig.cacheHitMissCount}").getValue() should be(1)
    BaseMetricsReporter.gaugeMetrics(s"${assessmentConfig.jobName}.${assessmentConfig.certIssueEventsCount}").getValue() should be(6)
    BaseMetricsReporter.gaugeMetrics(s"${assessmentConfig.jobName}.${assessmentConfig.dbScoreAggUpdateCount}").getValue() should be(6)
    BaseMetricsReporter.gaugeMetrics(s"${assessmentConfig.jobName}.${assessmentConfig.dbScoreAggReadCount}").getValue() should be(6)
    BaseMetricsReporter.gaugeMetrics(s"${assessmentConfig.jobName}.${assessmentConfig.recomputeAggEventCount}").getValue() should be(0)
    val test_row1 = cassandraUtil.findOne("select total_score,total_max_score from sunbird_courses.assessment_aggregator where user_id='d0d8a341-9637-484c-b871-0c27015af238' and course_id='do_2128410273679114241112'")
    assert(test_row1.getDouble("total_score") == 2.0)
    assert(test_row1.getDouble("total_max_score") == 2.0)

    val test_row2: java.util.List[Row] = cassandraUtil.find("select attempt_id,total_score,total_max_score from sunbird_courses.assessment_aggregator where user_id='ff1c4bdf-27e2-49bc-a53f-6e304bb3a87f' and course_id='do_2128415652377067521125'")

    assert(test_row2.get(0).getString("attempt_id") == "8cd87e24df268ad09a8b0060c0a40271")
    assert(test_row2.get(0).getDouble("total_score") == 2.0)
    assert(test_row2.get(0).getDouble("total_max_score") == 3.0)

    assert(test_row2.get(1).getString("attempt_id") == "9dd87e24df268ad09a8b0060c0a40262")
    assert(test_row2.get(1).getDouble("total_score") == 1.33)
    assert(test_row2.get(1).getDouble("total_max_score") == 3.0)

    val test_row3 = cassandraUtil.findOne("select aggregates from sunbird_courses.user_activity_agg where activity_type='Course' and activity_id='do_2128410273679114241112' and  user_id='d0d8a341-9637-484c-b871-0c27015af238'")
    val resultMap3 = test_row3.getMap("aggregates", new TypeToken[String]() {}, new TypeToken[java.lang.Double]() {})
    assert(null != resultMap3)
    assert(2 == resultMap3.getOrDefault("score:do_2128373396098744321673", 0))
    assert(2 == resultMap3.getOrDefault("max_score:do_2128373396098744321673", 0))
    assert(1 == resultMap3.getOrDefault("attempts_count:do_2128373396098744321673", 0))

    val test_row6 = cassandraUtil.findOne("select agg_details from sunbird_courses.user_activity_agg where activity_type='Course' and activity_id='do_2128415652377067521125' and  user_id='ff1c4bdf-27e2-49bc-a53f-6e304bb3a87f'")
    val aggDetail1 = new Gson().fromJson(test_row6.getList("agg_details", new TypeToken[String](){}).get(0), classOf[AggDetails])
    assert(null != aggDetail1)
    assert("8cd87e24df268ad09a8b0060c0a40271" == aggDetail1.attempt_id)
    assert("do_212686723743318016173" == aggDetail1.content_id)
    assert(2 == aggDetail1.score)
    assert(3 == aggDetail1.max_score)
    assert("attempt_metrics" == aggDetail1.`type`)

    val aggDetail2 = new Gson().fromJson(test_row6.getList("agg_details", new TypeToken[String](){}).get(1), classOf[AggDetails])
    assert(null != aggDetail2)
    assert("9dd87e24df268ad09a8b0060c0a40262" == aggDetail2.attempt_id)
    assert("do_212686723743318016173" == aggDetail2.content_id)
    assert(1.33 == aggDetail2.score)
    assert(3 == aggDetail2.max_score)
    assert("attempt_metrics" == aggDetail2.`type`)

    val test_row4 = cassandraUtil.findOne("select aggregates from sunbird_courses.user_activity_agg where activity_type='Course' and activity_id='do_2128415652377067521125' and  user_id='ff1c4bdf-27e2-49bc-a53f-6e304bb3a87f'")
    val resultMap4 = test_row4.getMap("aggregates", new TypeToken[String]() {}, new TypeToken[java.lang.Double]() {})
    assert(null != resultMap4)
    assert(2 == resultMap4.getOrDefault("score:do_212686723743318016173", 0))
    assert(3 == resultMap4.getOrDefault("max_score:do_212686723743318016173", 0))
    assert(2 == resultMap4.getOrDefault("attempts_count:do_212686723743318016173", 0))

    val test_row5 = cassandraUtil.findOne("select aggregates from sunbird_courses.user_activity_agg where activity_type='Course' and activity_id='do_3129323995959541761169' and  user_id='50a9e3fc-d047-4fa5-a37b-67501b8933db'")
    val resultMap5 = test_row5.getMap("aggregates", new TypeToken[String]() {}, new TypeToken[java.lang.Double]() {})
    assert(null != resultMap5)
    assert(1 == resultMap5.getOrDefault("score:do_3129323935897108481169", 0))
    assert(1 == resultMap5.getOrDefault("max_score:do_3129323935897108481169", 0))
    assert(1 == resultMap5.getOrDefault("attempts_count:do_3129323935897108481169", 0))
  }

  "AssessmentAggregator " should "Skip the missing records from the event" in {
    val forceValidationAssessmentConfig: AssessmentAggregatorConfig = new AssessmentAggregatorConfig(ConfigFactory.load("forcevalidate.conf"))
    when(mockKafkaUtil.kafkaEventSource[Event](forceValidationAssessmentConfig.kafkaInputTopic)).thenReturn(new AssessmentAggreagatorEventSourceForceValidation)
    when(mockKafkaUtil.kafkaEventSink[Event](forceValidationAssessmentConfig.kafkaFailedTopic)).thenReturn(new FailedEventsSink)
    when(mockKafkaUtil.kafkaStringSink(forceValidationAssessmentConfig.kafkaCertIssueTopic)).thenReturn(new certificateIssuedEventsSink)
    val task = new AssessmentAggregatorStreamTask(forceValidationAssessmentConfig, mockKafkaUtil)
    task.process()
    BaseMetricsReporter.gaugeMetrics(s"${assessmentConfig.jobName}.${assessmentConfig.batchSuccessCount}").getValue() should be(1)
    BaseMetricsReporter.gaugeMetrics(s"${assessmentConfig.jobName}.${assessmentConfig.cacheHitCount}").getValue() should be(5)
    BaseMetricsReporter.gaugeMetrics(s"${assessmentConfig.jobName}.${assessmentConfig.apiHitSuccessCount}").getValue() should be(0)
    BaseMetricsReporter.gaugeMetrics(s"${assessmentConfig.jobName}.${assessmentConfig.ignoredEventsCount}").getValue() should be(1)
  }

  def testCassandraUtil(cassandraUtil: CassandraUtil): Unit = {
    cassandraUtil.reconnect()
    val response = cassandraUtil.find("SELECT * FROM sunbird_courses.assessment_aggregator;")
    response should not be (null)
  }

  def setupRedisTestData() {
    val redisConnect = new RedisConnect(assessmentConfig.metaRedisHost, assessmentConfig.metaRedisPort, assessmentConfig)
    val jedis = redisConnect.getConnection(assessmentConfig.relationCacheNode)
    EventFixture.leafNodesList.map(nodes => {
      nodes.map(node => {
        jedis.sadd(node._1, node._2)
      })
    })

    // Setup content Cache
    val contentCache = redisConnect.getConnection(assessmentConfig.contentCacheNode)
    EventFixture.contentCacheList.map(nodes => {
      nodes.map(node => {
        contentCache.set(node._1, node._2)
      })
    })
  }
}



class AssessmentAggreagatorEventSource extends SourceFunction[Event] {

  override def run(ctx: SourceContext[Event]) {
    val gson = new Gson()

    //val eventMap1 = gson.fromJson(EventFixture.BATCH_ASSESS_EVENT, new util.LinkedHashMap[String, Any]().getClass)

    val eventMap1 = JSONUtil.deserialize[util.HashMap[String, Any]](EventFixture.BATCH_ASSESS_EVENT)
    val eventMap2 = JSONUtil.deserialize[util.HashMap[String, Any]](EventFixture.BATCH_ASSESS__OLDER_EVENT)
    val eventMap3 = JSONUtil.deserialize[util.HashMap[String, Any]](EventFixture.BATCH_ASSESS_FAIL_EVENT)
    val eventMap4 = JSONUtil.deserialize[util.HashMap[String, Any]](EventFixture.QUESTION_EVENT_RES_VALUES)
    val eventMap5 = JSONUtil.deserialize[util.HashMap[String, Any]](EventFixture.LATEST_BATCH_ASSESS_EVENT)
    val eventMap6 = JSONUtil.deserialize[util.HashMap[String, Any]](EventFixture.BATCH_DUPLICATE_QUESTION_EVENT)
    val eventMap7 = JSONUtil.deserialize[util.HashMap[String, Any]](EventFixture.INVALID_CONTENT_ID_EVENT)
    val eventMap8 = JSONUtil.deserialize[util.HashMap[String, Any]](EventFixture.BATCH_ASSESS_EVENT_WITHOUT_CACHE)
    val eventMap9 = JSONUtil.deserialize[util.HashMap[String, Any]](EventFixture.SECOND_ATTEMPT_BATCH_ASSESS_EVENT)
    ctx.collect(new Event(eventMap1))
    ctx.collect(new Event(eventMap2))
    ctx.collect(new Event(eventMap3))
    ctx.collect(new Event(eventMap4))
    ctx.collect(new Event(eventMap5))
    ctx.collect(new Event(eventMap6))
    ctx.collect(new Event(eventMap7))
    ctx.collect(new Event(eventMap8))
    ctx.collect(new Event(eventMap9))
  }

  override def cancel() = {}

}


class AssessmentAggreagatorEventSourceForceValidation extends SourceFunction[Event] {
  override def run(ctx: SourceContext[Event]) {
    val eventMap1 = JSONUtil.deserialize[util.HashMap[String, Any]](EventFixture.DUPLICATE_BATCH_ASSESS_EVENTS_1)
    val eventMap2 = JSONUtil.deserialize[util.HashMap[String, Any]](EventFixture.DUPLICATE_BATCH_ASSESS_EVENTS_2)
    val eventMap3 = JSONUtil.deserialize[util.HashMap[String, Any]](EventFixture.DUPLICATE_BATCH_ASSESS_EVENTS_3)
    val eventMap4 = JSONUtil.deserialize[util.HashMap[String, Any]](EventFixture.DUPLICATE_BATCH_ASSESS_EVENTS_4)
    ctx.collect(new Event(eventMap1))
    ctx.collect(new Event(eventMap2))
    ctx.collect(new Event(eventMap3))
    ctx.collect(new Event(eventMap4))
  }

  override def cancel() = {}

}


class FailedEventsSink extends SinkFunction[Event] {

  override def invoke(value: Event): Unit = {
    synchronized {
      FailedEventsSink.values.add(value)
    }
  }
}

object FailedEventsSink {
  val values: util.List[Event] = new util.ArrayList()
}

class certificateIssuedEventsSink extends SinkFunction[String] {

  override def invoke(value: String): Unit = {
    synchronized {
      certificateIssuedEvents.values.add(value)
    }
  }
}

object certificateIssuedEvents {
  val values: util.List[String] = new util.ArrayList()
}