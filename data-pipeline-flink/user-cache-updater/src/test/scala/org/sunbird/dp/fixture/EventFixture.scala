package org.sunbird.dp.fixture

object EventFixture {

  val userCacheData1 = """{"usersignintype":"Anonymous","usertype":"TEACHER"}"""
  val userCacheData2 = """{"channel":"KV123","phoneverified":false,"createdby":"c8e51123-61a3-454d-beb0-2202450b0096","subject":["English"],"email":"BJAguqy3GaJECrYqDUPjeducVxa5J9ZsW9A8qc7YHelkV7KbgkCKW10quCbhpgxbh2t4toXC8uXW\\ngiguS+8ucwzbmgPm7q7YSYz26SfpHnzBo/0Vh3TWqr2MOq9LlX6gT6a+wzaAmCWueMEdPmZuRg==","username":"I+CyiN6Bx0GCRm9lkA3xn5uNBm0AODhxeDwJebxxBfuGJ5V2v1R8v1PEQsP+V+y9sAFcM2WtaMLj\\n91hpzBq0PFcQTq6OSPQOm0sySPXTDzyLvm1cKaLwzvJ6fzLLs9nKT6a+wzaAmCWueMEdPmZuRg==","firstname":"A512","framework":{},"userid":"610bab7d-1450-4e54-bf78-c7c9b14dbc81","usertype":"TEACHER","rootorgid":"0126978705345576967","id":"610bab7d-1450-4e54-bf78-c7c9b14dbc81","language":[],"grade":[],"roles":["BOOK_REVIEWER"],"status":1,"webpages":[],"createddate":"2019-04-11 08:58:16:512+0000","emailverified":true,"isdeleted":false,"locationids":[],"maskedemail":"a5**@yopmail.com","profilevisibility":{},"loginid":"I+CyiN6Bx0GCRm9lkA3xnx2W8+QgN39Y0We3KjR98O8hD6YjyoCirIBDsWHGwRf65PY/Cx+pFFK1\\nIz1VinIaKgDnSQwkl7ajzQjjRTzQbKOyHsAXkJgo9I5l7ulEYVXRT6a+wzaAmCWueMEdPmZuRg==","usersignintype":"Self-Signed-In","userlogintype":"Student","state":"Telangana","district":"Hyderabad"}"""

  val telemetrEvents: List[String] = List(

    """
      |{"eid":"AUDIT","ets":1573121861118,"ver":"3.0","mid":"1573121861118.40f9136b-1cc3-458d-a04a-4459606dfdd6","actor":{"id":"627a431d-4f5c-4adc-812d-1f01c5588555","type":"User"},"context":{"channel":"01285019302823526477","pdata":{"id":"dev.sunbird.portal","pid":"learner-service","ver":"2.5.0"},"env":"User","did":"2bcfc645e27e64625f7bad6ce282f9d0","cdata":[{"id":"25cb0530-7c52-ecb1-cff2-6a14faab7910","type":"SignupType"}],"rollup":{"l1":"01285019302823526477"}},"object":{"id":"627a431d-4f5c-4adc-812d-1f01c5588555","type":"User"},"edata":{"state":"Update","props":["recoveryEmail","recoveryPhone","userId","id","externalIds","updatedDate","updatedBy"]},"syncts":1573121861125,"@timestamp":"2019-11-07T10:17:41.125Z","flags":{"tv_processed":true,"dd_processed":true},"type":"events","ts":"2019-11-07T10:17:41.118+0000"}
      |""".stripMargin
  )


}
