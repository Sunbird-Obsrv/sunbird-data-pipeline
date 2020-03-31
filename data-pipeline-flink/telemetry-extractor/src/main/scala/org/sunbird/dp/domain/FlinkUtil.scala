package org.sunbird.dp.domain

import java.util
import java.util.UUID.randomUUID

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator
import org.apache.flink.streaming.api.scala.OutputTag
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer

object FlinkUtil {

  def registerOutPut(stream: SingleOutputStreamOperator[util.Map[String, AnyRef]],
                     producer: FlinkKafkaProducer[util.Map[String, AnyRef]],
                     outPutTag: String) {
    implicit val eventTypeInfo: TypeInformation[util.Map[String, AnyRef]] = TypeExtractor.getForClass(classOf[util.Map[String, AnyRef]])

    stream.getSideOutput(OutputTag[util.Map[String, AnyRef]](outPutTag))
      .addSink(producer)
      .name(s"kafka-telemetry-${outPutTag.toLowerCase}-producer")
  }

  /**
   * Method to Generate the LOG Event to Determine the Number of events has extracted.
   */
  def generateAudiEvents(totalEvents: Int, batchEvents: util.Map[String, AnyRef]): LogEvent = {
    LogEvent(
      actor = Actor("sunbird.telemetry", "telemetry-sync"),
      eid = "LOG",
      edata = EData(level = "INFO", "telemetry_audit", message = "telemetry sync", Array(Params("3.0", totalEvents, "SUCCESS"), Params("3.0", totalEvents, "SUCCESS"))),
      syncts = System.currentTimeMillis(),
      ets = System.currentTimeMillis(),
      context = Context(channel = "in.sunbird", env = "data-pipeline",
        sid = randomUUID().toString,
        did = randomUUID().toString,
        pdata = Pdata("3.0", "telemetry-extractor", "data-pipeline"),
        cdata = null),
      mid = randomUUID().toString,
      `object` = Object(randomUUID().toString, "3.0", "telemetry-events", None),
      tags = null)
  }
}
