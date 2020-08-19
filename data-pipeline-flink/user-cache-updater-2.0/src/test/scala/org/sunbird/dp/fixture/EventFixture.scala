package org.sunbird.dp.fixture

import java.util
import com.google.gson.Gson
import scala.collection.JavaConversions.mapAsJavaMap

object EventFixture {

  val gson = new Gson()
  val userCacheDataMap3 = mapAsJavaMap(Map("usersignintype" -> "Validated", "usertype" -> "TEACHER"))
  val userCacheData3 = """{"usersignintype":"Validated","usertype":"TEACHER"}"""
  val userCacheData4 = """{"channel":"KV123","phoneverified":"false","createdby":"c8e51123-61a3-454d-beb0-2202450b0096","subject":"[\"English\"]","email":"BJAguqy3GaJECrYqDUPjeducVxa5J9ZsW9A8qc7YHelkV7KbgkCKW10quCbhpgxbh2t4toXC8uXW\\ngiguS+8ucwzbmgPm7q7YSYz26SfpHnzBo/0Vh3TWqr2MOq9LlX6gT6a+wzaAmCWueMEdPmZuRg==","username":"I+CyiN6Bx0GCRm9lkA3xn5uNBm0AODhxeDwJebxxBfuGJ5V2v1R8v1PEQsP+V+y9sAFcM2WtaMLj\\n91hpzBq0PFcQTq6OSPQOm0sySPXTDzyLvm1cKaLwzvJ6fzLLs9nKT6a+wzaAmCWueMEdPmZuRg==","firstname":"A512","framework":"{}","userid":"610bab7d-1450-4e54-bf78-c7c9b14dbc81","usertype":"TEACHER","rootorgid":"0126978705345576967","id":"610bab7d-1450-4e54-bf78-c7c9b14dbc81","language":"","grade":"","roles":"[\"BOOK_REVIEWER\"]","status":"1","webpages":"[]","createddate":"2019-04-11 08:58:16:512+0000","emailverified":"true","isdeleted":"false","location ids":"[\"location-1\",\"location-2\",\"location-3\"]","maskedemail":"a5**@yopmail.com","profilevisibility":"{}","loginid":"I+CyiN6Bx0GCRm9lkA3xnx2W8+QgN39Y0We3KjR98O8hD6YjyoCirIBDsWHGwRf65PY/Cx+pFFK1\\nIz1VinIaKgDnSQwkl7ajzQjjRTzQbKOyHsAXkJgo9I5l7ulEYVXRT6a+wzaAmCWueMEdPmZuRg==","usersignintype":"Self-Signed-In","userlogintype":"Student","state":"Telangana","district":"Hyderabad"}"""
  val userCacheDataMap4 = gson.fromJson(userCacheData4, new util.HashMap[String, String]().getClass)
  val userCacheData9 = mapAsJavaMap(Map("usersignintype" -> "Self-Signed-In", "usertype" -> "student"))
  val userCacheData11 = mapAsJavaMap(Map("usersignintype" -> "Self-Signed-In", "usertype" -> "student"))

