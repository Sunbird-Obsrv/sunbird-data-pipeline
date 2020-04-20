package org.sunbird.dp.util

import com.typesafe.config.ConfigFactory
import org.scalatest.{FlatSpec, Matchers}
import org.sunbird.dp.preprocessor.task.PipelinePreprocessorConfig
import org.sunbird.dp.preprocessor.util.SchemaValidator

class TestSchemaValidator extends FlatSpec with Matchers {

  "SchemaValidator" should "give default validation message" in {
    val config = ConfigFactory.load("test.conf");
    val piplineProcessorConfig = new PipelinePreprocessorConfig(config)
    val schemaValidator = new SchemaValidator(piplineProcessorConfig)

    val errMsgWithoutReportData =
      """
        |--- BEGIN MESSAGES ---
        |error: instance failed to match all required schemas (matched only 1 out of 2)
        |    level: "error"
        |    schema: {"loadingURI":"#","pointer":""}
        |    instance: {"pointer":""}
        |    domain: "validation"
        |    keyword: "allOf"
        |    matched: 1
        |    nrSchemas: 2
        |---  END MESSAGES  ---
            """.stripMargin

    val outErrMsg1 = schemaValidator.getInvalidFieldName(errMsgWithoutReportData)
    outErrMsg1 should be("Unable to obtain field name for failed validation")

    val errMsgWithPointer =
      """
        |--- BEGIN MESSAGES ---
        |error: instance failed to match all required schemas (matched only 1 out of 2)
        |    level: "error"
        |    schema: {"loadingURI":"#","pointer":""}
        |    instance: {"pointer":""}
        |    domain: "validation"
        |    keyword: "allOf"
        |    matched: 1
        |    nrSchemas: 2
        |    reports: {"/allOf/0":[{"level":"error"}, "/error":["error"], "/error":["error"], "pointer": ["error"] }
        |---  END MESSAGES  ---
            """.stripMargin

    val outErrMsg2 = schemaValidator.getInvalidFieldName(errMsgWithPointer)
    outErrMsg2 should not be(null)


    val errMsgWithoutPointer =
      """
        |--- BEGIN MESSAGES ---
        |error: instance failed to match all required schemas (matched only 1 out of 2)
        |    level: "error"
        |    schema: {"loadingURI":"#","pointer":""}
        |    instance: {"pointer":""}
        |    domain: "validation"
        |    keyword: "allOf"
        |    matched: 1
        |    nrSchemas: 2
        |    reports: {"/allOf/0":[{"level":"error"}]}
        |---  END MESSAGES  ---
            """.stripMargin

    val outErrMsg3 = schemaValidator.getInvalidFieldName(errMsgWithoutPointer)
    outErrMsg3 should be("Unable to obtain field name for failed validation")
  }
}
