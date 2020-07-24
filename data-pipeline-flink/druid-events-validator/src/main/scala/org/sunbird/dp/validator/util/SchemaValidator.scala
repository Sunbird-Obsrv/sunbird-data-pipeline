package org.sunbird.dp.validator.util

import java.io.IOException
import java.text.MessageFormat

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.core.exceptions.ProcessingException
import com.github.fge.jsonschema.core.report.ProcessingReport
import com.github.fge.jsonschema.main.JsonSchemaFactory
import com.google.common.io.ByteStreams
import org.slf4j.LoggerFactory
import org.sunbird.dp.validator.domain.Event
import org.sunbird.dp.validator.task.DruidValidatorConfig

class SchemaValidator(config: DruidValidatorConfig) extends java.io.Serializable {

  private val serialVersionUID = 8780940932759659175L
  private[this] val logger = LoggerFactory.getLogger(classOf[SchemaValidator])
  private val schemaFactory = JsonSchemaFactory.byDefault
  private[this] val objectMapper = new ObjectMapper()

  logger.info("Initializing schema for telemetry objects...")
  val pattern = "{0}/{1}"
  val telemetrySchemaPath: String = MessageFormat.format(pattern, config.telemetrySchemaPath, config.defaultSchemaFile)
  val summaryEventSchemapath: String = MessageFormat.format(pattern, config.summarySchemaPath, config.summarySchemaFile)
  val searchEventSchemaPath: String = MessageFormat.format(pattern, config.telemetrySchemaPath, config.searchSchemaFile)

  val telemetrySchema = new String(ByteStreams.toByteArray(this.getClass.getClassLoader.getResourceAsStream(telemetrySchemaPath)))
  val summarySchema = new String(ByteStreams.toByteArray(this.getClass.getClassLoader.getResourceAsStream(summaryEventSchemapath)))
  val searchEventSchema = new String(ByteStreams.toByteArray(this.getClass.getClassLoader.getResourceAsStream(searchEventSchemaPath)))

  private val telemetryJsonSchema = schemaFactory.getJsonSchema(JsonLoader.fromString(telemetrySchema))
  private val summaryJsonSchema = schemaFactory.getJsonSchema(JsonLoader.fromString(summarySchema))
  private val searchEventJsonSchema = schemaFactory.getJsonSchema(JsonLoader.fromString(searchEventSchema))

  logger.info("Schema initialization completed for telemetry objects...")

  @throws[IOException]
  @throws[ProcessingException]
  def validate(event: Event): ProcessingReport = {
    val eventJson = objectMapper.convertValue[JsonNode](event.getTelemetry.map, classOf[JsonNode])
    val report = if (event.isSearchEvent) searchEventJsonSchema.validate(eventJson)
    else if (event.isSummaryEvent) summaryJsonSchema.validate(eventJson)
    else telemetryJsonSchema.validate(eventJson)
    report
  }

  def getInvalidFieldName(errorInfo: String): String = {
    val message = errorInfo.split("reports:")
    val defaultValidationErrMsg = "Unable to obtain field name for failed validation"
    if (message.length > 1) {
      val fields = message(1).split(",")
      if (fields.length > 2) {
        val pointer = fields(3).split("\"pointer\":")
        pointer(1).substring(0, pointer(1).length - 1)
      } else {
        defaultValidationErrMsg
      }
    } else {
      defaultValidationErrMsg
    }
  }

}