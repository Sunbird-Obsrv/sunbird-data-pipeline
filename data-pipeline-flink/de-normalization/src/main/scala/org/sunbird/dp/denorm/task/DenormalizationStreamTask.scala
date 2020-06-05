package org.sunbird.dp.denorm.task

import com.typesafe.config.ConfigFactory
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.sunbird.dp.core.job.FlinkKafkaConnector
import org.sunbird.dp.core.util.FlinkUtil
import org.sunbird.dp.denorm.domain.Event
import org.sunbird.dp.denorm.functions._

/**
 * Denormalization stream task does the following pipeline processing in a sequence:
 *
 * 1. Merge the unique and derived sources
 * 2. Skip the denorm for summary events other than WFS and for LOG events. Increment the skipped counter
 * 3. If the event is older than ignorePeriodInMonths config the event is pushed to failed topic. Increment the expired events counter
 * 4. Start the denorm process
 * 5. DeviceDenorm - If the event has 'did' then
 * 		5.1 Fetch the device cache from redis and stamp the data under `devicedata`
 * 		5.2 Derive the iso state code.
 * 		5.1 Increment the did-total, did-cache-hit, did-cache-miss counters appropriately
 * 6. 	UserDenrom - If the event has 'actor.id' and actor.type is 'User' then
 * 		6.1 Fetch the user cache from redis and stamp the data under `userdata`
 * 		6.2 Increment the user-total, user-cache-hit, user-cache-miss counters appropriately
 * 7. ObjectDenorm - If the event has 'object.id' and 'object.type'
 * 		7.1 If object.type == 'dialcode' or 'qr', then
 * 				7.1.1 Fetch the dial cache from redis and stamp the data under `dialcodedata`
 * 				7.1.2 Convert 'generatedon' and 'publishedon' to epoch timestamp
 * 				7.1.3 Increment the dial-total, dial-cache-hit, dial-cache-miss counters appropriately
 * 		7.2 If object.type == 'user' then do nothing
 * 		7.3 else
 * 				7.3.1 Fetch the content cache from redis and stamp the data under `contentdata`
 * 				7.3.2 If the object.rollup.l1 is not null and not equals object.id, then fetch the content cache from redis
 * 							and stamp the data under `collectiondata`.
 * 				7.3.3 Convert 'lastsubmittedon', 'lastupdatedon' and 'lastpublishedon' to epoch timestamp
 * 				7.3.4 Increment the content-total, content-cache-hit, content-cache-miss counters appropriately
 * 				7.3.5 Increment the coll-total, coll-cache-hit, coll-cache-miss counters appropriately
 * 8. DerviedLocationDenorm
 * 		8.1 Fetch the user declared and ip location from device cache and user profile location from user cache.
 * 		8.2 Derive the location with the fallback for user profile > user declared > ip location.
 * 		8.3 Add the derived location to the event under `derivedlocationdata` along with where it is derived from
 * 9. Common
 * 		9.1 Add appropriate denorm flags of what denorm has been done on the event
 * 		9.2 Retry once from redis on any redis connection issues
 * 		9.3 Stop the job from proceeding further if there are any redis connection issues
 */
class DenormalizationStreamTask(config: DenormalizationConfig, kafkaConnector: FlinkKafkaConnector) {

  private val serialVersionUID = -7729362727131516112L

  def process(): Unit = {
    
    implicit val env: StreamExecutionEnvironment = FlinkUtil.getExecutionContext(config)
    implicit val eventTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])

    val source = kafkaConnector.kafkaEventSource[Event](config.inputTopic)
    val deviceDenormStream =
      env.addSource(source, config.denormalizationConsumer).uid(config.denormalizationConsumer).setParallelism(config.kafkaConsumerParallelism).rebalance()
        .process(new DeviceDenormFunction(config)).name(config.deviceDenormFunction).uid(config.deviceDenormFunction)
    val userDenormStream = deviceDenormStream.getSideOutput(config.withDeviceEventsTag).process(new UserDenormFunction(config)).name(config.userDenormFunction).uid(config.userDenormFunction)
    val dialCodeDenormStream = userDenormStream.getSideOutput(config.withUserEventsTag).process(new DialCodeDenormFunction(config)).name(config.dialcodeDenormFunction).uid(config.dialcodeDenormFunction)
    val contentDenormStream = dialCodeDenormStream.getSideOutput(config.withDialCodeEventsTag).process(new ContentDenormFunction(config)).name(config.contentDenormFunction).uid(config.contentDenormFunction)
    val locDenormStream = contentDenormStream.getSideOutput(config.withContentEventsTag).process(new LocationDenormFunction(config)).name(config.locationDenormFunction).uid(config.locationDenormFunction)

    locDenormStream.getSideOutput(config.withLocationEventsTag).addSink(kafkaConnector.kafkaEventSink(config.denormSuccessTopic)).name(config.DENORM_EVENTS_PRODUCER).uid(config.DENORM_EVENTS_PRODUCER)

    env.execute(config.jobName)
  }

}

// $COVERAGE-OFF$ Disabling scoverage as the below code can only be invoked within flink cluster
object DenormalizationStreamTask {

  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load("de-normalization.conf").withFallback(ConfigFactory.systemEnvironment())
    val eConfig = new DenormalizationConfig(config)
    val kafkaUtil = new FlinkKafkaConnector(eConfig)
    val task = new DenormalizationStreamTask(eConfig, kafkaUtil)
    task.process()
  }
}
// $COVERAGE-ON$
