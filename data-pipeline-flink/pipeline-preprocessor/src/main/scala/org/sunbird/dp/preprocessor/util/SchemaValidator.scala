package org.sunbird.dp.preprocessor.util

import java.io.{File, IOException}
import java.nio.file.{FileSystems, Files, Paths}

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.core.exceptions.ProcessingException
import com.github.fge.jsonschema.core.report.ProcessingReport
import com.github.fge.jsonschema.main.{JsonSchema, JsonSchemaFactory}
import com.google.common.io.ByteStreams
import com.google.gson.Gson
import org.slf4j.LoggerFactory
import org.sunbird.dp.preprocessor.domain.Event
import org.sunbird.dp.preprocessor.task.PipelinePreprocessorConfig

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.Try

class SchemaValidator(config: PipelinePreprocessorConfig) extends java.io.Serializable {

  private val serialVersionUID = 8780940932759659175L
  private[this] val logger = LoggerFactory.getLogger(classOf[SchemaValidator])
  private[this] val objectMapper = new ObjectMapper()

  logger.info("Initializing schema for telemetry objects...")

  private val schemaJsonMap: Map[String, JsonSchema] = {
    val schamaMap = new mutable.HashMap[String, JsonSchema]()
    val schemaFactory = JsonSchemaFactory.byDefault
    val schemaUrl = this.getClass.getClassLoader.getResource(s"${config.schemaPath}").toURI
    // $COVERAGE-OFF$ Disabling code coverage for below code, It can be testable only incase of jar
    val schemaFiles = if (schemaUrl.getScheme.equalsIgnoreCase("jar")) {
      val fileSystem = FileSystems.newFileSystem(schemaUrl, Map[String, AnyRef]().asJava)
      val files = loadSchemaFiles(fileSystem.getPath(s"${config.schemaPath}"))
      fileSystem.close()
      files
    } else {
      loadSchemaFiles(Paths.get(schemaUrl))
    }

    logger.info(s"Loaded ${schemaFiles.size} telemetry schema files...")
    schemaFiles.map { schemaFile =>
      val schemaJson =
        new String(ByteStreams.toByteArray(
          this.getClass.getClassLoader.getResourceAsStream(s"${config.schemaPath}/$schemaFile")
        ))
      schamaMap += schemaFile.toString -> schemaFactory.getJsonSchema(JsonLoader.fromString(schemaJson))
    }
    schamaMap.toMap
  }

  logger.info("Schema initialization completed for telemetry objects...")

  def loadSchemaFiles(schemaDirPath: java.nio.file.Path): List[String] = {
    val schemaFiles = Try(Files.newDirectoryStream(schemaDirPath)).map { stream =>
      stream.iterator().asScala.toList.map(path => path.getFileName.toString)
    }.getOrElse(List[String]())
    schemaFiles
  }

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
