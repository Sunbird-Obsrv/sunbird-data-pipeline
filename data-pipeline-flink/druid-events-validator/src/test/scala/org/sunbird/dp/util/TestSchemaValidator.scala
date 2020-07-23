package org.sunbird.dp.util

import com.typesafe.config.ConfigFactory
import org.scalatest.{FlatSpec, Matchers}
import org.sunbird.dp.validator.task.DruidValidatorConfig
import org.sunbird.dp.validator.util.SchemaValidator

class TestSchemaValidator extends FlatSpec with Matchers {

    "SchemaValidator" should "give default validation message" in {
        val config = ConfigFactory.load("test.conf");
        val druidValidatorConfig = new DruidValidatorConfig(config)
        val schemaValidator = new SchemaValidator(druidValidatorConfig)

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

        val outErrMsg2 = schemaValidator.getInvalidFieldName(errMsgWithoutPointer)
        outErrMsg2 should be("Unable to obtain field name for failed validation")

    }
}
