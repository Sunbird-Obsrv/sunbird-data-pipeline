package org.sunbird.dp.util

import java.io.IOException
import java.nio.file.{FileSystems, Files, Paths}
import java.text.MessageFormat

import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.core.report.ProcessingReport
import com.github.fge.jsonschema.main.{JsonSchema, JsonSchemaFactory}
import com.github.fge.jsonschema.core.exceptions.ProcessingException
import com.google.common.io.ByteStreams
import org.slf4j.LoggerFactory
import org.sunbird.dp.domain.Event
import org.sunbird.dp.task.DruidValidatorConfig
import scala.collection.JavaConverters._
import scala.util.Try

class SchemaValidator(config: DruidValidatorConfig) extends java.io.Serializable {

    private val serialVersionUID = 8780940932759659175L
    private[this] val logger = LoggerFactory.getLogger(classOf[SchemaValidator])
    private val schemaFactory = JsonSchemaFactory.byDefault

    logger.info("Initializing schema for telemetry objects...")

    val schemaFormat = "{0}/{1}";
    val telemetrySchemaPath: String = MessageFormat.format(schemaFormat, config.telemetrySchemaPath, config.defaultSchemaFile)
    val summaryEventSchemapath: String = MessageFormat.format(schemaFormat, config.summarySchemaPath, config.summarySchemaFile)
    val searchEventSchemaPath: String = MessageFormat.format(schemaFormat, config.telemetrySchemaPath, config.searchSchemaFile)

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
        val eventJson = JsonLoader.fromString(event.getJson)
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