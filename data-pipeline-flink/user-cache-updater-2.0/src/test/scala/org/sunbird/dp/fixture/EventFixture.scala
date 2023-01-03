package org.sunbird.dp.fixture

object EventFixture {

  val telemetrEvents: List[String] = List(

    /**
      * User-Id : user-1
      * Edata.state is update
      * Having all the user data
      */
    """
      {"eid":"AUDIT","ets":1573121861118,"ver":"3.0","mid":"1573121861118.40f9136b-1cc3-458d-a04a-4459606df","actor":{"id":"5609876543234567890987654345678","type":"Request"},"context":{"channel":"01285019302823526477","pdata":{"id":"dev.sunbird.portal","pid":"learner-service","ver":"2.5.0"},"env":"User","did":"user-3","cdata":[{"id":"google","type":"SignupType"}],"rollup":{"l1":"01285019302823526477"}},"object":{"id":"user-1","type":"user"},"edata":{"state":"Update","props":["recoveryEmail","recoveryPhone","userId","id","externalIds","updatedDate","updatedBy"]},"syncts":1573121861125,"@timestamp":"2019-11-07T10:17:41.125Z","flags":{"tv_processed":true,"dd_processed":true},"type":"events","ts":"2019-11-07T10:17:41.118+0000"}
      |
      |""".stripMargin,
    /**
      * UserId: user-9
      * Client_Error
      */
    """{"actor":{"id":"cb0defc7-9f5f-4c0a-bb73-207d34ed2cf0","type":"User"},"eid":"AUDIT","edata":{"state":"Update","type":"mergeUser","props":["fromAccountId","toAccountId","type"]},"ver":"3.0","syncts":1621593310099,"@timestamp":"2021-05-21T10:35:10.099Z","ets":1621593307552,"context":{"channel":"0126796199493140480","pdata":{"id":"preprod.diksha.learning.service","pid":"learner-service","ver":"3.9.0"},"env":"User","cdata":[{"id":"9e6f3392-af99-425b-9404-e8e4078a1b65","type":"FromAccountId"},{"id":"cb0defc7-9f5f-4c0a-bb73-207d34ed2cf0","type":"ToAccountId"},{"id":"8f0d93fd-33ac-4a64-a39c-f008011c976a","type":"Request"}],"rollup":{"l1":"01275678925675724817"}},"flags":{"pp_duplicate_skipped":true,"pp_validation_processed":true},"mid":"8f0d93fd-33ac-4a64-a39c-f008011c976a","type":"events","object":{"id":"user-8","type":"User"}}""",
    /**
      * User-Id : user-2
      * Edata.state is update
      * user's data missed from API
      */
    """
      {"eid":"AUDIT","ets":1573121861118,"ver":"3.0","mid":"1573121861118.40f9136b-1cc3-458d-a04a-4459606df","actor":{"id":"5609876543234567890987654345678","type":"Request"},"context":{"channel":"01285019302823526477","pdata":{"id":"dev.sunbird.portal","pid":"learner-service","ver":"2.5.0"},"env":"User","did":"user-3","cdata":[{"id":"google","type":"SignupType"}],"rollup":{"l1":"01285019302823526477"}},"object":{"id":"user-2","type":"user"},"edata":{"state":"Update","props":["recoveryEmail","recoveryPhone","userId","id","externalIds","updatedDate","updatedBy"]},"syncts":1573121861125,"@timestamp":"2019-11-07T10:17:41.125Z","flags":{"tv_processed":true,"dd_processed":true},"type":"events","ts":"2019-11-07T10:17:41.118+0000"}
      |
      |""".stripMargin,
    /**
      * User-Id : user-3
      * Edata.state is update
      * framework and block info not present
      */
    """
      {"eid":"AUDIT","ets":1573121861118,"ver":"3.0","mid":"1573121861118.40f9136b-1cc3-458d-a04a-4459606df","actor":{"id":"5609876543234567890987654345678","type":"Request"},"context":{"channel":"01285019302823526477","pdata":{"id":"dev.sunbird.portal","pid":"learner-service","ver":"2.5.0"},"env":"User","did":"user-3","cdata":[{"id":"google","type":"SignupType"}],"rollup":{"l1":"01285019302823526477"}},"object":{"id":"user-3","type":"user"},"edata":{"state":"Update","props":["recoveryEmail","recoveryPhone","userId","id","externalIds","updatedDate","updatedBy"]},"syncts":1573121861125,"@timestamp":"2019-11-07T10:17:41.125Z","flags":{"tv_processed":true,"dd_processed":true},"type":"events","ts":"2019-11-07T10:17:41.118+0000"}
      |
      |""".stripMargin,
    /**
      * User-Id : user-4
      * Edata.state is update
      * framework and block info not present
      */
    """
      {"eid":"AUDIT","ets":1573121861118,"ver":"3.0","mid":"1573121861118.40f9136b-1cc3-458d-a04a-4459606df","actor":{"id":"5609876543234567890987654345678","type":"Request"},"context":{"channel":"01285019302823526477","pdata":{"id":"dev.sunbird.portal","pid":"learner-service","ver":"2.5.0"},"env":"User","did":"user-3","cdata":[{"id":"google","type":"SignupType"}],"rollup":{"l1":"01285019302823526477"}},"object":{"id":"user-4","type":"user"},"edata":{"state":"Update","props":["recoveryEmail","recoveryPhone","userId","id","externalIds","updatedDate","updatedBy"]},"syncts":1573121861125,"@timestamp":"2019-11-07T10:17:41.125Z","flags":{"tv_processed":true,"dd_processed":true},"type":"events","ts":"2019-11-07T10:17:41.118+0000"}
      |
      |""".stripMargin,
    /**
      * UserId = user-5
      * EData state is "Created"
      * User SignupType is "sso"
      * It should able to insert The Map(usersignintype, Validated)
      *
      */
        """
          |{"actor":{"type":"Consumer","id":"89490534-126f-4f0b-82ac-3ff3e49f3468"},"eid":"AUDIT","edata":{"state":"Created","props":["firstName","email","emailVerified","id","userId","createdBy","rootOrgId","channel","userType","roles","phoneVerified","isDeleted","createdDate","status","userName","loginId","externalIds"]},"ver":"3.0","ets":1561739226844,"context":{"channel":"0126684405014528002","pdata":{"pid":"learner-service","ver":"2.0.0","id":"prod.diksha.learning.service"},"env":"User","cdata":[{"type":"User","id":"34881c3a-8b92-4a3c-a982-7f946137cb09"},{"type":"SignupType","id":"sso"},{"type":"Source","id":"android"},{"type":"Request","id":"91f3c280-99c1-11e9-956e-6b6ef71ed575"}],"rollup":{"l1":"0126684405014528002"}},"mid":"1561739226844.e0048ef8-a01e-4780-8c83-e571f28c53c8","object":{"type":"User","id":"user-5"},"syncts":1561739243532,"@timestamp":"2019-06-28T16:27:23.532Z","flags":{"tv_processed":true},"type":"events"}""".stripMargin,

    /**
      * AUDIT Event with object type as content. (not related to user)
      * Props are not defined - Skip Count should incr (skipCount =+ 1)
      */
    """
      |{"eid":"AUDIT","ets":1573121861118,"ver":"3.0","mid":"1573121861118.40f9136b-1cc3-458d-a04a-4459606df","actor":{"id":"5609876543234567890987654345678","type":"Request"},"context":{"channel":"01285019302823526477","pdata":{"id":"dev.sunbird.portal","pid":"learner-service","ver":"2.5.0"},"env":"User","did":"user-5","cdata":[{"id":"25cb0530-7c52-ecb1-cff2-6a14faab7910","type":"UserRole"}],"rollup":{"l1":"01285019302823526477"}},"object":{"id":"do_r8r8rew97we9r8","type":"content"},"edata":{"state":"Updated"},"syncts":1573121861125,"@timestamp":"2019-11-07T10:17:41.125Z","flags":{"tv_processed":true,"dd_processed":true},"type":"events","ts":"2019-11-07T10:17:41.118+0000"}
      |
      |""".stripMargin,

    /**
      * AUDIT Event, But having invalid state(other than create/update)
      * userid= user-6
      */

    """
      |
      |{"eid":"AUDIT","ets":1573121861118,"ver":"3.0","mid":"1573121861118.40f9136b-1cc3-458d-a04a-4459606df","actor":{"id":"5609876543234567890987654345678","type":"Request"},"context":{"channel":"01285019302823526477","pdata":{"id":"dev.sunbird.portal","pid":"learner-service","ver":"2.5.0"},"env":"User","did":"user-5","cdata":[{"id":"25cb0530-7c52-ecb1-cff2-6a14faab7910","type":"UserRole"}],"rollup":{"l1":"01285019302823526477"}},"object":{"id":"user-7","type":"user"},"edata":{"state":"wrongState"},"syncts":1573121861125,"@timestamp":"2019-11-07T10:17:41.125Z","flags":{"tv_processed":true,"dd_processed":true},"type":"events","ts":"2019-11-07T10:17:41.118+0000"}
      |""".stripMargin,
    /**
     * User-Id : user-10
     * schoolname starting with '['
     * Having all the user data
     */
    """
      {"eid":"AUDIT","ets":1573121861118,"ver":"3.0","mid":"1573121861118.40f9136b-1cc3-458d-a04a-4459606df","actor":{"id":"5609876543234567890987654345678","type":"Request"},"context":{"channel":"01285019302823526477","pdata":{"id":"dev.sunbird.portal","pid":"learner-service","ver":"2.5.0"},"env":"User","did":"user-3","cdata":[{"id":"google","type":"SignupType"}],"rollup":{"l1":"01285019302823526477"}},"object":{"id":"user-10","type":"user"},"edata":{"state":"Update","props":["recoveryEmail","recoveryPhone","userId","id","externalIds","updatedDate","updatedBy"]},"syncts":1573121861125,"@timestamp":"2019-11-07T10:17:41.125Z","flags":{"tv_processed":true,"dd_processed":true},"type":"events","ts":"2019-11-07T10:17:41.118+0000"}
      |
      |""".stripMargin

  )