  val telemetrEvents: List[String] = List(

    /**
      * UserId = 89490534-126f-4f0b-82ac-3ff3e49f3468
      * EData state is "Created"
      * User SignupType is "sso"
      * It should able to insert The Map(usersignintype, Validated)
      *
      */
    """
      |{"actor":{"type":"Consumer","id":"89490534-126f-4f0b-82ac-3ff3e49f3468"},"eid":"AUDIT","edata":{"state":"Created","props":["firstName","email","emailVerified","id","userId","createdBy","rootOrgId","channel","userType","roles","phoneVerified","isDeleted","createdDate","status","userName","loginId","externalIds"]},"ver":"3.0","ets":1561739226844,"context":{"channel":"0126684405014528002","pdata":{"pid":"learner-service","ver":"2.0.0","id":"prod.diksha.learning.service"},"env":"User","cdata":[{"type":"User","id":"34881c3a-8b92-4a3c-a982-7f946137cb09"},{"type":"SignupType","id":"sso"},{"type":"Source","id":"android"},{"type":"Request","id":"91f3c280-99c1-11e9-956e-6b6ef71ed575"}],"rollup":{"l1":"0126684405014528002"}},"mid":"1561739226844.e0048ef8-a01e-4780-8c83-e571f28c53c8","object":{"type":"User","id":"user-1"},"syncts":1561739243532,"@timestamp":"2019-06-28T16:27:23.532Z","flags":{"tv_processed":true},"type":"events"}""".stripMargin,

    /**
      * UserId = user-2
      * EData state is "create"
      * User SignupType is "google"
      * It should able to insert The Map(usersignintype, Self-Signed-In)
      */


    """
      |{"actor":{"type":"Consumer","id":"89490534-126f-4f0b-82ac-3ff3e49f3468"},"eid":"AUDIT","edata":{"state":"create","props":["firstName","email","emailVerified","id","userId","createdBy","rootOrgId","channel","userType","roles","phoneVerified","isDeleted","createdDate","status","userName","loginId","externalIds"]},"ver":"3.0","ets":1561739226844,"context":{"channel":"0126684405014528002","pdata":{"pid":"learner-service","ver":"2.0.0","id":"prod.diksha.learning.service"},"env":"User","cdata":[{"type":"User","id":"user-2"},{"type":"SignupType","id":"google"},{"type":"Source","id":"android"},{"type":"Request","id":"91f3c280-99c1-11e9-956e-6b6ef71ed575"}],"rollup":{"l1":"0126684405014528002"}},"mid":"1561739226844.e0048ef8-a01e-4780-8c83-e571f28c53c8","object":{"type":"User","id":"user-2"},"syncts":1561739243532,"@timestamp":"2019-06-28T16:27:23.532Z","flags":{"tv_processed":true},"type":"events"}""".stripMargin,

    /**
      * User-Id :user-3
      * Edata.state is update
      *
      */
    """
      |{"eid":"AUDIT","ets":1573121861118,"ver":"3.0","mid":"1573121861118.40f9136b-1cc3-458d-a04a-4459606df","actor":{"id":"5609876543234567890987654345678","type":"Request"},"context":{"channel":"01285019302823526477","pdata":{"id":"dev.sunbird.portal","pid":"learner-service","ver":"2.5.0"},"env":"User","did":"user-3","cdata":[{"id":"25cb0530-7c52-ecb1-cff2-6a14faab7910","type":"SignupType"}],"rollup":{"l1":"01285019302823526477"}},"object":{"id":"user-3", "type":"user"},"edata":{"state":"Update","props":["recoveryEmail","recoveryPhone","userId","id","externalIds","updatedDate","updatedBy"]},"syncts":1573121861125,"@timestamp":"2019-11-07T10:17:41.125Z","flags":{"tv_processed":true,"dd_processed":true},"type":"events","ts":"2019-11-07T10:17:41.118+0000"}
      |
      |""".stripMargin,

    /**
      * UseId-Id: user-4
      * Location id's are defined in the events and also loaded into redis db
      */

    """
      |{"eid":"AUDIT","ets":1573121861118,"ver":"3.0","mid":"1573121861118.40f9136b-1cc3-458d-a04a-4459606df","actor":{"id":"5609876543234567890987654345678","type":"Request"},"context":{"channel":"01285019302823526477","pdata":{"id":"dev.sunbird.portal","pid":"learner-service","ver":"2.5.0"},"env":"User","did":"user-3","cdata":[{"id":"25cb0530-7c52-ecb1-cff2-6a14faab7910","type":"SignupType"}],"rollup":{"l1":"01285019302823526477"}},"object":{"id":"user-4","type":"user"},"edata":{"state":"Updated","props":["recoveryEmail","recoveryPhone","userId","id","externalIds","updatedDate","updatedBy","locationIds"]},"syncts":1573121861125,"@timestamp":"2019-11-07T10:17:41.125Z","flags":{"tv_processed":true,"dd_processed":true},"type":"events","ts":"2019-11-07T10:17:41.118+0000"}
      |
      |""".stripMargin,


    /**
      * User Id - user-5
      * Props are not defined
      */
    """
      |{"eid":"AUDIT","ets":1573121861118,"ver":"3.0","mid":"1573121861118.40f9136b-1cc3-458d-a04a-4459606df","actor":{"id":"5609876543234567890987654345678","type":"Request"},"context":{"channel":"01285019302823526477","pdata":{"id":"dev.sunbird.portal","pid":"learner-service","ver":"2.5.0"},"env":"User","did":"user-5","cdata":[{"id":"25cb0530-7c52-ecb1-cff2-6a14faab7910","type":"UserRole"}],"rollup":{"l1":"01285019302823526477"}},"object":{"id":"user-5","type":"user"},"edata":{"state":"Updated"},"syncts":1573121861125,"@timestamp":"2019-11-07T10:17:41.125Z","flags":{"tv_processed":true,"dd_processed":true},"type":"events","ts":"2019-11-07T10:17:41.118+0000"}
      |
      |""".stripMargin,

    /**
      * User-Id :user-6
      * Edata.state is update
      * Data not present in organisation table
      */
    """
      |{"eid":"AUDIT","ets":1573121861118,"ver":"3.0","mid":"1573121861118.40f9136b-1cc3-458d-a04a-4459606df","actor":{"id":"5609876543234567890987654345678","type":"Request"},"context":{"channel":"01285019302823526477","pdata":{"id":"dev.sunbird.portal","pid":"learner-service","ver":"2.5.0"},"env":"User","did":"user-6","cdata":[{"id":"25cb0530-7c52-ecb1-cff2-6a14faab7910","type":"SignupType"}],"rollup":{"l1":"01285019302823526477"}},"object":{"id":"user-6", "type":"user"},"edata":{"state":"Update","props":["recoveryEmail","recoveryPhone","userId","id","externalIds","updatedDate","updatedBy"]},"syncts":1573121861125,"@timestamp":"2019-11-07T10:17:41.125Z","flags":{"tv_processed":true,"dd_processed":true},"type":"events","ts":"2019-11-07T10:17:41.118+0000"}
      |
      |""".stripMargin,



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
      *
      */

    """
      |
      |{"eid":"AUDIT","ets":1573121861118,"ver":"3.0","mid":"1573121861118.40f9136b-1cc3-458d-a04a-4459606df","actor":{"id":"5609876543234567890987654345678","type":"Request"},"context":{"channel":"01285019302823526477","pdata":{"id":"dev.sunbird.portal","pid":"learner-service","ver":"2.5.0"},"env":"User","did":"user-5","cdata":[{"id":"25cb0530-7c52-ecb1-cff2-6a14faab7910","type":"UserRole"}],"rollup":{"l1":"01285019302823526477"}},"object":{"id":"user-4","type":"user"},"edata":{"state":"wrongState"},"syncts":1573121861125,"@timestamp":"2019-11-07T10:17:41.125Z","flags":{"tv_processed":true,"dd_processed":true},"type":"events","ts":"2019-11-07T10:17:41.118+0000"}
      |""".stripMargin,


    /**
      * AUDIT Event, But having without state id null
      *
      */

    """
      |
      |{"eid":"AUDIT","ets":1573121861118,"ver":"3.0","mid":"1573121861118.40f9136b-1cc3-458d-a04a-4459606df","actor":{"id":"5609876543234567890987654345678","type":"Request"},"context":{"channel":"01285019302823526477","pdata":{"id":"dev.sunbird.portal","pid":"learner-service","ver":"2.5.0"},"env":"User","did":"user-5","cdata":[{"id":"25cb0530-7c52-ecb1-cff2-6a14faab7910","type":"UserRole"}],"rollup":{"l1":"01285019302823526477"}},"object":{"id":"user-4","type":"user"},"edata":{},"syncts":1573121861125,"@timestamp":"2019-11-07T10:17:41.125Z","flags":{"tv_processed":true,"dd_processed":true},"type":"events","ts":"2019-11-07T10:17:41.118+0000"}
      |""".stripMargin,


  /**
    * User-Id :user-9
    * Edata.state is update
    *
    */
  """
    |{"eid":"AUDIT","ets":1573121861118,"ver":"3.0","mid":"1573121861118.40f9136b-1cc3-458d-a04a-4459606df","actor":{"id":"5609876543234567890987654345679","type":"Request"},"context":{"channel":"01285019302823526477","pdata":{"id":"dev.sunbird.portal","pid":"learner-service","ver":"2.5.0"},"env":"User","did":"user-9","cdata":[{"id":"25cb0530-7c52-ecb1-cff2-6a14faab7910","type":"SignupType"}],"rollup":{"l1":"01285019302823526477"}},"object":{"id":"user-9", "type":"user"},"edata":{"state":"Update","props":["recoveryEmail","recoveryPhone","userId","id","externalIds","updatedDate","updatedBy"]},"syncts":1573121861125,"@timestamp":"2019-11-07T10:17:41.125Z","flags":{"tv_processed":true,"dd_processed":true},"type":"events","ts":"2019-11-07T10:17:41.118+0000"}
    |
    |""".stripMargin,

  /**
    * User-Id :user-11
    * Edata.state is update
    *
    */
  """
    |{"eid":"AUDIT","ets":1573121861118,"ver":"3.0","mid":"1573121861118.40f9136b-1cc3-458d-a04a-4459606df","actor":{"id":"5609876543234567890987654345679","type":"Request"},"context":{"channel":"01285019302823526477","pdata":{"id":"dev.sunbird.portal","pid":"learner-service","ver":"2.5.0"},"env":"User","did":"user-9","cdata":[{"id":"25cb0530-7c52-ecb1-cff2-6a14faab7910","type":"SignupType"}],"rollup":{"l1":"01285019302823526477"}},"object":{"id":"user-11", "type":"user"},"edata":{"state":"Update","props":["recoveryEmail","recoveryPhone","userId","id","externalIds","updatedDate","updatedBy"]},"syncts":1573121861125,"@timestamp":"2019-11-07T10:17:41.125Z","flags":{"tv_processed":true,"dd_processed":true},"type":"events","ts":"2019-11-07T10:17:41.118+0000"}
    |
    |""".stripMargin,

  /**
    * User-Id :user-12
    * Edata.state is update
    *
    */
  """
    |{"eid":"AUDIT","ets":1573121861118,"ver":"3.0","mid":"1573121861118.40f9136b-1cc3-458d-a04a-4459606df","actor":{"id":"5609876543234567890987654345678","type":"Request"},"context":{"channel":"01285019302823526477","pdata":{"id":"dev.sunbird.portal","pid":"learner-service","ver":"2.5.0"},"env":"User","did":"user-12","cdata":[{"id":"25cb0530-7c52-ecb1-cff2-6a14faab7910","type":"SignupType"}],"rollup":{"l1":"01285019302823526477"}},"object":{"id":"user-12", "type":"user"},"edata":{"state":"Update","props":["recoveryEmail","recoveryPhone","userId","id","externalIds","updatedDate","updatedBy"]},"syncts":1573121861125,"@timestamp":"2019-11-07T10:17:41.125Z","flags":{"tv_processed":true,"dd_processed":true},"type":"events","ts":"2019-11-07T10:17:41.118+0000"}
    |
    |""".stripMargin


  //      |{"eid":"AUDIT","ets":1573121861118,"ver":"3.0","mid":"1573121861118.40f9136b-1cc3-458d-a04a-4459606dfdd6","actor":{"id":"627a431d-4f5c-4adc-812d-1f01c5588555","type":"User"},"context":{"channel":"01285019302823526477","pdata":{"id":"dev.sunbird.portal","pid":"learner-service","ver":"2.5.0"},"env":"User","did":"2bcfc645e27e64625f7bad6ce282f9d0","cdata":[{"id":"25cb0530-7c52-ecb1-cff2-6a14faab7910","type":"SignupType"}],"rollup":{"l1":"01285019302823526477"}},"object":{"id":"627a431d-4f5c-4adc-812d-1f01c5588555","type":"User"},"edata":{"state":"Create","props":["recoveryEmail","recoveryPhone","userId","id","externalIds","updatedDate","updatedBy"]},"syncts":1573121861125,"@timestamp":"2019-11-07T10:17:41.125Z","flags":{"tv_processed":true,"dd_processed":true},"type":"events","ts":"2019-11-07T10:17:41.118+0000"}
    //      |""".stripMargin,
    //
    //    """
    //      |{"eid":"AUDIT","ets":1.573121861118E12,"ver":"3.0","mid":"1573121861118.40f9136b-1cc3-458d-a04a-4459606df","actor":{"id":"5609876543234567890987654345678","type":"Request"},"context":{"channel":"01285019302823526477","pdata":{"id":"dev.sunbird.portal","pid":"learner-service","ver":"2.5.0"},"env":"User","did":"2bcfc645e27e64625f7bad6ce282f9d0","cdata":[{"id":"25cb0530-7c52-ecb1-cff2-6a14faab7910","type":"SignupType"}],"rollup":{"l1":"01285019302823526477"}},"object":{"id":"5609876543h2fd34h5678jf909876af54345678"},"edata":{"state":"Update","props":["recoveryEmail","recoveryPhone","userId","id","externalIds","updatedDate","updatedBy"]},"syncts":1.573121861125E12,"@timestamp":"2019-11-07T10:17:41.125Z","flags":{"tv_processed":true,"dd_processed":true},"type":"events","ts":"2019-11-07T10:17:41.118+0000"}
    //      |""".stripMargin,
    //
    //    """
    //      |{"eid":"AUDIT","ets":1.573121861118E12,"ver":"3.0","mid":"1573121861118.40f9136b-1cc3-458d-a04a-4459606df","actor":{"id":"5609876543234567890987654345678","type":"Request"},"context":{"channel":"01285019302823526477","pdata":{"id":"dev.sunbird.portal","pid":"learner-service","ver":"2.5.0"},"env":"User","did":"2bcfc645e27e64625f7bad6ce282f9d0","rollup":{"l1":"01285019302823526477"}},"object":{"type":"User","id":"5609876543h2fd34h5678jf909876af54345678"},"edata":{"state":"Create","props":["recoveryEmail","recoveryPhone","userId","id","externalIds","updatedDate","updatedBy"]},"syncts":1.573121861125E12,"@timestamp":"2019-11-07T10:17:41.125Z","flags":{"tv_processed":true,"dd_processed":true},"type":"events","ts":"2019-11-07T10:17:41.118+0000"}
    //      |""".stripMargin,
    //
    //    """
    //      |{"actor":{"type":"Consumer","id":"89490534-126f-4f0b-82ac-3ff3e49f3468"},"eid":"AUDIT","edata":{"state":"Create","props":["firstName","email","emailVerified","id","userId","createdBy","rootOrgId","channel","userType","roles","phoneVerified","isDeleted","createdDate","status","userName","loginId","externalIds"]},"ver":"3.0","ets":1561739226844,"context":{"channel":"0126684405014528002","pdata":{"pid":"learner-service","ver":"2.0.0","id":"prod.diksha.learning.service"},"env":"User","cdata":[{"type":"User","id":"34881c3a-8b92-4a3c-a982-7f946137cb09"},{"type":"SignupType","id":"sso"},{"type":"Source","id":"android"},{"type":"Request","id":"91f3c280-99c1-11e9-956e-6b6ef71ed575"}],"rollup":{"l1":"0126684405014528002"}},"mid":"1561739226844.e0048ef8-a01e-4780-8c83-e571f28c53c8","object":{"type":"User","id":"89490534-126f-4f0b-82ac-3ff3e49f3468"},"syncts":1561739243532,"@timestamp":"2019-06-28T16:27:23.532Z","flags":{"tv_processed":true},"type":"events"}
    //      |""".stripMargin
    //    """
    //      |{"actor":{"type":"Consumer","id":"89490534-126f-4f0b-82ac-3ff3e49f3468"},"eid":"AUDIT","edata":{"state":"Created","props":["firstName","email","emailVerified","id","userId","createdBy","rootOrgId","channel","userType","roles","phoneVerified","isDeleted","createdDate","status","userName","loginId","externalIds"]},"ver":"3.0","ets":1561739226844,"context":{"channel":"0126684405014528002","pdata":{"pid":"learner-service","ver":"2.0.0","id":"prod.diksha.learning.service"},"env":"User","cdata":[{"type":"User","id":"34881c3a-8b92-4a3c-a982-7f946137cb09"},{"type":"SignupType","id":"sso"},{"type":"Source","id":"android"},{"type":"Request","id":"91f3c280-99c1-11e9-956e-6b6ef71ed575"}],"rollup":{"l1":"0126684405014528002"}},"mid":"1561739226844.e0048ef8-a01e-4780-8c83-e571f28c53c8","object":{"type":"User","id":"89490534-126f-4f0b-82ac-3ff3e49f3468"},"syncts":1561739243532,"@timestamp":"2019-06-28T16:27:23.532Z","flags":{"tv_processed":true},"type":"events"}
    //      |""".stripMargin,
    //    """
    //      |{"actor":{"type":"Consumer","id":"89490534-126f-4f0b-82ac-3ff3e49f3468"},"eid":"AUDIT","edata":{"state":"Create","props":["firstName","email","emailVerified","id","userId","createdBy","rootOrgId","channel","userType","roles","phoneVerified","isDeleted","createdDate","status","userName","loginId","externalIds"]},"ver":"3.0","ets":1561739226844,"context":{"channel":"0126684405014528002","pdata":{"pid":"learner-service","ver":"2.0.0","id":"prod.diksha.learning.service"},"env":"User","cdata":[{"type":"User","id":"34881c3a-8b92-4a3c-a982-7f946137cb09"},{"type":"SignupType","id":"sso"},{"type":"Source","id":"android"},{"type":"Request","id":"91f3c280-99c1-11e9-956e-6b6ef71ed575"}],"rollup":{"l1":"0126684405014528002"}},"mid":"1561739226844.e0048ef8-a01e-4780-8c83-e571f28c53c8","object":{"type":"User"},"syncts":1561739243532,"@timestamp":"2019-06-28T16:27:23.532Z","flags":{"tv_processed":true},"type":"events"}
    //      |""".stripMargin,
    //    """
    //      |{"actor":{"type":"System","id":"3b46b4c9-3a10-439a-a2cb-feb5435b3a0d"},"eid":"AUDIT","edata":{"state":"Update","props":["medium","board","grade","syllabus","gradeValue"]},"ver":"3.0","ets":1561739240727,"context":{"pdata":{"pid":"sunbird.app","ver":"2.1.92","id":"prod.diksha.app"},"channel":"505c7c48ac6dc1edc9b08f21db5a571d","env":"sdk","did":"010612971a80a7677d0a3e849ab35cb4a83157de","cdata":[{"type":"UserRole","id":"student"}],"sid":"ea68a05e-0843-4c06-9a84-9b98cd974724"},"mid":"0268860e-76b0-4b4e-b99b-ebf543e7a9d8","object":{"id":"3b46b4c9-3a10-439a-a2cb-feb5435b3a0d","type":"User","version":"","rollup":{}},"syncts":1561739245463,"@timestamp":"2019-06-28T16:27:25.463Z","flags":{"tv_processed":true},"type":"events"}
    //      |""".stripMargin,
    //    """
    //      |{"eid":"AUDIT","ets":1571297660511,"ver":"3.0","mid":"1571297660511.32f5024a-aa30-4c82-abd8-bb8d8914ed2d","actor":{"id":"ef70da5a-bb99-4785-b970-1d6d6ee75aad","type":"User"},"context":{"channel":"01285019302823526477","pdata":{"id":"dev.sunbird.portal","pid":"learner-service","ver":"2.4.0"},"env":"User","did":"bfaf115f65a2086b062735885f4d2f1a","cdata":[{"id":"1b1392bc-39d8-6e47-d7d6-5781a2f1481a","type":"Request"}],"rollup":{"l1":"01285019302823526477"}},"object":{"id":"52226956-61d8-4c1b-b115-c660111866d3","type":"User"},"edata":{"state":"Update","props":["firstName","userId","id","externalIds","locationIds","updatedDate","updatedBy"]},"syncts":1571297660521,"@timestamp":"2019-10-17T07:34:20.521Z","flags":{"tv_processed":true,"dd_processed":true},"type":"events","ts":"2019-10-17T07:34:20.511+0000"}
    //      |""".stripMargin

  )


}