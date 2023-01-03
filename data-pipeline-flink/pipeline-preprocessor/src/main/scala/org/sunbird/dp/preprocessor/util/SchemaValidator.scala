package org.sunbird.dp.preprocessor.util

import java.io.{File, IOException}
import java.nio.charset.StandardCharsets
import java.nio.file._

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.core.exceptions.ProcessingException
import com.github.fge.jsonschema.core.report.ProcessingReport
import com.github.fge.jsonschema.main.{JsonSchema, JsonSchemaFactory}
import io.github.classgraph.{ClassGraph, Resource}
import org.slf4j.LoggerFactory
import org.sunbird.dp.preprocessor.domain.Event
import org.sunbird.dp.preprocessor.task.PipelinePreprocessorConfig

import scala.collection.mutable

class SchemaValidator(config: PipelinePreprocessorConfig) extends java.io.Serializable {

  private val serialVersionUID = 8780940932759659175L
  private[this] val logger = LoggerFactory.getLogger(classOf[SchemaValidator])
  private[this] val objectMapper = new ObjectMapper()

  logger.info("Initializing schema for telemetry objects...")

  private val schemaJsonMap: Map[String, JsonSchema] = {
    readResourceFiles(s"${config.schemaPath}")
  }

  def readResourceFiles(schemaUrl: String): Map[String, JsonSchema] = {
    val classGraphResult = new ClassGraph().acceptPaths(schemaUrl).scan()
    val schemaFactory = JsonSchemaFactory.byDefault
    val schemaMap = new mutable.HashMap[String, JsonSchema]()
    try {
      val resources = classGraphResult.getResourcesWithExtension("json")
      resources.forEachByteArrayIgnoringIOException((res: Resource, content: Array[Byte]) => {
        schemaMap += Paths.get(res.getPath).getFileName.toString -> schemaFactory.getJsonSchema(JsonLoader.fromString(new String(content, StandardCharsets.UTF_8)))
      })
    } catch {
      case ex: Exception => ex.printStackTrace()
        throw ex
    } finally {
      classGraphResult.close()
    }
    schemaMap.toMap
  }

  logger.info("Schema initialization completed for telemetry objects...")

  def schemaFileExists(event: Event): Boolean = schemaJsonMap.contains(event.schemaName)

  @throws[IOException]
  @throws[ProcessingException]
  def validate(event: Event, isSchemaPresent:Boolean): ProcessingReport = {
    val eventJson = objectMapper.convertValue[JsonNode](event.getTelemetry.map, classOf[JsonNode])
    val report = if(isSchemaPresent) schemaJsonMap(event.schemaName).validate(eventJson) else schemaJsonMap(config.defaultSchemaFile).validate(eventJson)
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
// $COVERAGE-ON$