  /**
   * User-Id : user-1
   * Edata.state is update
   * Having pid as sunbird.app
   */
  val telemetryEventWithAppPid: String = """
      |{"actor":{"type":"System","id":"cb0defc7-9f5f-4c0a-bb73-207d34ed2cf0"},"eid":"AUDIT","edata":{"state":"Updated","props":[]},"ver":"3.0","syncts":1631551986826,"@timestamp":"2021-09-13T16:53:06.826Z","ets":1.63155195956E12,"context":{"cdata":[{"id":"teacher","type":"UserRole"},{"type":"Tabs","id":"Library-Course"},{"id":"a9d1d5df-da2e-4006-8495-363cce1ceec2","type":"UserSession"}],"env":"sdk","channel":"0126684405014528002","pdata":{"id":"dev.sunbird.app","pid":"sunbird.app","ver":"4.1.892"},"sid":"a9d1d5df-da2e-4006-8495-363cce1ceec2","did":"575fc6d8a853d2f543e4ef54d57aa3b6cd0110cf","rollup":{"l1":"0126684405014528002"}},"flags":{"ex_processed":true,"pp_validation_processed":true,"pp_duplicate_skipped":true},"mid":"bb429169-f320-4da3-80ad-660093aafe3b","type":"events","object":{"id":"user-1","type":"user","version":"","rollup":{}}}
      |""".stripMargin

  /**
   * User-Id : user-1
   * Edata.state is update
   * Having pid as learner-service
   */
  val telemetryEventWithLearnerPid: String = """
      |{"eid":"AUDIT","ets":1573121861118,"ver":"3.0","mid":"1573121861118.40f9136b-1cc3-458d-a04a-4459606df","actor":{"id":"5609876543234567890987654345678","type":"Request"},"context":{"channel":"01285019302823526477","pdata":{"id":"dev.sunbird.portal","pid":"learner-service","ver":"2.5.0"},"env":"User","did":"user-3","cdata":[{"id":"google","type":"SignupType"}],"rollup":{"l1":"01285019302823526477"}},"object":{"id":"user-1","type":"user"},"edata":{"state":"Update","props":["recoveryEmail","recoveryPhone","userId","id","externalIds","updatedDate","updatedBy"]},"syncts":1573121861125,"@timestamp":"2019-11-07T10:17:41.125Z","flags":{"tv_processed":true,"dd_processed":true},"type":"events","ts":"2019-11-07T10:17:41.118+0000"}
      |""".stripMargin

}